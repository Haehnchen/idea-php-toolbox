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
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ArrayKeyValueSignatureRegistrarMatcher implements LanguageRegistrarMatcherInterface {
    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {

        PsiElement psiElement = parameter.getElement().getParent();
        if(!(psiElement instanceof StringLiteralExpression)) {
            return false;
        }

        Collection<JsonSignature> signatures = ContainerUtil.filter(
            parameter.getSignatures(),
            new MyValidJsonSignatureCondition()
        );

        for (JsonSignature signature : signatures) {

            // filtered before; check again for nullable
            if(signature.getArray() == null) {
                continue;
            }

            // @TODO: find ArrayCreationExpression once and check for key
            ArrayCreationExpression arrayCreationExpression = findArrayCreationExpression((StringLiteralExpression) psiElement, signature.getArray());
            if(arrayCreationExpression == null) {
                continue;
            }

            PsiElement parameterList = arrayCreationExpression.getParent();
            if(!(parameterList instanceof ParameterList)) {
                continue;
            }

            // new Foo(['foo' => '<caret>'])
            PsiElement parent = parameterList.getParent();
            if(parent instanceof NewExpression) {
                if (!"__construct".equals(signature.getMethod()) || StringUtils.isBlank(signature.getClassName())) {
                    continue;
                }

                if (new MethodMatcher.NewExpressionParameterMatcher(arrayCreationExpression, signature.getIndex())
                    .withSignature(signature.getClassName(), signature.getMethod())
                    .match() != null
                    ) {
                    return true;
                }

            } else if(parent instanceof MethodReference) {
                if (StringUtils.isBlank(signature.getClassName()) || StringUtils.isBlank(signature.getMethod())) {
                    continue;
                }

                if (MethodMatcher.getMatchedSignatureWithDepth(arrayCreationExpression, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(signature.getClassName(), signature.getMethod())}, signature.getIndex()) != null) {
                    return true;
                }
            } else if(parent instanceof FunctionReference) {
                if(StringUtils.isNotBlank(signature.getFunction())) {
                    if(PhpElementsUtil.isFunctionReference(arrayCreationExpression, signature.getIndex(), signature.getFunction())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    private ArrayCreationExpression findArrayCreationExpression(@NotNull StringLiteralExpression psiElement, @NotNull String key) {

        // value inside array
        // $menu->addChild(array(
        //   'route' => 'foo',
        // ));
        if(PhpPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).accepts(psiElement.getContext())) {
            PsiElement arrayValue = psiElement.getContext();
            if(arrayValue != null) {
                PsiElement arrayHashElement = arrayValue.getContext();
                if(arrayHashElement instanceof ArrayHashElement) {
                    PhpPsiElement arrayKey = ((ArrayHashElement) arrayHashElement).getKey();
                    if(arrayKey instanceof StringLiteralExpression && ((StringLiteralExpression) arrayKey).getContents().equals(key)) {
                        PsiElement arrayCreationExpression = arrayHashElement.getContext();
                        if(arrayCreationExpression instanceof ArrayCreationExpression) {
                            if(!(arrayCreationExpression.getParent() instanceof ParameterList)) {
                                return null;
                            }

                            return (ArrayCreationExpression) arrayCreationExpression;
                        }
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

    private static class MyValidJsonSignatureCondition implements Condition<JsonSignature> {
        @Override
        public boolean value(JsonSignature signature) {
            return
                ContainerConditions.DEFAULT_TYPE_FILTER.value(signature) &&
                signature.getArray() != null && StringUtils.isNotBlank(signature.getArray()) && (
                 (
                     signature.getClassName() != null && StringUtils.isNotBlank(signature.getClassName()) &&
                     signature.getMethod() != null && StringUtils.isNotBlank(signature.getMethod())
                 ) ||
                   signature.getFunction() != null && StringUtils.isNotBlank(signature.getFunction())
                )
                ;
        }
    }
}
