package de.espend.idea.php.toolbox.remote.util;

import de.espend.idea.php.toolbox.remote.dic.Route;
import de.espend.idea.php.toolbox.remote.dic.RouteInterface;
import de.espend.idea.php.toolbox.remote.httpHandler.InfoActionHandler;
import de.espend.idea.php.toolbox.remote.httpHandler.project.JsonProjectStorageHandler;
import de.espend.idea.php.toolbox.remote.httpHandler.project.ProjectHttpAction;
import de.espend.idea.php.toolbox.remote.httpHandler.project.ProjectIndexHttpAction;
import de.espend.idea.php.toolbox.remote.httpHandler.project.ProjectProviderPostHttpAction;
import de.espend.idea.php.toolbox.remote.httpHandler.project.ProjectStorageClearHttpAction;
import de.espend.idea.php.toolbox.remote.provider.JsonToolboxProvider;
import de.espend.idea.php.toolbox.remote.provider.ProviderInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RemoteUtil {

    private static ProviderInterface[] PROVIDERS = new ProviderInterface[] {
        new JsonToolboxProvider(),
    };

    public static ProviderInterface[] getProviders() {
        return PROVIDERS;
    }

    @Nullable
    public static ProviderInterface getProvider(@NotNull final String provider) {
        for (ProviderInterface providerInterface : PROVIDERS) {
            if(provider.equals(providerInterface.getAlias())) {
                return providerInterface;
            }
        }

        return null;
    }

    @NotNull
    public static Collection<RouteInterface> getRoutes() {
        Collection<RouteInterface> routes = new ArrayList<>();

        routes.add(new Route("/", new InfoActionHandler()));
        routes.add(new Route("/projects", new ProjectIndexHttpAction()));
        routes.add(new Route("/projects/{project}", new ProjectHttpAction()));
        routes.add(new Route("/projects/{project}/clear", new ProjectStorageClearHttpAction()));
        routes.add(new Route("/projects/{project}/{provider}", "POST", new ProjectProviderPostHttpAction()));
        routes.add(new Route("/projects/{project}/json-debug", new JsonProjectStorageHandler()));

        return routes;
    }

}
