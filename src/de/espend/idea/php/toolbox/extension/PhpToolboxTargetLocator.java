package de.espend.idea.php.toolbox.extension;

import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.navigation.locator.TargetLocatorParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface PhpToolboxTargetLocator {
    @NotNull
    Collection<PsiElement> getTargets(@NotNull TargetLocatorParameter parameter);
}
