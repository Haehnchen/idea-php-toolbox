package de.espend.idea.php.toolbox.remote.http;

import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonResponse extends Response {

    private Object object;

    public JsonResponse(@NotNull Map<String, String> map) {
        this.object = map;
    }

    public JsonResponse(@NotNull Object object, int status) {
        this(object);
        this.setStatus(status);
    }

    public JsonResponse(@NotNull Object object) {
        super("");
        this.object = object;
        this.addHeader("Content-Type", "application/json");
    }

    protected String getContent() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(object);
    }
}
