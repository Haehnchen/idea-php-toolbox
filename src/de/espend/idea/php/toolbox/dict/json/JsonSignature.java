package de.espend.idea.php.toolbox.dict.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonSignature {

    public static String DEFAULT_TYPE = "default";

    private int index = 0;

    @Nullable
    private String type = DEFAULT_TYPE;

    @Nullable
    private String array;

    @Nullable
    private String function;

    @SerializedName("class")
    private String clazz;

    @Nullable
    private String method;

    public int getIndex() {
        return index;
    }

    @Nullable
    public String getFunction() {
        return function;
    }

    @Nullable
    public String getClassName() {
        return clazz;
    }

    @Nullable
    public String getMethod() {
        return method;
    }

    @Nullable
    public String getArray() {
        return array;
    }

    @Nullable
    public String getType() {
        return type;
    }
}
