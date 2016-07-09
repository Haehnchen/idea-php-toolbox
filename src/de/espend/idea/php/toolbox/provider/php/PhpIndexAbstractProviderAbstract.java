package de.espend.idea.php.toolbox.provider.php;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderAbstract;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.type.PhpToolboxTypeProviderArguments;
import de.espend.idea.php.toolbox.type.PhpToolboxTypeProviderInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
abstract class PhpIndexAbstractProviderAbstract extends PhpToolboxProviderAbstract implements PhpToolboxTypeProviderInterface {

    @NotNull
    public Collection<LookupElement> getLookupElements(@NotNull PhpToolboxCompletionContributorParameter parameter) {
        PhpIndex instance = PhpIndex.getInstance(parameter.getProject());

        Collection<LookupElement> lookupElements = new ArrayList<>();
        for (String className : getClasses(parameter)) {
            // strip double backslash
            className = className.replaceAll("\\\\+", "\\\\");

            for (PhpClass phpClass : getPhpClassesForLookup(instance, className)) {
                lookupElements.add(
                    LookupElementBuilder.create(phpClass.getPresentableFQN()).withIcon(phpClass.getIcon())
                );
            }
        }

        return lookupElements;
    }

    @NotNull
    protected Collection<String> getClasses(@NotNull PhpToolboxCompletionContributorParameter parameter) {
        return PhpIndex.getInstance(parameter.getProject()).getAllClassNames(parameter.getCompletionResultSet().getPrefixMatcher());
    }

    protected abstract Collection<PhpClass> getPhpClassesForLookup(@NotNull PhpIndex phpIndex, @NotNull String className);

    @NotNull
    protected Collection<PhpClass> resolveParameter(@NotNull PhpIndex phpIndex, @NotNull String parameter) {
        return phpIndex.getAnyByFQN(parameter);
    }

    @NotNull
    @Override
    public Collection<PsiElement> getPsiTargets(@NotNull PhpToolboxDeclarationHandlerParameter parameter) {
        String contents = parameter.getContents();
        if(!StringUtil.startsWithChar(contents, '\\')) {
            contents = "\\" + contents;
        }

        return new ArrayList<>(
            resolveParameter(PhpIndex.getInstance(parameter.getProject()), contents)
        );
    }

    @Nullable
    @Override
    public Collection<PhpNamedElement> resolveParameter(@NotNull PhpToolboxTypeProviderArguments args) {
        String type = args.getParameter().replaceAll("\\\\+", "\\\\");

        return new HashSet<>(
            PhpIndex.getInstance(args.getProject()).getAnyByFQN(type)
        );
    }

}
