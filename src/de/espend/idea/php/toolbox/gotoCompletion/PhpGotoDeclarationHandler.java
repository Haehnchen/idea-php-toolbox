package de.espend.idea.php.toolbox.gotoCompletion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import de.espend.idea.php.toolbox.gotoCompletion.contributor.GlobalStringClassGoto;
import de.espend.idea.php.toolbox.gotoCompletion.contributor.PhpArrayCallbackGotoCompletion;
import de.espend.idea.php.toolbox.matcher.php.docTag.PhpDocTagGotoCompletionContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpGotoDeclarationHandler extends CompletionContributor implements GotoDeclarationHandler {

    private static GotoCompletionContributor[] CONTRIBUTORS = new GotoCompletionContributor[] {
        new PhpArrayCallbackGotoCompletion(),
        new GlobalStringClassGoto(),
        new PhpDocTagGotoCompletionContributor(),
    };

    public PhpGotoDeclarationHandler() {
        for (final GotoCompletionContributor contributor : CONTRIBUTORS) {
            extend(CompletionType.BASIC, contributor.getPattern(), new CompletionProvider<CompletionParameters>() {
                @Override
                protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                    contributor.addCompletions(completionParameters, processingContext, completionResultSet);
                }
            });
        }
    }

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement psiElement, int i, Editor editor) {
        if(psiElement == null) {
            return null;
        }

        Set<PsiElement> psiElements = new HashSet<>();
        for (final GotoCompletionContributor contributor : CONTRIBUTORS) {
            if(!contributor.getPattern().accepts(psiElement)) {
                continue;
            }

            PsiElement[] targets = contributor.getGotoDeclarationTargets(psiElement, i, editor);
            if(targets != null && targets.length > 0) {
                ContainerUtil.addAllNotNull(psiElements, targets);
            }
        }

        return psiElements.toArray(new PsiElement[psiElements.size()]);    }

    @Nullable
    @Override
    public String getActionText(DataContext dataContext) {
        return null;
    }
}
