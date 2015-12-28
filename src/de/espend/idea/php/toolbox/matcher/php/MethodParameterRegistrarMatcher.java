package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
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

        for (String signature : parameter.getSignatures()) {

            if(!signature.contains(":")) {
                continue;
            }

            String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
            if(split.length != 2) {
                continue;
            }

            if(signature.endsWith("__construct")) {
                if(new MethodMatcher.NewExpressionParameterMatcher(parent, parameter.getRegistrar().getIndex()).withSignature(split[0], split[1]).match() != null) {
                    return true;
                }
            } else {
                if (MethodMatcher.getMatchedSignatureWithDepth(parent, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(split[0], split[1])}, parameter.getRegistrar().getIndex()) != null) {
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
