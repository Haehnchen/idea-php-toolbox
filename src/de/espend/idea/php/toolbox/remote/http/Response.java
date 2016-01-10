package de.espend.idea.php.toolbox.remote.http;

import com.sun.net.httpserver.HttpExchange;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class Response {

    private int status = 200;
    private String content = "";
    private Map<String, String> headers = new HashMap<String, String>();

    public Response() {
    }

    public Response(@NotNull String content) {
        this.content = content;
    }

    protected String getContent() {
        return this.content;
    }

    public void send(@NotNull HttpExchange exchange) {
        String content = getContent();

        try {
            for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
                exchange.getResponseHeaders().add(entry.getKey(), entry.getValue());
            }

            exchange.sendResponseHeaders(getStatus(), content.length());
            OutputStream os = exchange.getResponseBody();

            os.write(content.getBytes());
            os.close();
        } catch (IOException ignored) {
        }
    }


    public int getStatus() {
        return status;
    }

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    public Response setContent(@NotNull String content) {
        this.content = content;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(@NotNull String key, @NotNull String value) {
        this.headers.put(key, value);
    }
}
