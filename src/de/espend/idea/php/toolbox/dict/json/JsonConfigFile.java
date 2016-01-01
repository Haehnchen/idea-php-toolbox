package de.espend.idea.php.toolbox.dict.json;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonConfigFile {

    private Collection<JsonRegistrar> registrar = new ArrayList<JsonRegistrar>();
    private Collection<JsonProvider> providers = new ArrayList<JsonProvider>();

    public JsonConfigFile() {
    }

    public Collection<JsonRegistrar> getRegistrar() {
        return registrar;
    }

    public Collection<JsonProvider> getProviders() {
        return providers;
    }

}
