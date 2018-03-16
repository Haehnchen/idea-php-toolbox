package de.espend.idea.php.toolbox.provider.php;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.PhpIndex;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.provider.presentation.ProviderPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class FunctionProvider implements PhpToolboxProviderInterface {
    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull PhpToolboxCompletionContributorParameter parameter) {
        return PhpIndex.getInstance(parameter.getProject())
            .getAllFunctionNames(PrefixMatcher.ALWAYS_TRUE).stream().map(
                s -> LookupElementBuilder.create(s).withIcon(PhpIcons.FUNCTION)
            )
            .collect(Collectors.toCollection(HashSet::new));
    }

    @NotNull
    @Override
    public Collection<PsiElement> getPsiTargets(@NotNull PhpToolboxDeclarationHandlerParameter parameter) {
        return new HashSet<PsiElement>() {{
            addAll(PhpIndex.getInstance(parameter.getProject()).getFunctionsByFQN(parameter.getContents()));
        }};
    }

    @NotNull
    @Override
    public String getName() {
        return "php.functions";
    }

    @Nullable
    @Override
    public ProviderPresentation getPresentation() {
        return new ProviderPresentation() {
            @Nullable
            @Override
            public Icon getIcon() {
                return PhpIcons.FUNCTION;
            }

            @Nullable
            @Override
            public String getDescription() {
                return "Functions";
            }
        };
    }
}
