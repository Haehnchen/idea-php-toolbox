package de.espend.idea.php.toolbox.dict.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Model json input
 *
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProvider {

    @Nullable
    private String name;

    @Nullable
    private JsonRawLookupElement defaults;

    @Nullable
    private Collection<JsonRawLookupElement> items = new ArrayList<>();

    @SerializedName("lookup_strings")
    private Collection<String> lookupStrings = new HashSet<>();

    private Collection<JsonRawLookupElement> myItems = null;

    @Nullable
    private JsonProviderSource source;

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public JsonRawLookupElement getDefaults() {
        return defaults;
    }

    synchronized public Collection<JsonRawLookupElement> getItems() {
        if(this.myItems != null) {
            return this.myItems;
        }

        Collection<JsonRawLookupElement> items = new ArrayList<>();
        if(this.items != null && this.items.size() > 0) {
            items.addAll(this.items);
        }

        if(lookupStrings.size() > 0) {
            for (String lookupElement : lookupStrings) {
                items.add(JsonRawLookupElement.create(lookupElement));
            }
        }

        return this.myItems = items;
    }

    @Nullable
    public JsonProviderSource getSource() {
        return source;
    }
}
