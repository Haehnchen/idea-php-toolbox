package de.espend.idea.php.toolbox.dict.json;

import java.util.ArrayList;
import java.util.Collection;

public class JsonProvider {

    private String name;
    private JsonRawLookupElement defaults;
    private Collection<JsonRawLookupElement> items = new ArrayList<JsonRawLookupElement>();

    public String getName() {
        return name;
    }

    public JsonRawLookupElement getDefaults() {
        return defaults;
    }

    public Collection<JsonRawLookupElement> getItems() {
        return items;
    }

}
