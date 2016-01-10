package de.espend.idea.php.toolbox.remote.provider;

import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface ProviderInterface<T> {

    @NotNull
    String getAlias();

    @Nullable
    ProviderStorageInterface<T> getData(@NotNull String content) throws JsonSyntaxException;

    boolean isValid(@NotNull String content);
}
