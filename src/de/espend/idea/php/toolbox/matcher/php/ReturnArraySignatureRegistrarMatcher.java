package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpReturn;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import fr.adrienbrault.idea.symfony2plugin.Symfony2InterfacesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ReturnArraySignatureRegistrarMatcher implements LanguageRegistrarMatcherInterface {
    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {

        PsiElement parent = parameter.getElement().getParent();
        if(!(parent instanceof StringLiteralExpression)) {
            return false;
        }

        PsiElement phpReturn = parent.getParent();
        if(!(phpReturn instanceof PhpReturn)) {
            return false;
        }

        Function function = PsiTreeUtil.getParentOfType(phpReturn, Function.class);
        if(function == null) {
            return false;
        }

        if(function instanceof Method) {
            Symfony2InterfacesUtil symfony2InterfacesUtil = new Symfony2InterfacesUtil();

            for (String signature : parameter.getSignatures()) {
                String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
                if(split.length != 2) {
                    continue;
                }

                if(symfony2InterfacesUtil.isCallTo((Method) function, split[0], split[1])) {
                    return true;
                }
            }

            return false;
        }

        return Arrays.asList(PhpFunctionRegistrarMatcher.filterFunctionSignatures(parameter.getSignatures())).contains(function.getName());
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {
        return fileType == PhpFileType.INSTANCE;
    }
}
