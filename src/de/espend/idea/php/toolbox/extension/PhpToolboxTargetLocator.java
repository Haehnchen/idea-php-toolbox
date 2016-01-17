package de.espend.idea.php.toolbox.extension;

import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface PhpToolboxTargetLocator {
    Collection<PsiElement> getTargets(PhpToolboxDeclarationHandlerParameter parameter);
}
