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
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SubClassesSourceContributor implements SourceContributorInterface {

    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull SourceContributorParameter parameter) {
        String sourceParameter = parameter.getSourceParameter();
        if(sourceParameter == null) {
            return Collections.emptyList();
        }

        Collection<LookupElement> lookupElements = new ArrayList<LookupElement>();
        for (PhpClass phpClass : PhpIndex.getInstance(parameter.getProject()).getAllSubclasses(sourceParameter)) {
            lookupElements.add(
                LookupElementBuilder.create(phpClass.getPresentableFQN()).withIcon(phpClass.getIcon())
            );
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

        final Collection<PsiElement> psiElements = new ArrayList<PsiElement>();

        for (PhpClass phpClass : PhpIndex.getInstance(parameter.getProject()).getAllSubclasses(sourceParameter)) {
            if(StringUtils.stripStart(contents, "\\").equalsIgnoreCase(StringUtils.stripStart(phpClass.getPresentableFQN(), "\\"))) {
                psiElements.add(phpClass);
            }
        }

        return psiElements;
    }

    @NotNull
    @Override
    public String getName() {
        return "sub_classes";
    }
}
