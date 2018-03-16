package de.espend.idea.php.toolbox.remote.dic;

import de.espend.idea.php.toolbox.remote.http.HttpAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Route implements RouteInterface {

    @NotNull
    final private String path;
    private HttpAction httpAction;

    @NotNull
    final private String method;

    public Route(@NotNull String path, HttpAction httpAction) {
        this(path, "GET", httpAction);
    }

    public Route(@NotNull String path) {
        this(path, "GET");
    }

    public Route(@NotNull String path, @NotNull String method) {
        this.path = path;
        this.method = method;
    }

    public Route(@NotNull String path, @NotNull String method, HttpAction httpAction) {
        this(path, method);
        this.httpAction = httpAction;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getMethod() {
        return method;
    }

    @Nullable
    @Override
    public HttpAction getHttpAction() {
        return httpAction;
    }
}
