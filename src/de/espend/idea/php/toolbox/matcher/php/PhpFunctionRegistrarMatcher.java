package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpFunctionRegistrarMatcher implements LanguageRegistrarMatcherInterface {

    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {

        PsiElement parent = parameter.getElement().getParent();
        if(!(parent instanceof StringLiteralExpression)) {
            return false;
        }

        Collection<String> signatures = parameter.getRegistrar().getSignatures();
        if(signatures.size() == 0) {
            return false;
        }

        return PhpElementsUtil.isFunctionReference(parent, parameter.getRegistrar().getIndex(), filterFunctionSignatures(signatures));
    }

    public boolean supports(@NotNull FileType fileType) {
        return fileType == PhpFileType.INSTANCE;
    }

    public static String[] filterFunctionSignatures(Collection<String> strings) {
        HashSet<String> filtered = new HashSet<String>(ContainerUtil.filter(strings, new Condition<String>() {
            @Override
            public boolean value(String s) {
                return !s.contains(":");
            }
        }));
        return filtered.toArray(new String[filtered.size()]);
    }

}
