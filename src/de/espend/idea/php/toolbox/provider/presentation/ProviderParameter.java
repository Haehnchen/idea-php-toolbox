package de.espend.idea.php.toolbox.provider.presentation;

import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProviderParameter {
    public enum TYPE {
        INTEGER, BOOLEAN, STRING
    };

    @NotNull
    private final String name;

    @NotNull
    private final TYPE type;

    public ProviderParameter(@NotNull String name, @NotNull TYPE type) {
        this.name = name;
        this.type = type;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public TYPE getType() {
        return type;
    }
}
