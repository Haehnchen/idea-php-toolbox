package de.espend.idea.php.toolbox.dict;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MapObjectBag {

    private final Map<String, Object> parameters;

    public MapObjectBag(@NotNull Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @NotNull
    public Boolean getParameterBool(@NotNull String key, @NotNull Boolean defaultValue) {
        if(!parameters.containsKey(key)) {
            return defaultValue;
        }

        Object o = parameters.get(key);
        return (o instanceof Boolean) ? (Boolean) o : defaultValue;
    }

    @Nullable
    public Boolean getParameterBool(@NotNull String key) {
        if(!parameters.containsKey(key)) {
            return null;
        }

        Object o = parameters.get(key);
        return (o instanceof Boolean) ? (Boolean) o : null;
    }

    @Nullable
    public String getParameterString(@NotNull String key) {
        return getParameterString(key, null);
    }

    @Nullable
    public String getParameterString(@NotNull String key, @Nullable String defaultValue) {
        if(!parameters.containsKey(key)) {
            return defaultValue;
        }

        Object o = parameters.get(key);
        return (o instanceof String) ? (String) o : defaultValue;
    }
}
