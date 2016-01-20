package de.espend.idea.php.toolbox.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.*;
import com.intellij.util.containers.HashMap;
import com.intellij.util.containers.HashSet;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RegistrarMatchUtil {

    @NotNull
    public static Map<PhpToolboxProviderInterface, Set<JsonRegistrar>> getProviders(@NotNull PsiElement psiElement) {

        Collection<JsonRegistrar> registrars = ExtensionProviderUtil.getRegistrar(psiElement.getProject(), ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class));
        if(registrars.size() == 0) {
            return Collections.emptyMap();
        }

        FileType fileType = psiElement.getContainingFile().getFileType();
        String fileTypeName = fileType.getName();

        Collection<PhpToolboxProviderInterface> providers = null;

        Map<PhpToolboxProviderInterface, Set<JsonRegistrar>> providerMatches = new HashMap<PhpToolboxProviderInterface, Set<JsonRegistrar>>();

        for (JsonRegistrar registrar : registrars) {

            if(registrar == null) {
                continue;
            }

            Collection<JsonSignature> signatures = registrar.getSignatures();
            if(!fileTypeName.equalsIgnoreCase(registrar.getLanguage()) || registrar.getProvider() == null || signatures.size() == 0) {
                continue;
            }

            // init providers
            if(providers == null) {
                providers = ExtensionProviderUtil.getProviders(psiElement.getProject());
            }

            Collection<PhpToolboxProviderInterface> matchedProviders = new ArrayList<PhpToolboxProviderInterface>();
            for (PhpToolboxProviderInterface provider : providers) {
                String name = provider.getName();
                if (name.equals(registrar.getProvider())) {
                    matchedProviders.add(provider);
                }
            }

            if(matchedProviders.size() == 0) {
                continue;
            }

            LanguageMatcherParameter parameter = null;

            for (LanguageRegistrarMatcherInterface matcher : ExtensionProviderUtil.REGISTRAR_MATCHER.getExtensions()) {

                if(!matcher.supports(psiElement.getContainingFile().getFileType())) {
                    continue;
                }

                if(parameter == null) {
                    parameter = new LanguageMatcherParameter(psiElement, registrar);
                }

                if(matcher.matches(parameter)) {
                    for (PhpToolboxProviderInterface matchedProvider : matchedProviders) {
                        if(providerMatches.containsKey(matchedProvider)) {
                            providerMatches.get(matchedProvider).add(registrar);
                        }
                        providerMatches.put(matchedProvider, ContainerUtil.newHashSet(registrar));
                    }

                    break;
                }

            }

        }

        return providerMatches;
    }

}
