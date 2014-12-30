package de.espend.idea.php.toolbox.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JsonParseUtil {

    @Nullable
    public static JsonConfigFile getDeserializeConfig(@NotNull File file) {

        if(!file.exists()) {
            return null;
        }

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            return null;
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(br).getAsJsonObject();

        JsonConfigFile jsonConfig = new Gson().fromJson(
            jsonObject,
            new TypeToken<JsonConfigFile>(){}.getType()
        );

        if(jsonConfig == null) {
            // @TODO: debug output for invalid json file
            System.out.println(String.format("invalid file %s", file.getAbsolutePath()));
            return null;
        }

        return jsonConfig;
    }

    public static Map<String, Collection<JsonRawLookupElement>> getProviderJsonRawLookupElements(Collection<JsonProvider> jsonProviders) {

        Map<String, Collection<JsonRawLookupElement>> jsonLookupElements = new HashMap<String, Collection<JsonRawLookupElement>>();

        for (JsonProvider provider: jsonProviders) {

            String providerName = provider.getName();
            if(providerName == null || provider.getItems().size() == 0) {
                continue;
            }

            Collection<JsonRawLookupElement> lookupItems = provider.getItems();
            if(lookupItems != null && lookupItems.size() > 0) {

                if(!jsonLookupElements.containsKey(providerName)) {
                    jsonLookupElements.put(providerName, new ArrayList<JsonRawLookupElement>());
                }

                // merge default values
                JsonRawLookupElement defaults = provider.getDefaults();
                if(defaults != null) {
                    for(JsonRawLookupElement element: lookupItems) {
                        element.setDefaultOptions(defaults);
                    }
                }

                jsonLookupElements.get(providerName).addAll(lookupItems);
            }

        }

        return jsonLookupElements;
    }

    public static class JsonFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".json");
        }
    }

}
