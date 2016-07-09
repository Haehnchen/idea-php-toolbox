package de.espend.idea.php.toolbox.dict.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProvider {

    private String name;
    private JsonRawLookupElement defaults;
    private Collection<JsonRawLookupElement> items = new ArrayList<>();

    @SerializedName("lookup_strings")
    private Collection<String> lookupStrings = new HashSet<>();

    private Collection<JsonRawLookupElement> myItems = null;

    @Nullable
    private JsonProviderSource source;

    public String getName() {
        return name;
    }

    public JsonRawLookupElement getDefaults() {
        return defaults;
    }

    public Collection<JsonRawLookupElement> getItems() {
        if(this.myItems != null) {
            return this.myItems;
        }

        this.myItems = new ArrayList<>(items);

        if(lookupStrings.size() > 0) {
            for (String lookupElement : lookupStrings) {
                this.myItems.add(JsonRawLookupElement.create(lookupElement));
            }
        }

        return this.myItems;
    }

    @Nullable
    public JsonProviderSource getSource() {
        return source;
    }

}
