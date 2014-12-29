package de.espend.idea.php.toolbox.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.dict.json.JsonType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JsonParseUtil {

    @NotNull
    public static Collection<JsonRegistrar> getRegistrarJsonFromFile(@NotNull File file) {
        return unSerializeRoot(file, "registrar", JsonRegistrar.class);
    }

    @NotNull
    public static Collection<JsonType> getTypeFromJsonFromFile(@NotNull File file) {
        return unSerializeRoot(file, "type", JsonType.class);
    }

    @NotNull
    public static <T> Collection<T> unSerializeRoot(@NotNull File file, String root, final Class<T> clazz) {

        if(!file.exists()) {
            return Collections.emptyList();
        }

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(br).getAsJsonObject();
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!jsonObject.has(root)) {
            return Collections.emptyList();
        }

        Type type = new ListParameterizedType(clazz);

        Collection<T> jsonRegistrars = new Gson().fromJson(jsonObject.get(root), type);
        if(jsonRegistrars != null) {
            return jsonRegistrars;
        }

        // @TODO: debug output for invalid json file
        System.out.println(String.format("invalid file %s", file.getAbsolutePath()));

        return Collections.emptyList();
    }


    private static class ListParameterizedType implements ParameterizedType {

        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {type};
        }

        @Override
        public Type getRawType() {
            return ArrayList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

    }

    public static Map<String, Collection<JsonRawLookupElement>> getProviderJsonRawLookupElements(JsonObject jsonObject) {

        if(!jsonObject.has("providers")) {
            return Collections.emptyMap();
        }

        Collection<JsonProvider> fulls = new Gson().fromJson(
            jsonObject.get("providers"),
            new TypeToken<List<JsonProvider>>(){}.getType()
        );

        if(fulls == null) {
            return Collections.emptyMap();
        }

        Map<String, Collection<JsonRawLookupElement>> jsonLookupElements = new HashMap<String, Collection<JsonRawLookupElement>>();

        for (JsonProvider full: fulls) {

            String providerName = full.getName();
            if(providerName == null || full.getItems().size() == 0) {
                continue;
            }

            Collection<JsonRawLookupElement> lookupItems = full.getItems();
            if(lookupItems != null && lookupItems.size() > 0) {

                if(!jsonLookupElements.containsKey(providerName)) {
                    jsonLookupElements.put(providerName, new ArrayList<JsonRawLookupElement>());
                }

                // merge default values
                JsonRawLookupElement defaults = full.getDefaults();
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

    public static Map<String, Collection<JsonRawLookupElement>> getProviderJsonFromFile(@NotNull File file) {

        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return Collections.emptyMap();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(br).getAsJsonObject();

        return getProviderJsonRawLookupElements(jsonObject);
    }

    public static class JsonFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(".json");
        }
    }

}
