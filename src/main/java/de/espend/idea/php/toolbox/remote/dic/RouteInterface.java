package de.espend.idea.php.toolbox.remote.dic;

import de.espend.idea.php.toolbox.remote.http.HttpAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RouteInterface {

    @NotNull
    String getPath();

    @NotNull
    String getMethod();

    @Nullable
    HttpAction getHttpAction();

}
