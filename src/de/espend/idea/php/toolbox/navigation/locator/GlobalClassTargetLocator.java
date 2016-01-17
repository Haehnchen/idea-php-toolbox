package de.espend.idea.php.toolbox.navigation.locator;

import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator;
import de.espend.idea.php.toolbox.gotoCompletion.contributor.GlobalStringClassGoto;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class GlobalClassTargetLocator implements PhpToolboxTargetLocator {

    @NotNull
    @Override
    public Collection<PsiElement> getTargets(@NotNull TargetLocatorParameter parameter) {
        return ContainerUtil.newHashSet(
            GlobalStringClassGoto.getPsiElements(parameter.getProject(), parameter.getTarget())
        );
    }
}
