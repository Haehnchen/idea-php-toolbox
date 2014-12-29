package de.espend.idea.php.toolbox.dict.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public class JsonRawLookupElement {

    @SerializedName("lookup_string")
    private String lookupString;

    @SerializedName("presentable_text")
    private String presentableText;

    @SerializedName("type_text")
    private String typeText;

    @SerializedName("tail_text")
    private String tailText;

    private String icon;
    private String target;

    public String getLookupString() {
        return lookupString;
    }

    @Nullable
    public String getPresentableText() {
        return presentableText;
    }

    @Nullable
    public String getTypeText() {
        return typeText;
    }

    @Nullable
    public String getTailText() {
        return tailText;
    }

    @Nullable
    public String getIcon() {
        return icon;
    }

    @Nullable
    public String getTarget() {
        return target;
    }

    public void setDefaultOptions(JsonRawLookupElement defaultOptions) {

        if(this.lookupString == null && defaultOptions.getLookupString() != null) {
            this.lookupString = defaultOptions.getIcon();
        }

        if(this.presentableText == null && defaultOptions.getPresentableText() != null) {
            this.presentableText = defaultOptions.getPresentableText();
        }

        if(this.tailText == null && defaultOptions.getTailText() != null) {
            this.tailText = defaultOptions.getTailText();
        }

        if(this.typeText == null && defaultOptions.getTypeText() != null) {
            this.typeText = defaultOptions.getTypeText();
        }

        if(this.icon == null && defaultOptions.getIcon() != null) {
            this.icon = defaultOptions.getIcon();
        }

        if(this.target == null && defaultOptions.getTarget() != null) {
            this.target = defaultOptions.getTarget();
        }

    }
}
