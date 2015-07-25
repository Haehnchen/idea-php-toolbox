package de.espend.idea.php.toolbox.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.twig.TwigFileType;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class RegistrarMatchUtil {

    @NotNull
    public static Collection<PhpToolboxProviderInterface> getProviders(@NotNull PsiElement psiElement) {

        Collection<JsonRegistrar> registrars = ExtensionProviderUtil.getRegistrar(psiElement.getProject(), ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class));
        if(registrars.size() == 0) {
            return Collections.emptyList();
        }

        FileType fileType = psiElement.getContainingFile().getFileType();
        String fileTypeName = fileType.getName();

        PhpToolboxProviderInterface[] providers = null;

        Collection<PhpToolboxProviderInterface> providerInterfaces = new ArrayList<PhpToolboxProviderInterface>();

        for (JsonRegistrar registrar : registrars) {

            if(registrar == null) {
                continue;
            }

            Collection<String> signatures = registrar.getSignatures();
            if(!fileTypeName.equalsIgnoreCase(registrar.getLanguage()) || registrar.getProvider() == null || signatures.size() == 0) {
                continue;
            }

            // init providers
            if(providers == null) {
                providers = ExtensionProviderUtil.getProviders(psiElement.getProject());
            }

            Collection<PhpToolboxProviderInterface> matchedProviders = new ArrayList<PhpToolboxProviderInterface>();
            for (PhpToolboxProviderInterface provider : providers) {
                if (provider.getName().equals(registrar.getProvider())) {
                    matchedProviders.add(provider);
                }
            }

            if(matchedProviders.size() == 0) {
                continue;
            }

            if(fileType instanceof PhpFileType && getPhpLanguageProvider(psiElement, registrar, signatures)) {
                providerInterfaces.addAll(matchedProviders);
            }

            if(fileType instanceof TwigFileType && getTwigLanguageProvider(psiElement, registrar, signatures)) {
                providerInterfaces.addAll(matchedProviders);
            }

        }

        return providerInterfaces;
    }

    private static boolean getTwigLanguageProvider(PsiElement psiElement, JsonRegistrar registrar, Collection<String> signatures) {
        return TwigUtil.getPrintBlockFunctionPattern(signatures.toArray(new String[signatures.size()])).accepts(psiElement);
    }

    private static boolean getPhpLanguageProvider(PsiElement psiElement, JsonRegistrar registrar, Collection<String> signatures) {

        PsiElement parent = psiElement.getParent();
        if(!(parent instanceof StringLiteralExpression)) {
            return false;
        }

        for (String signature : signatures) {
            if(signature.contains(":")) {

                String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
                if(split.length == 2) {

                    if(signature.endsWith("__construct")) {
                        if(new MethodMatcher.NewExpressionParameterMatcher(parent, registrar.getIndex()).withSignature(split[0], split[1]).match() != null) {
                            return true;
                        }
                    } else {
                        if (MethodMatcher.getMatchedSignatureWithDepth(parent, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(split[0], split[1])}, registrar.getIndex()) != null) {
                            return true;
                        }

                    }
                }
            } else {

                if(PhpElementsUtil.isFunctionReference(parent, registrar.getIndex(), signature)) {
                    return true;
                }

            }
        }

        return false;
    }

}
