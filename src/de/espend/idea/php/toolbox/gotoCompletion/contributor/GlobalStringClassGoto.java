package de.espend.idea.php.toolbox.gotoCompletion.contributor;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.gotoCompletion.GotoCompletionContributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * 'Foo::foo'
 * 'Foo'
 */
public class GlobalStringClassGoto implements GotoCompletionContributor {

    @Override
    public void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
    }

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@NotNull PsiElement psiElement, int i, Editor editor) {
        PsiElement parent = psiElement.getParent();
        if(!(parent instanceof StringLiteralExpression)) {
            return new PsiElement[0];
        }

        // only class names
        String contents = ((StringLiteralExpression) parent).getContents();
        if(contents.length() == 0 || contents.length() > 255) {
            return new PsiElement[0];
        }

        return getPsiElements(psiElement.getProject(), contents);
    }

    @NotNull
    public static PsiElement[] getPsiElements(@NotNull Project project, @NotNull String contents) {

        Collection<PsiElement> psiElements = new HashSet<PsiElement>();

        // DateTime
        // date
        Matcher matcher = Pattern.compile("^([\\w\\\\-]+)$").matcher(contents);
        if (matcher.find()) {
            PhpIndex phpIndex = PhpIndex.getInstance(project);

            ContainerUtil.addAllNotNull(psiElements, phpIndex.getAnyByFQN(contents));
            ContainerUtil.addAllNotNull(psiElements, phpIndex.getFunctionsByName(contents));

            return psiElements.toArray(new PsiElement[psiElements.size()]);
        }

        // DateTime:format
        // DateTime::format
        matcher = Pattern.compile("^([\\w\\\\-]+):+([\\w_\\-]+)$").matcher(contents);
        if (matcher.find()) {
            for (PhpClass phpClass : PhpIndex.getInstance(project).getAnyByFQN(matcher.group(1))) {
                ContainerUtil.addIfNotNull(psiElements, phpClass.findMethodByName(matcher.group(2)));
            }
            return psiElements.toArray(new PsiElement[psiElements.size()]);
        }

        return psiElements.toArray(new PsiElement[psiElements.size()]);
    }

    @NotNull
    @Override
    public ElementPattern<? extends PsiElement> getPattern() {
        return PlatformPatterns.psiElement().withParent(StringLiteralExpression.class);
    }
}
