package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.matcher.php.container.ContainerConditions;
import de.espend.idea.php.toolbox.matcher.php.util.PhpMatcherUtil;
import fr.adrienbrault.idea.symfony2plugin.Symfony2InterfacesUtil;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class VariadicSignatureRegistrarMatcher implements LanguageRegistrarMatcherInterface {

    private static Condition<JsonSignature> VARIADIC_FILTER = new ContainerConditions.TypeSignatureCondition("variadic");

    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {

        Collection<JsonSignature> signatures = ContainerUtil.filter(parameter.getSignatures(), VARIADIC_FILTER);
        if(signatures.size() == 0) {
            return false;
        }

        PsiElement stringLiteral = parameter.getElement().getParent();
        if(!(stringLiteral instanceof StringLiteralExpression)) {
            return false;
        }

        PsiElement parameterList = stringLiteral.getParent();
        if(!(parameterList instanceof ParameterList)) {
            return false;
        }

        PsiElement parent = parameterList.getParent();

        if(parent instanceof MethodReference) {
            Symfony2InterfacesUtil symfony2InterfacesUtil = new Symfony2InterfacesUtil();
            // $f->foo("<caret>");
            for (JsonSignature signature : signatures) {
                if(signature.getClassName() == null || signature.getMethod() == null) {
                    continue;
                }

                if(symfony2InterfacesUtil.isCallTo((MethodReference) parent, signature.getClassName(), signature.getMethod())) {
                    return true;
                }
            }
        } else if(parent instanceof FunctionReference) {
            // foo("<caret>");
            return PhpMatcherUtil.matchesArraySignature(stringLiteral, ContainerUtil.filter(signatures, ContainerConditions.FUNCTION_FILTER));
        } else if(parent instanceof NewExpression) {
            // new Foo("<caret>");
            for (JsonSignature signature : ContainerUtil.filter(signatures, ContainerConditions.CONSTRUCTOR_FILTER)) {
                if(new MethodMatcher.NewExpressionParameterMatcher(stringLiteral, signature.getIndex())
                    .withSignature(signature.getClassName(), signature.getMethod())
                    .match() != null
                    )
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {
        return fileType == PhpFileType.INSTANCE;
    }
}
