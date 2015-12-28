package de.espend.idea.php.toolbox.dict.json;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProvider {

    private String name;
    private JsonRawLookupElement defaults;
    private Collection<JsonRawLookupElement> items = new ArrayList<JsonRawLookupElement>();

    @Nullable
    private JsonProviderSource source;

    public String getName() {
        return name;
    }

    public JsonRawLookupElement getDefaults() {
        return defaults;
    }

    public Collection<JsonRawLookupElement> getItems() {
        return items;
    }

    @Nullable
    public JsonProviderSource getSource() {
        return source;
    }

}
