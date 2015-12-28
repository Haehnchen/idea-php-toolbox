package de.espend.idea.php.toolbox.extension;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import org.jetbrains.annotations.NotNull;

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
}
