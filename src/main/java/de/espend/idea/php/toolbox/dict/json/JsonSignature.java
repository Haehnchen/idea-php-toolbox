package de.espend.idea.php.toolbox.dict.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
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

    @Nullable
    private String field;

    @Nullable
    @SerializedName("array_access")
    private String arrayAccess;

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

    @Nullable
    public String getField() {
        return field;
    }

    @Nullable
    public String getArrayAccess() {
        return arrayAccess;
    }

    @NotNull
    public static JsonSignature createFunction(@NotNull String function, int index) {
        JsonSignature signature = new JsonSignature();
        signature.function = function;
        signature.index = index;
        return signature;
    }

    @NotNull
    public static JsonSignature createClassMethod(@NotNull String clazz, @NotNull String method, int index) {
        JsonSignature signature = new JsonSignature();
        signature.clazz = clazz;
        signature.method = method;
        signature.index = index;
        return signature;
    }

    @NotNull
    public static JsonSignature createArrayAccess(@NotNull String arrayAccess) {
        JsonSignature signature = new JsonSignature();
        signature.arrayAccess = arrayAccess;
        return signature;
    }
}
