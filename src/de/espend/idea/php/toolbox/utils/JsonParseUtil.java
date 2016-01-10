package de.espend.idea.php.toolbox.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import de.espend.idea.php.toolbox.dict.json.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonParseUtil {

    @Nullable
    public static JsonConfigFile getDeserializeConfig(@NotNull String contents) {

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject;
        try {
            jsonObject = jsonParser.parse(contents).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            System.out.println("invalid file");
            return null;
        }

        return getJsonConfigFile(jsonObject);
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
        JsonObject jsonObject;

        try {
            jsonObject = jsonParser.parse(br).getAsJsonObject();
        } catch (JsonIOException e) {
            return null;
        } catch (JsonSyntaxException e) {
            return null;
        }

        return getJsonConfigFile(jsonObject);
    }

    @Nullable
    public static JsonConfigFile getJsonConfigFile(@NotNull JsonObject jsonObject) {
        try {
            return new Gson().fromJson(
                jsonObject,
                new TypeToken<JsonConfigFile>(){}.getType()
            );
        } catch (JsonSyntaxException e) {
            System.out.println("invalid file");
            return null;
        }
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
            return pathname.getName().endsWith("-toolbox.metadata.json");
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

    @NotNull
    public static Collection<JsonSignature> createSignaturesFromStrings(@NotNull Collection<String> signatures) {

        Collection<JsonSignature> jsonSignatures = new ArrayList<JsonSignature>();

        for (String signature : signatures) {
            if(signature == null || StringUtils.isBlank(signature)) {
                continue;
            }

            // foo:car but not foo:11
            Matcher matcher = Pattern.compile("^([\\w\\\\-]+):+(\\d*[a-zA-Z_-][a-zA-Z_\\-\\d]*)$").matcher(signature);
            if (matcher.find()) {
                jsonSignatures.add(JsonSignature.createClassMethod(matcher.group(1), matcher.group(2), 0));
                continue;
            }

            // foo:car:1
            matcher = Pattern.compile("^([\\w\\\\-]+):+(\\w+):+(\\d+)$").matcher(signature);
            if (matcher.find()) {
                try {
                    jsonSignatures.add(JsonSignature.createClassMethod(matcher.group(1), matcher.group(2), Integer.parseInt(matcher.group(3))));
                } catch (NumberFormatException ignored) {
                }
                continue;
            }

            // foo
            matcher = Pattern.compile("^([\\w\\\\-]+)$").matcher(signature);
            if (matcher.find()) {
                jsonSignatures.add(JsonSignature.createFunction(matcher.group(1), 0));
                continue;
            }

            // foo:1
            matcher = Pattern.compile("^([\\w\\\\-]+):+(\\d+)$").matcher(signature);
            if (matcher.find()) {
                try {
                    jsonSignatures.add(JsonSignature.createFunction(matcher.group(1), Integer.parseInt(matcher.group(2))));
                } catch (NumberFormatException ignored) {
                }
                continue;
            }
        }

        return jsonSignatures;
    }
}
