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
import de.espend.idea.php.toolbox.symfony.util.MethodMatcher;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ArrayKeySignatureRegistrarMatcher implements LanguageRegistrarMatcherInterface {

    private static final Condition<JsonSignature> ARRAY_KEY_FILTER = new Condition<JsonSignature>() {
        @Override
        public boolean value(JsonSignature signature) {
            return "array_key".equals(signature.getType());
        }
    };

    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {

        PsiElement psiElement = parameter.getElement().getParent();
        if(!(psiElement instanceof StringLiteralExpression)) {
            return false;
        }

        ArrayCreationExpression arrayCreationExpression = findArrayCreationExpression((StringLiteralExpression) psiElement);
        if(arrayCreationExpression == null) {
            return false;
        }

        PsiElement parameterList = arrayCreationExpression.getParent();
        if(!(parameterList instanceof ParameterList)) {
            return false;
        }

        PsiElement parent = parameterList.getParent();

        Collection<JsonSignature> signatures = ContainerUtil.filter(
            parameter.getSignatures(),
            ARRAY_KEY_FILTER
        );

        if(signatures.size() == 0) {
            return false;
        }

        if(parent instanceof MethodReference) {
            // $this->foo(["<caret>"]);
            for (JsonSignature signature : signatures) {

                // we need valid array
                if(!"array_key".equals(signature.getType()) ||
                    StringUtils.isBlank(signature.getMethod())||
                    StringUtils.isBlank(signature.getClassName())
                  )
                {
                    continue;
                }

                if (MethodMatcher.getMatchedSignatureWithDepth(arrayCreationExpression, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(signature.getClassName(), signature.getMethod())}, signature.getIndex()) != null) {
                    return true;
                }

            }
        } else if(parent instanceof FunctionReference) {
            // foo(["<caret>"]);
            return PhpMatcherUtil.matchesArraySignature(arrayCreationExpression, ContainerUtil.filter(signatures, ContainerConditions.ARRAY_KEY_AND_FUNCTION_FILTER));
        } else if(parent instanceof NewExpression) {
            // new Foo(["<caret>"]);
            for (JsonSignature signature : ContainerUtil.filter(signatures, ContainerConditions.CONSTRUCTOR_FILTER)) {
                if(new MethodMatcher.NewExpressionParameterMatcher(arrayCreationExpression, signature.getIndex())
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

    @Nullable
    private ArrayCreationExpression findArrayCreationExpression(@NotNull StringLiteralExpression psiElement) {

        // value inside array
        // $menu->addChild(array(
        //   'foo' => '',
        // ));
        if(PhpPatterns.psiElement(PhpElementTypes.ARRAY_KEY).accepts(psiElement.getContext())) {
            PsiElement arrayKey = psiElement.getContext();
            if(arrayKey != null) {
                PsiElement arrayHashElement = arrayKey.getContext();
                if(arrayHashElement instanceof ArrayHashElement) {
                    PsiElement arrayCreationExpression = arrayHashElement.getContext();
                    if(arrayCreationExpression instanceof ArrayCreationExpression) {
                        PsiElement parameterList = arrayCreationExpression.getParent();
                        if(parameterList instanceof ParameterList) {
                            return (ArrayCreationExpression) arrayCreationExpression;
                        }
                    }

                }
            }

        }

        // on array creation key dont have value, so provide completion here also
        // array('foo' => 'bar', '<test>')
        if(PhpPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).accepts(psiElement.getContext())) {
            PsiElement arrayKey = psiElement.getContext();
            if(arrayKey != null) {
                PsiElement arrayCreationExpression = arrayKey.getContext();
                if(arrayCreationExpression instanceof ArrayCreationExpression) {
                    PsiElement parameterList = arrayCreationExpression.getParent();
                    if(parameterList instanceof ParameterList) {
                        return (ArrayCreationExpression) arrayCreationExpression;
                    }
                }

            }

        }

        return null;
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {
        return fileType == PhpFileType.INSTANCE;
    }
}
