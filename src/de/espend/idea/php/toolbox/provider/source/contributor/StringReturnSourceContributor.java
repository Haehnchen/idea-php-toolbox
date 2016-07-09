package de.espend.idea.php.toolbox.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.provider.source.SourceContributorDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.provider.source.SourceContributorParameter;
import de.espend.idea.php.toolbox.provider.source.contributor.utils.ReturnSourceUtil;
import de.espend.idea.php.toolbox.symfony.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class StringReturnSourceContributor implements SourceContributorInterface {

    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull SourceContributorParameter parameter) {

        Collection<LookupElement> lookupElements = new ArrayList<>();

        ReturnVisitor visitor = null;

        String sourceParameter = parameter.getSourceParameter();
        if(sourceParameter == null) {
            return Collections.emptyList();
        }

        for (Pair<String, String> s : ReturnSourceUtil.extractParameter(sourceParameter)) {
            if(visitor == null) {
                visitor = new MyReturnLookupVisitor(parameter.getJsonProvider(), lookupElements);
            }

            visitReturnElements(parameter.getProject(), s.getFirst(), s.getSecond(), visitor);
        }

        return lookupElements;
    }

    @NotNull
    @Override
    public Collection<PsiElement> getPsiTargets(@NotNull SourceContributorDeclarationHandlerParameter parameter) {

        String contents = parameter.getHandlerParameter().getContents();
        if(StringUtils.isBlank(contents)) {
            return Collections.emptyList();
        }

        String sourceParameter = parameter.getSourceParameter();
        if(sourceParameter == null) {
            return Collections.emptyList();
        }

        final Collection<PsiElement> psiElements = new ArrayList<>();

        ReturnVisitor visitor = null;

        for (Pair<String, String> s : ReturnSourceUtil.extractParameter(sourceParameter)) {
            if(visitor == null) {
                visitor = new MyReturnTargetVisitor(contents, psiElements);
            }

            visitReturnElements(parameter.getProject(), s.getFirst(), s.getSecond(), visitor);
        }

        return psiElements;
    }

    private void visitReturnElements(@NotNull Project project, @NotNull String className, @NotNull String methodName, @NotNull ReturnVisitor visitor) {

        for (PhpClass phpClass : PhpIndex.getInstance(project).getAllSubclasses(className)) {

            Method method = phpClass.findOwnMethodByName(methodName);
            if(method == null) {
                continue;
            }

            String string = PhpElementsUtil.getMethodReturnAsString(method);
            if(string != null) {
                visitor.visit(method, string);
            }
        }
    }

    private interface ReturnVisitor {
        void visit(@NotNull Method method, @NotNull String contents);
    }

    @NotNull
    @Override
    public String getName() {
        return "return";
    }

    private static class MyReturnTargetVisitor implements ReturnVisitor {
        @NotNull
        private final String contents;
        private final Collection<PsiElement> psiElements;

        public MyReturnTargetVisitor(@NotNull String contents, @NotNull Collection<PsiElement> psiElements) {
            this.contents = contents;
            this.psiElements = psiElements;
        }

        @Override
        public void visit(@NotNull Method method, @NotNull String returnValue) {
            if (contents.equals(returnValue)) {
                psiElements.add(method);
            }
        }
    }

    private static class MyReturnLookupVisitor implements ReturnVisitor {
        @NotNull
        private final JsonProvider provider;
        private final Collection<LookupElement> lookupElements;

        public MyReturnLookupVisitor(@NotNull JsonProvider provider, @NotNull Collection<LookupElement> lookupElements) {
            this.provider = provider;
            this.lookupElements = lookupElements;
        }

        @Override
        public void visit(@NotNull Method method, @NotNull String contents) {
            LookupElementBuilder decoratedLookupElementBuilder = ReturnSourceUtil.buildLookupElement(method, contents, provider.getDefaults());
            lookupElements.add(decoratedLookupElementBuilder);
        }
    }
}
