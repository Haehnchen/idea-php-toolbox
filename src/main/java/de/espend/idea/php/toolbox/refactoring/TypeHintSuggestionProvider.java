package de.espend.idea.php.toolbox.refactoring;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.NameSuggestionProvider;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.refactoring.PhpNameSuggestionUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TypeHintSuggestionProvider implements NameSuggestionProvider
{

    private static final String[] TRIM_STRINGS = new String[] {
        "interface", "abstract", "extension", "test", "resolver",
        "factory", "loader", "provider", "manager", "service",
        "logger", "plugin",
    };

    @Nullable
    @Override
    public SuggestedNameInfo getSuggestedNames(@NotNull PsiElement psiElement, PsiElement psiElement1, @NotNull Set<String> set) {

        if(!(psiElement instanceof Parameter)) {
            return null;
        }

        List<String> filter = ContainerUtil.filter(PhpNameSuggestionUtil.variableNameByType((Parameter) psiElement, psiElement.getProject(), false), s -> !StringUtil.containsChar(s, '\\'));

        if(filter.size() == 0) {
            return null;
        }

        for (String item : filter) {

            for(String end: TRIM_STRINGS) {

                // ending
                if(item.toLowerCase().endsWith(end)) {
                    item = item.substring(0, item.length() - end.length());
                }

                // remove starting
                if(item.toLowerCase().startsWith(end)) {
                    item = WordUtils.uncapitalize(item.substring(end.length()));
                }
            }

            if(StringUtils.isNotBlank(item)) {
                set.add(item);
            }
        }

        return null;
    }
}
