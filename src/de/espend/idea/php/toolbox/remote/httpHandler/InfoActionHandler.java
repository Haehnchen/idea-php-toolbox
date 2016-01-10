package de.espend.idea.php.toolbox.remote.httpHandler;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import de.espend.idea.php.toolbox.remote.http.HttpAction;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.dic.RouteInterface;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.InfoDic;
import de.espend.idea.php.toolbox.remote.httpHandler.project.ProjectIndexHttpAction;
import de.espend.idea.php.toolbox.remote.util.RemoteUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class InfoActionHandler implements HttpAction {
    @NotNull
    @Override
    public Response handle(@NotNull RequestMatcher requestMatcher) {
        InfoDic info = new InfoDic();

        info.fullVersion = ApplicationInfo.getInstance().getFullVersion();
        info.apiVersion = ApplicationInfo.getInstance().getApiVersion();
        info.versionName = ApplicationInfo.getInstance().getVersionName();
        info.projects = ProjectIndexHttpAction.getProjects();

        info.routes = ContainerUtil.map(RemoteUtil.getRoutes(), new Function<RouteInterface, Map<String, String>>() {
            @Override
            public Map<String, String> fun(final RouteInterface route) {
                return new HashMap<String, String>() {{
                    put("path", route.getPath());
                    put("method", route.getMethod());
                }};
            }
        });

        for (IdeaPluginDescriptor descriptor : PluginManager.getPlugins()) {
            Map<String, String> map = new TreeMap<String, String>();

            map.put("name", descriptor.getName());
            map.put("version", descriptor.getVersion());
            map.put("pluginId", descriptor.getPluginId().getIdString());

            info.plugins.add(map);
        }

        return new JsonResponse(info);
    }
}
