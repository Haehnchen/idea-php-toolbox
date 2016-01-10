package de.espend.idea.php.toolbox.remote.http;

import com.intellij.openapi.project.Project;
import com.sun.net.httpserver.HttpExchange;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class RequestMatcher {

    @NotNull
    private final HttpExchange xchg;

    @NotNull
    private final Map<String, String> vars;

    public RequestMatcher(@NotNull HttpExchange xchg, @NotNull Map<String, String> vars) {
        this.xchg = xchg;
        this.vars = vars;
    }

    @NotNull
    public HttpExchange getHttpExchange() {
        return xchg;
    }

    @Nullable
    public String getVar(@NotNull String var) {
        return vars.containsKey(var) ? vars.get(var) : null;
    }

    @NotNull
    public Map<String, String> getVars() {
        return vars;
    }

    public void send(Response response) {
        try {
            HttpExchangeUtil.sendResponse(xchg, response);
        } catch (IOException ignored) {
        }
    }
}
