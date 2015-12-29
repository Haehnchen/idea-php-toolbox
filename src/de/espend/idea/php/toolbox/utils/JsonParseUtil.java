package de.espend.idea.php.toolbox.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonParseUtil {

    @Nullable
    public static JsonConfigFile getDeserializeConfig(@NotNull InputStream stream) {

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(new InputStreamReader(stream)).getAsJsonObject();

        JsonConfigFile jsonConfig;
        try {
            jsonConfig = new Gson().fromJson(jsonObject, new TypeToken<JsonConfigFile>(){}.getType());
        } catch (JsonSyntaxException e) {
            System.out.println("invalid file");
            return null;
        }

        if(jsonConfig == null) {
            return null;
        }

        return jsonConfig;
    }

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

        JsonConfigFile jsonConfig;
        try {
            jsonConfig = new Gson().fromJson(
                jsonObject,
                new TypeToken<JsonConfigFile>(){}.getType()
            );
        } catch (JsonSyntaxException e) {
            System.out.println(String.format("invalid file %s", file.getAbsolutePath()));
            return null;
        }

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

    public static void decorateLookupElement(@NotNull LookupElementPresentation lookupElement, @NotNull JsonRawLookupElement jsonLookup) {

        if(jsonLookup.getTailText() != null) {
            lookupElement.setTailText(jsonLookup.getTailText(), true);
        }

        if(jsonLookup.getTypeText() != null) {
            lookupElement.setTypeText(jsonLookup.getTypeText());
            lookupElement.setTypeGrayed(true);
        }

        String iconString = jsonLookup.getIcon();
        if(iconString != null) {
            Icon icon = getLookupIconOnString(iconString);
            if(icon != null) {
                lookupElement.setIcon(icon);
            }
        }

    }

    @NotNull
    public static LookupElementBuilder getDecoratedLookupElementBuilder(@NotNull LookupElementBuilder lookupElement, @Nullable JsonRawLookupElement jsonLookup) {
        if(jsonLookup == null) {
            return lookupElement;
        }

        if(jsonLookup.getTailText() != null) {
            lookupElement = lookupElement.withTailText(jsonLookup.getTailText(), true);
        }

        if(jsonLookup.getTypeText() != null) {
            lookupElement = lookupElement.withTypeText(jsonLookup.getTypeText(), true);
        }

        String iconString = jsonLookup.getIcon();
        if(iconString != null) {
            Icon icon = getLookupIconOnString(iconString);
            if(icon != null) {
                lookupElement = lookupElement.withIcon(icon);
            }
        }

        return lookupElement;
    }

    @Nullable
    public static Icon getLookupIconOnString(@NotNull String icon) {

        int endIndex = icon.lastIndexOf(".");
        if(endIndex < 0 || icon.length() - endIndex < 1) {
            return null;
        }

        String className = icon.substring(0, endIndex);

        try {
            Class<?> iconClass = Class.forName(className);
            Field field = iconClass.getDeclaredField(icon.substring(endIndex + 1));
            return ((Icon) field.get(null));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}
