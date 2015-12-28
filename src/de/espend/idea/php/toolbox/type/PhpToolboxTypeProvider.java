package de.espend.idea.php.toolbox.type;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonType;
import de.espend.idea.php.toolbox.type.utils.PhpTypeProviderUtil;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
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

        Collection<JsonType> types = ExtensionProviderUtil.getTypes(e.getProject(), ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class));
        if(types.size() == 0) {
            return null;
        }

        // @TODO: pipe provider names
        Set<String> methods = new HashSet<String>();
        for (JsonType type : types) {

            // default is php, on other language we are workless
            if(type.getLanguage() == null || !type.getLanguage().equalsIgnoreCase("php")) {
                continue;
            }

            for (String signature: type.getSignatures()) {
                if(signature.contains(":")) {
                    String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
                    if(split.length == 2) {
                        methods.add(split[1]);
                    }
                }
            }
        }

        // container calls are only on "get" methods
        if(!(e instanceof MethodReference) || !PhpElementsUtil.isMethodWithFirstStringOrFieldReference(e, methods.toArray(new String[methods.size()]))) {
            return null;
        }

        return PhpTypeProviderUtil.getReferenceSignature((MethodReference) e, TRIM_KEY);

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
        Collection<? extends PhpNamedElement> phpNamedElementCollections = phpIndex.getBySignature(originalSignature, null, 0);
        if(phpNamedElementCollections.size() == 0) {
            return Collections.emptySet();
        }

        // get first matched item
        PhpNamedElement phpNamedElement = phpNamedElementCollections.iterator().next();
        if(!(phpNamedElement instanceof Method)) {
            return phpNamedElementCollections;
        }

        parameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, parameter);
        if(parameter == null) {
            return phpNamedElementCollections;
        }

        Map<String, Collection<JsonRawLookupElement>> providerMap = ExtensionProviderUtil.getProviders(project, ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class));
        Set<String> providers = getProviderNames(project);

        for (String provider : providers) {

            if(!providerMap.containsKey(provider)) {
                continue;
            }

            for (JsonRawLookupElement jsonRawLookupElement : providerMap.get(provider)) {
                if(parameter.equals(jsonRawLookupElement.getLookupString()) && jsonRawLookupElement.getType() != null) {
                    return PhpIndex.getInstance(project).getAnyByFQN(jsonRawLookupElement.getType());
                }
            }

        }

        return phpNamedElementCollections;

    }

    private Set<String> getProviderNames(Project project) {

        Collection<JsonType> types = ExtensionProviderUtil.getTypes(project, ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class));
        Set<String> providers = new HashSet<String>();

        // @TODO: add method instance check
        for (JsonType type : types) {
            for (String signature: type.getSignatures()) {
                if(signature.contains(":")) {
                    String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
                    if(split.length == 2) {
                        providers.add(type.getProvider());
                    }
                }
            }
        }

        return providers;
    }
}
