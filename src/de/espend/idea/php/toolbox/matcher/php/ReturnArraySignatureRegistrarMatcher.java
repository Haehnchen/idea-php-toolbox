package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.PhpReturn;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.matcher.php.util.PhpMatcherUtil;
import org.jetbrains.annotations.NotNull;

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

        PsiElement arrayValue = parent.getParent();
        if(arrayValue == null || arrayValue.getNode().getElementType() != PhpElementTypes.ARRAY_VALUE) {
            return false;
        }

        PsiElement arrayCreation = arrayValue.getParent();
        if(!(arrayCreation instanceof ArrayCreationExpression)) {
            return false;
        }

        PsiElement phpReturn = arrayCreation.getParent();
        if(!(phpReturn instanceof PhpReturn)) {
            return false;
        }

        return PhpMatcherUtil.isMachingReturnArray(parameter.getSignatures(), phpReturn);
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {
        return fileType == PhpFileType.INSTANCE;
    }
}
