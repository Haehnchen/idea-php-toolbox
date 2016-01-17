package de.espend.idea.php.toolbox.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.twig.TwigFileType;
import com.jetbrains.twig.TwigTokenTypes;
import de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxGotoDeclarationHandler implements GotoDeclarationHandler {

    public static final ExtensionPointName<PhpToolboxTargetLocator> EXTENSIONS = new ExtensionPointName<PhpToolboxTargetLocator>(
        "de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator"
    );

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int i, Editor editor) {

        FileType fileType = psiElement.getContainingFile().getFileType();
        if(!(fileType instanceof PhpFileType) && !(fileType instanceof TwigFileType)) {
            return new PsiElement[0];
        }

        String selectedItem = null;
        if(psiElement.getNode().getElementType() == TwigTokenTypes.STRING_TEXT) {
            // twig language
            selectedItem = psiElement.getText();
        } else {
            // php language

            PsiElement stringLiteral = psiElement.getParent();
            if(stringLiteral instanceof StringLiteralExpression) {
                selectedItem = ((StringLiteralExpression) stringLiteral).getContents();
            }
        }

        if(StringUtils.isBlank(selectedItem)) {
            return new PsiElement[0];
        }

        PhpToolboxDeclarationHandlerParameter parameter = new PhpToolboxDeclarationHandlerParameter(psiElement, selectedItem, fileType);

        Collection<PsiElement> targets = new HashSet<PsiElement>();
        for (PhpToolboxTargetLocator locator : EXTENSIONS.getExtensions()) {
            targets.addAll(locator.getTargets(parameter));
        }

        return targets.toArray(new PsiElement[targets.size()]);
    }

    @Nullable
    @Override
    public String getActionText(DataContext dataContext) {
        return null;
    }

}
