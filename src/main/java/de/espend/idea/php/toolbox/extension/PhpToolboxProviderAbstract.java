package de.espend.idea.php.toolbox.extension;

import de.espend.idea.php.toolbox.provider.presentation.ProviderPresentation;
import de.espend.idea.php.toolbox.provider.presentation.SimpleProviderPresentation;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public abstract class PhpToolboxProviderAbstract implements PhpToolboxProviderInterface {
    @Nullable
    @Override
    public ProviderPresentation getPresentation() {
        return SimpleProviderPresentation.getInstance();
    }
}
