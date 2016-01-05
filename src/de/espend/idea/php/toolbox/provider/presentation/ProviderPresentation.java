package de.espend.idea.php.toolbox.provider.presentation;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public abstract class ProviderPresentation {

    @Nullable
    public Icon getIcon() {
        return null;
    }

    @Nullable
    public String getDescription() {
        return null;
    }
}

