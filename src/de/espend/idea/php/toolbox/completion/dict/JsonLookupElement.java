package de.espend.idea.php.toolbox.completion.dict;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.Field;

public class JsonLookupElement extends LookupElement {

    private final JsonRawLookupElement jsonLookupElement;

    public JsonLookupElement(JsonRawLookupElement jsonLookupElement) {
        this.jsonLookupElement = jsonLookupElement;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return jsonLookupElement.getLookupString();
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        JsonParseUtil.decorateLookupElement(presentation, jsonLookupElement);
        presentation.setItemText(jsonLookupElement.getLookupString());
    }

}