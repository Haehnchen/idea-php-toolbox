package de.espend.idea.php.toolbox.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.provider.source.SourceContributorParameter;
import de.espend.idea.php.toolbox.provider.source.SourceContributorDeclarationHandlerParameter;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StringReturnSourceContributor implements SourceContributorInterface {

    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull SourceContributorParameter parameter) {

        Collection<LookupElement> lookupElements = new ArrayList<LookupElement>();

        ReturnVisitor visitor = null;

        String[] sourceParameter = parameter.getSource().getParameter().split(",");
        for (String s : sourceParameter) {

            // \\FOO:Bar
            String[] split = s.trim().replaceAll("(:)\\1", "$1").split(":");
            if(split.length < 2) {
                continue;
            }

            if(visitor == null) {
                visitor = new MyReturnLookupVisitor(lookupElements);
            }

            visitReturnElements(parameter.getProject(), split[0], split[1], visitor);
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


        final Collection<PsiElement> psiElements = new ArrayList<PsiElement>();

        ReturnVisitor visitor = null;

        String[] sourceParameter = parameter.getSource().getParameter().split(",");
        for (String s : sourceParameter) {

            // \\FOO:Bar
            String[] split = s.trim().replaceAll("(:)\\1", "$1").split(":");
            if(split.length < 2) {
                continue;
            }

            if(visitor == null) {

                visitor = new MyReturnTargetVisitor(contents, psiElements);
            }

            visitReturnElements(parameter.getProject(), split[0], split[1], visitor);
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

    private static interface ReturnVisitor {
        public void visit(@NotNull Method method, @NotNull String contents);
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
        private final Collection<LookupElement> lookupElements;

        public MyReturnLookupVisitor(@NotNull Collection<LookupElement> lookupElements) {
            this.lookupElements = lookupElements;
        }

        @Override
        public void visit(@NotNull Method method, @NotNull String contents) {
            lookupElements.add(LookupElementBuilder.create(contents));
        }
    }
}
