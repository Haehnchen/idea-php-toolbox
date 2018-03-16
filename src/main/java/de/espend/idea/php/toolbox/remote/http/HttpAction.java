package de.espend.idea.php.toolbox.remote.http;

import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.Response;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface HttpAction {
    @NotNull
    Response handle(@NotNull RequestMatcher requestMatcher);
}
