package de.espend.idea.php.toolbox.extension;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.provider.presentation.ProviderPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface PhpToolboxProviderInterface {

    @NotNull
    Collection<LookupElement> getLookupElements(@NotNull PhpToolboxCompletionContributorParameter parameter);

    @NotNull
    Collection<PsiElement> getPsiTargets(@NotNull PhpToolboxDeclarationHandlerParameter parameter);

    @NotNull
    String getName();

    @Nullable
    ProviderPresentation getPresentation();
}
