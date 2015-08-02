package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ArrayKeySignatureRegistrarMatcher implements LanguageRegistrarMatcherInterface {
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

        Collection<String> signatures = parameter.getSignatures();

        if(parent instanceof MethodReference) {
            for (String signature : signatures) {
                String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
                if(split.length != 2) {
                    continue;
                }

                if (MethodMatcher.getMatchedSignatureWithDepth(arrayCreationExpression, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(split[0], split[1])}, parameter.getRegistrar().getIndex()) != null) {
                    return true;
                }

            }
        } else if(parent instanceof FunctionReference) {
            return PhpElementsUtil.isFunctionReference(arrayCreationExpression, parameter.getRegistrar().getIndex(), PhpFunctionRegistrarMatcher.filterFunctionSignatures(signatures));
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
                        PsiElement parent = parameterList.getParent();
                        if(parent instanceof FunctionReference) {
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
}
