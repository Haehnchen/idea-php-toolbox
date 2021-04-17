package de.espend.idea.php.toolbox.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.provider.source.SourceContributorDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.provider.source.SourceContributorParameter;
import de.espend.idea.php.toolbox.provider.source.contributor.utils.ReturnSourceUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ArrayReturnSourceContributor implements SourceContributorInterface {

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

    private void visitReturnElements(@NotNull Project project, @NotNull String className, @NotNull String methodName, @NotNull final ReturnVisitor visitor) {

        for (PhpClass phpClass : PhpIndex.getInstance(project).getAllSubclasses(className)) {

            final Method method = phpClass.findOwnMethodByName(methodName);
            if(method == null) {
                continue;
            }

            method.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {

                    if(!(element instanceof PhpReturn)) {
                        super.visitElement(element);
                        return;
                    }

                    PsiElement firstChild = ((PhpReturn) element).getFirstPsiChild();
                    if(!(firstChild instanceof ArrayCreationExpression)) {
                        return;
                    }

                    for (PsiElement arrayValue : firstChild.getChildren()) {

                        if(arrayValue.getNode().getElementType() != PhpElementTypes.ARRAY_VALUE) {
                            continue;
                        }

                        PsiElement stringLiteral = arrayValue.getFirstChild();
                        if(!(stringLiteral instanceof StringLiteralExpression)) {
                            continue;
                        }

                        String contents = ((StringLiteralExpression) stringLiteral).getContents();
                        if(StringUtils.isNotBlank(contents)) {
                            visitor.visit(method, (StringLiteralExpression) stringLiteral, contents);
                        }

                    }

                    super.visitElement(element);
                }
            });

        }
    }

    private interface ReturnVisitor {
        void visit(@NotNull Method method, @NotNull StringLiteralExpression psiElement, @NotNull String contents);
    }

    @NotNull
    @Override
    public String getName() {
        return "return_array";
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
        public void visit(@NotNull Method method, @NotNull StringLiteralExpression psiElement, @NotNull String returnValue) {
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
        public void visit(@NotNull Method method, @NotNull StringLiteralExpression psiElement, @NotNull String contents) {
            LookupElementBuilder decoratedLookupElementBuilder = ReturnSourceUtil.buildLookupElement(method, contents, provider.getDefaults());
            lookupElements.add(decoratedLookupElementBuilder);
        }
    }
}
