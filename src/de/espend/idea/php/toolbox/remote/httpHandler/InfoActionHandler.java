package de.espend.idea.php.toolbox.remote.httpHandler;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.InfoDic;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class InfoActionHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InfoDic info = new InfoDic();

        info.fullVersion = ApplicationInfo.getInstance().getFullVersion();
        info.apiVersion = ApplicationInfo.getInstance().getApiVersion();
        info.versionName = ApplicationInfo.getInstance().getVersionName();
        info.projects = ProjectActionHandler.getProjects(httpExchange);

        for (IdeaPluginDescriptor descriptor : PluginManager.getPlugins()) {
            Map<String, String> map = new TreeMap<String, String>();

            map.put("name", descriptor.getName());
            map.put("version", descriptor.getVersion());
            map.put("pluginId", descriptor.getPluginId().getIdString());

            info.plugins.add(map);
        }

        HttpExchangeUtil.sendResponse(httpExchange, new JsonResponse(info));
    }
}
