package de.espend.idea.php.toolbox.type;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider3;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.matcher.php.container.ContainerConditions;
import de.espend.idea.php.toolbox.symfony.Symfony2InterfacesUtil;
import de.espend.idea.php.toolbox.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.openapi.util.Pair.pair;
import static java.util.stream.Collectors.toMap;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxTypeProvider implements PhpTypeProvider3 {

    final static char TRIM_KEY = '\u0195';

    @Override
    public char getKey() {
        return '\u0196';
    }

    @Nullable
    @Override
    public PhpType getType(PsiElement e) {
        if(!(e instanceof FunctionReference)) {
            return null;
        }

        Collection<JsonRegistrar> types = ExtensionProviderUtil.getTypes(e.getProject());
        if(types.size() == 0) {
            return null;
        }

        String name = ((FunctionReference) e).getName();

        // @TODO: pipe provider names
        Set<Integer> indexes = new HashSet<>();
        for (JsonRegistrar type : types) {
            for (JsonSignature signature: ContainerUtil.filter(new ArrayList<>(type.getSignatures()), ContainerConditions.RETURN_TYPE_TYPE)) {
                // $this->foo('bar')
                // Foo::app('bar')
                if(e instanceof MethodReference && StringUtils.isNotBlank(signature.getClassName()) && StringUtils.isNotBlank(signature.getMethod()) && signature.getMethod().equals(name)) {
                    indexes.add(signature.getIndex());
                }
                // foo('bar')
                else if(StringUtils.isNotBlank(signature.getFunction()) && signature.getFunction().equals(name)) {
                    indexes.add(signature.getIndex());
                }
            }
        }

        if(indexes.isEmpty()) {
            return null;
        }

        String referenceSignature = PhpTypeProviderUtil.getReferenceSignature((FunctionReference) e, TRIM_KEY, indexes);
        if(referenceSignature != null) {
            return new PhpType().add("#" + this.getKey() + referenceSignature);
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {

        // get back our original call
        // since phpstorm 7.1.2 we need to validate this
        int endIndex = expression.lastIndexOf(String.valueOf(TRIM_KEY) + TRIM_KEY);
        if(endIndex == -1) {
            return Collections.emptySet();
        }

        String originalSignature = expression.substring(0, endIndex);
        String parametersSignature = expression.substring(endIndex + 2);

        // search for called method
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        Collection<? extends PhpNamedElement> phpNamedElements = phpIndex.getBySignature(originalSignature, null, 0);
        if(phpNamedElements.size() == 0) {
            return Collections.emptySet();
        }

        // get first matched item
        PhpNamedElement phpNamedElement = phpNamedElements.iterator().next();
        if(!(phpNamedElement instanceof Function)) {
            return phpNamedElements;
        }

        List<String> parts = Arrays.asList(parametersSignature.split(String.valueOf(TRIM_KEY)));
        Map<Integer, String> parameters = parts.stream()
            .collect(toMap(
                part -> Integer.parseInt(part.substring(0, 1)),
                part -> PhpTypeProviderUtil.getResolvedParameter(phpIndex, part.substring(1))
            ));
        parameters.values().removeIf(Objects::isNull);

        if (parameters.isEmpty()) {
            return phpNamedElements;
        }

        Map<String, Collection<JsonRawLookupElement>> providerMap = ExtensionProviderUtil.getProviders(
            project,
            ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class)
        );

        Set<Pair<String, Integer>> providers = getProviderNames(project, (Function) phpNamedElement);

        Collection<PhpNamedElement> elements = new HashSet<>();
        elements.addAll(phpNamedElements);

        for (Pair<String, Integer> providerPair : providers) {
            String providerName = providerPair.first;
            Integer index = providerPair.second;

            String parameter = parameters.get(index);
            if (parameter == null) {
                continue;
            }

            PhpToolboxProviderInterface provider = ExtensionProviderUtil.getProvider(project, providerName);
            if(!(provider instanceof PhpToolboxTypeProviderInterface)) {
                continue;
            }

            PhpToolboxTypeProviderArguments args = new PhpToolboxTypeProviderArguments(
                project,
                parameter,
                providerMap.containsKey(providerName) ? providerMap.get(providerName) : Collections.emptyList()
            );

            Collection<PhpNamedElement> items = ((PhpToolboxTypeProviderInterface) provider).resolveParameter(args);
            if(items != null && items.size() > 0) {
                elements.addAll(items);
            }
        }

        return elements;
    }

    private Set<Pair<String, Integer>> getProviderNames(@NotNull Project project, @NotNull Function method) {
        Collection<JsonRegistrar> types = ExtensionProviderUtil.getTypes(project);

        Set<Pair<String, Integer>> providers = new HashSet<>();

        Symfony2InterfacesUtil symfony2InterfacesUtil = null;

        String funcName = method.getName();

        // stuff called often; so try reduce calls as possible :)
        for (JsonRegistrar type : types) {
            for (JsonSignature sig: type.getSignatures()) {
                // method or function must be equal
                if(method instanceof Method) {
                    if(sig.getMethod() != null && funcName.equals(sig.getMethod()) && StringUtils.isNotBlank(sig.getClassName())) {
                        if(symfony2InterfacesUtil == null) {
                            symfony2InterfacesUtil = new Symfony2InterfacesUtil();
                        }

                        if (symfony2InterfacesUtil.isCallTo((Method) method, sig.getClassName(), sig.getMethod())) {
                            providers.add(pair(type.getProvider(), sig.getIndex()));
                            break;
                        }
                    }
                } else if(StringUtils.isNotBlank(sig.getFunction()) && funcName.equals(sig.getFunction())) {
                    // function condition
                    providers.add(pair(type.getProvider(), sig.getIndex()));
                    break;
                }
            }
        }

        return providers;
    }
}
