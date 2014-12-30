package de.espend.idea.php.toolbox.dict.json;

import java.util.ArrayList;
import java.util.Collection;

public class JsonConfigFile {

    private Collection<JsonType> types = new ArrayList<JsonType>();
    private Collection<JsonRegistrar> registrar = new ArrayList<JsonRegistrar>();
    private Collection<JsonProvider> providers = new ArrayList<JsonProvider>();

    public JsonConfigFile() {

    }

    public Collection<JsonType> getTypes() {
        return types;
    }

    public Collection<JsonRegistrar> getRegistrar() {
        return registrar;
    }

    public Collection<JsonProvider> getProviders() {
        return providers;
    }

}
