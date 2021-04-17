package de.espend.idea.php.toolbox.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.twig.TwigFileType;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.utils.RegistrarMatchUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ToolboxCompletionContributor extends CompletionContributor {

    public ToolboxCompletionContributor() {

        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                PsiElement position = completionParameters.getPosition();

                FileType fileType = position.getContainingFile().getFileType();
                if(!(fileType instanceof PhpFileType) && !(fileType instanceof TwigFileType)) {
                    return;
                }

                Map<PhpToolboxProviderInterface, Set<JsonRegistrar>> providers = RegistrarMatchUtil.getProviders(position);
                if(providers.size() == 0) {
                    return;
                }

                PhpToolboxCompletionContributorParameter parameter = null;
                for (Map.Entry<PhpToolboxProviderInterface, Set<JsonRegistrar>> provider : providers.entrySet()) {

                    if(parameter == null) {
                        parameter = new PhpToolboxCompletionContributorParameter(
                            completionParameters,
                            processingContext,
                            completionResultSet,
                            provider.getValue()
                        );
                    }

                    completionResultSet.addAllElements(provider.getKey().getLookupElements(parameter));
                }

            }
        });

    }

}
