package de.espend.idea.php.toolbox.gotoCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface GotoCompletionContributor<V extends CompletionParameters> {
    void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext context, @NotNull CompletionResultSet resultSet);

    @Nullable
    PsiElement[] getGotoDeclarationTargets(@NotNull PsiElement psiElement, int i, Editor editor);

    @NotNull
    ElementPattern<? extends PsiElement> getPattern();
}
