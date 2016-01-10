package de.espend.idea.php.toolbox.remote.httpHandler;

import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import com.sun.net.httpserver.HttpExchange;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.ProjectStorageDic;
import de.espend.idea.php.toolbox.remote.provider.ProviderStorageInterface;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;

import java.io.IOException;
import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectGetActionHandler {

    public void handleProject(Project project, HttpExchange xchg, String[] pathElements) throws IOException {

        if(pathElements.length > 2 && pathElements[2].equals("clear")) {
            RemoteStorage.removeInstance(project);
            HttpExchangeUtil.sendResponse(xchg, "cleared");
            return;
        }

        ProjectStorageDic storageDic = new ProjectStorageDic();

        storageDic.name = project.getName();
        storageDic.presentableUrl = project.getPresentableUrl();

        for (Map.Entry<String, ProviderStorageInterface> provider : RemoteStorage.getInstance(project).all().entrySet()) {
            try {
                storageDic.storages.put(provider.getKey(), provider.getValue().getData());
            } catch (JsonSyntaxException ignored) {
            }
        }

        HttpExchangeUtil.sendResponse(xchg, new JsonResponse(storageDic));
    }
}
