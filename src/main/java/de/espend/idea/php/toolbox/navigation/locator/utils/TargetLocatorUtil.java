package de.espend.idea.php.toolbox.navigation.locator.utils;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.navigation.locator.TargetLocatorParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class TargetLocatorUtil {

    public static final ExtensionPointName<PhpToolboxTargetLocator> EXTENSIONS = new ExtensionPointName<>(
        "de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator"
    );

    @NotNull
    public static Collection<PsiElement> getTargetsBySignature(@NotNull PhpToolboxDeclarationHandlerParameter parameter, @NotNull String target) {

        Collection<PsiElement> psiElements = new HashSet<>();
        for (PhpToolboxTargetLocator locator : EXTENSIONS.getExtensions()) {
            psiElements.addAll(
                locator.getTargets(new TargetLocatorParameter(parameter, target))
            );
        }

        return psiElements;
    }

}
