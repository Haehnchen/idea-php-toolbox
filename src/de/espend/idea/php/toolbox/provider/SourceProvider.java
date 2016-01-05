package de.espend.idea.php.toolbox.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderAbstract;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.provider.source.SourceContributorDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.provider.source.SourceContributorParameter;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SourceProvider extends PhpToolboxProviderAbstract {

    private final String name;
    private final Collection<JsonProvider> sourceProviders;

    public SourceProvider(@NotNull String name, @NotNull Collection<JsonProvider> sourceProviders) {
        this.name = name;
        this.sourceProviders = sourceProviders;
    }

    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull PhpToolboxCompletionContributorParameter parameter) {

        Collection<LookupElement> lookupElements = new ArrayList<LookupElement>();
        SourceContributorParameter params = null;
        for (JsonProvider sourceProvider : sourceProviders) {
            if(sourceProvider.getSource() == null || sourceProvider.getSource().getContributor() == null) {
                continue;
            }

            SourceContributorInterface sourceContributor = ExtensionProviderUtil.getSourceContributor(sourceProvider.getSource().getContributor());
            if(sourceContributor == null) {
                continue;
            }

            if(params == null) {
                params = new SourceContributorParameter(parameter, sourceProvider);
            }

            lookupElements.addAll(sourceContributor.getLookupElements(params));
        }

        return lookupElements;
    }

    @NotNull
    @Override
    public Collection<PsiElement> getPsiTargets(@NotNull PhpToolboxDeclarationHandlerParameter parameter) {

        Collection<PsiElement> targets = new ArrayList<PsiElement>();
        SourceContributorDeclarationHandlerParameter params = null;
        for (JsonProvider sourceProvider : sourceProviders) {
            if(sourceProvider.getSource() == null || sourceProvider.getSource().getContributor() == null) {
                continue;
            }

            SourceContributorInterface sourceContributor = ExtensionProviderUtil.getSourceContributor(sourceProvider.getSource().getContributor());
            if(sourceContributor == null) {
                continue;
            }

            if(params == null) {
                params = new SourceContributorDeclarationHandlerParameter(parameter, sourceProvider);
            }

            targets.addAll(sourceContributor.getPsiTargets(params));
        }

        return targets;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

}
