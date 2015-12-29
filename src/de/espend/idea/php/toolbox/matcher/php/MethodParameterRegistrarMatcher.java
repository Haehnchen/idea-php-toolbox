package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class MethodParameterRegistrarMatcher implements LanguageRegistrarMatcherInterface {
    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {

        PsiElement parent = parameter.getElement().getParent();
        if(!(parent instanceof StringLiteralExpression)) {
            return false;
        }

        for (JsonSignature signature : parameter.getSignatures()) {

            if(StringUtils.isBlank(signature.getMethod()) || StringUtils.isBlank(signature.getClassName())) {
                continue;
            }

            if(signature.getMethod().equals("__construct")) {
                if(new MethodMatcher.NewExpressionParameterMatcher(parent, signature.getIndex()).withSignature(signature.getClassName(), signature.getMethod()).match() != null) {
                    return true;
                }
            } else {
                if (MethodMatcher.getMatchedSignatureWithDepth(parent, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(signature.getClassName(), signature.getMethod())}, signature.getIndex()) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean supports(@NotNull FileType fileType) {
        return fileType == PhpFileType.INSTANCE;
    }
}
