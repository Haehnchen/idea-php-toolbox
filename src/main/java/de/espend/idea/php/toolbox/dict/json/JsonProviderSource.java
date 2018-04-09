package de.espend.idea.php.toolbox.dict.json;

import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProviderSource {

    @Nullable
    private String contributor;

    @Nullable
    private String parameter;

    @Nullable
    public String getParameter() {
        return parameter;
    }

    @Nullable
    public String getContributor() {
        return contributor;
    }
}
