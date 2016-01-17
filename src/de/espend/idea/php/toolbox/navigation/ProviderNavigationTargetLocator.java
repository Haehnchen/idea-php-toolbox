package de.espend.idea.php.toolbox.navigation;

import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.PhpToolboxTargetLocator;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.utils.RegistrarMatchUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProviderNavigationTargetLocator implements PhpToolboxTargetLocator {

    public Collection<PsiElement> getTargets(PhpToolboxDeclarationHandlerParameter parameter) {

        Collection<PhpToolboxProviderInterface> providers = RegistrarMatchUtil.getProviders(parameter.getPsiElement());
        if(providers.size() == 0) {
            return Collections.emptyList();
        }

        Collection<PsiElement> targets = new ArrayList<PsiElement>();
        for (PhpToolboxProviderInterface provider : providers) {
            targets.addAll(provider.getPsiTargets(parameter));
        }

        return targets;
    }
}
