package de.espend.idea.php.toolbox.type;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.json.JsonType;
import de.espend.idea.php.toolbox.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import fr.adrienbrault.idea.symfony2plugin.Symfony2InterfacesUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxTypeProvider implements PhpTypeProvider2 {

    final static char TRIM_KEY = '\u0195';

    @Override
    public char getKey() {
        return '\u0196';
    }

    @Nullable
    @Override
    public String getType(PsiElement e) {

        if (DumbService.getInstance(e.getProject()).isDumb()) {
            return null;
        }

        if(!(e instanceof FunctionReference)) {
            return null;
        }

        Collection<JsonType> types = ExtensionProviderUtil.getTypes(e.getProject());
        if(types.size() == 0) {
            return null;
        }

        // @TODO: pipe provider names
        Set<String> methods = new HashSet<String>();
        Set<String> functions = new HashSet<String>();
        for (JsonType type : types) {

            // default is php, on other language we are workless
            if(type.getLanguage() == null || !type.getLanguage().equalsIgnoreCase("php")) {
                continue;
            }

            for (JsonSignature signature: type.getSignatures()) {

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
                return PhpTypeProviderUtil.getReferenceSignature((FunctionReference) e, TRIM_KEY);
            }

            return null;
        }

        // foo('bar')
        if(functions.contains(((FunctionReference) e).getName())) {
            return PhpTypeProviderUtil.getReferenceSignature((FunctionReference) e, TRIM_KEY);
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {

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

        Collection<PhpNamedElement> elements = new HashSet<PhpNamedElement>();
        elements.addAll(phpNamedElements);

        Set<String> types = new HashSet<String>();

        for (String provider : providers) {

            // @TODO: add per provider interface to resolve type references
            if(provider.equals("Class") || provider.equals("ClassInterface")) {
                elements.addAll(phpIndex.getAnyByFQN(parameter));
                continue;
            }

            if(!providerMap.containsKey(provider)) {
                continue;
            }

            for (JsonRawLookupElement jsonRawLookupElement : providerMap.get(provider)) {
                String type = jsonRawLookupElement.getType();
                if(type == null || StringUtils.isBlank(type)) {
                    continue;
                }

                // internal fully fqn needed by converter since phpstorm9;
                // we normalize it on our side for a unique collection
                if(!type.startsWith("\\")) {
                    type = "\\" + type;
                }

                if(!types.contains(type) && parameter.equals(jsonRawLookupElement.getLookupString())) {
                    elements.addAll(phpIndex.getAnyByFQN(type));
                    types.add(type);
                }
            }

        }

        return elements;
    }

    private Set<String> getProviderNames(@NotNull Project project, @NotNull Function method) {
        Collection<JsonType> types = ExtensionProviderUtil.getTypes(project);

        Set<String> providers = new HashSet<String>();

        Symfony2InterfacesUtil symfony2InterfacesUtil = null;

        String funcName = method.getName();

        // stuff called often; so try reduce calls as possible :)
        for (JsonType type : types) {
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
