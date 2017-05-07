package de.espend.idea.php.toolbox.type;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
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

        // @TODO: pipe provider names
        Set<String> methods = new HashSet<>();
        Set<String> functions = new HashSet<>();
        for (JsonRegistrar type : types) {
            for (JsonSignature signature: ContainerUtil.filter(new ArrayList<>(type.getSignatures()), ContainerConditions.RETURN_TYPE_TYPE)) {
                if(signature.getFunction() != null && StringUtils.isNotBlank(signature.getFunction())) {
                    functions.add(signature.getFunction());
                }

                if(signature.getClassName() != null && signature.getMethod() != null && StringUtils.isNotBlank(signature.getClassName()) && StringUtils.isNotBlank(signature.getMethod())) {
                    methods.add(signature.getMethod());
                }
            }
        }

        // $this->foo('bar')
        // Foo::app('bar')
        if(e instanceof MethodReference) {
            if(methods.contains(((FunctionReference) e).getName())) {
                String referenceSignature =  PhpTypeProviderUtil.getReferenceSignature((FunctionReference) e, TRIM_KEY);
                if(referenceSignature != null) {
                    return new PhpType().add("#" + this.getKey() + referenceSignature);
                }
            }

            return null;
        }

        // foo('bar')
        if(functions.contains(((FunctionReference) e).getName())) {
            String referenceSignature = PhpTypeProviderUtil.getReferenceSignature((FunctionReference) e, TRIM_KEY);
            if(referenceSignature != null) {
                return new PhpType().add("#" + this.getKey() + referenceSignature);
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Set<String> visited, int depth, Project project) {

        // get back our original call
        // since phpstorm 7.1.2 we need to validate this
        int endIndex = expression.lastIndexOf(TRIM_KEY);
        if(endIndex == -1) {
            return Collections.emptySet();
        }

        String originalSignature = expression.substring(0, endIndex);
        String parameter = expression.substring(endIndex + 1);

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

        parameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, parameter);
        if(parameter == null) {
            return phpNamedElements;
        }

        Map<String, Collection<JsonRawLookupElement>> providerMap = ExtensionProviderUtil.getProviders(
            project,
            ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class)
        );

        Set<String> providers = getProviderNames(project, (Function) phpNamedElement);

        Collection<PhpNamedElement> elements = new HashSet<>();
        elements.addAll(phpNamedElements);

        for (String providerName : providers) {

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

    private Set<String> getProviderNames(@NotNull Project project, @NotNull Function method) {
        Collection<JsonRegistrar> types = ExtensionProviderUtil.getTypes(project);

        Set<String> providers = new HashSet<>();

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
                            providers.add(type.getProvider());
                            break;
                        }
                    }
                } else if(StringUtils.isNotBlank(sig.getFunction()) && funcName.equals(sig.getFunction())) {
                    // function condition
                    providers.add(type.getProvider());
                    break;
                }
            }
        }

        return providers;
    }
}
