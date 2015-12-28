package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import fr.adrienbrault.idea.symfony2plugin.util.MethodMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

        Collection<String> signatures = parameter.getSignatures();

        for (String signature : signatures) {
            String[] split = signature.replaceAll("(:)\\1", "$1").split(":");
            if(split.length != 2) {
                continue;
            }

            String[] split1 = split[1].split("\\.");
            if(split1.length != 2) {
                continue;
            }

            ArrayCreationExpression arrayCreationExpression = findArrayCreationExpression((StringLiteralExpression) psiElement, split1[1]);
            if(arrayCreationExpression == null) {
                return false;
            }

            if (MethodMatcher.getMatchedSignatureWithDepth(arrayCreationExpression, new MethodMatcher.CallToSignature[]{new MethodMatcher.CallToSignature(split[0], split1[0])}, parameter.getRegistrar().getIndex()) != null) {
                return true;
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
}
