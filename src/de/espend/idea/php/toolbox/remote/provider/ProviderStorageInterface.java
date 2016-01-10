package de.espend.idea.php.toolbox.remote.provider;

import com.google.gson.JsonSyntaxException;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface ProviderStorageInterface<T> {
    T getData() throws JsonSyntaxException;
}
