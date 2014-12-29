package de.espend.idea.php.toolbox.utils;


import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementTypes;

public class TwigUtil {

    /**
     * Check for {{ include('|')  }}
     *
     * @param functionName twig function name
     */
    public static ElementPattern<PsiElement> getPrintBlockFunctionPattern(String... functionName) {
        //noinspection unchecked
        return PlatformPatterns
            .psiElement(TwigTokenTypes.STRING_TEXT)
            .withParent(
                PlatformPatterns.psiElement(TwigElementTypes.PRINT_BLOCK)
            )
            .afterLeafSkipping(
                PlatformPatterns.or(
                    PlatformPatterns.psiElement(TwigTokenTypes.LBRACE),
                    PlatformPatterns.psiElement(PsiWhiteSpace.class),
                    PlatformPatterns.psiElement(TwigTokenTypes.WHITE_SPACE),
                    PlatformPatterns.psiElement(TwigTokenTypes.SINGLE_QUOTE),
                    PlatformPatterns.psiElement(TwigTokenTypes.DOUBLE_QUOTE)
                ),
                PlatformPatterns.psiElement(TwigTokenTypes.IDENTIFIER).withText(PlatformPatterns.string().oneOf(functionName))
            )
            .withLanguage(TwigLanguage.INSTANCE);
    }


}
