package de.espend.idea.php.toolbox.remote.provider;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonToolboxProvider implements ProviderInterface<JsonConfigFile> {

    @NotNull
    @Override
    public String getAlias() {
        return "php-toolbox-json";
    }

    @Nullable
    @Override
    public ProviderStorageInterface<JsonConfigFile> getData(final @NotNull String content) throws JsonSyntaxException {
        return () -> new Gson().fromJson(content, JsonConfigFile.class);
    }

    @Override
    public boolean isValid(@NotNull String content) {
        return true;
    }
}
