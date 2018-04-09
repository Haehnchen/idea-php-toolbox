package de.espend.idea.php.toolbox.provider.presentation;

import de.espend.idea.php.toolbox.PhpToolboxIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SimpleProviderPresentation extends ProviderPresentation {

    private static SimpleProviderPresentation instance;

    private SimpleProviderPresentation() {}

    @Nullable
    @Override
    public Icon getIcon() {
        return PhpToolboxIcons.TOOLBOX;
    }

    @NotNull
    public static SimpleProviderPresentation getInstance() {
        return instance != null ? instance : (instance = new SimpleProviderPresentation());
    }
}
