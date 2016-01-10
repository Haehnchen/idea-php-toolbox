package de.espend.idea.php.toolbox.remote.httpHandler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.StreamUtil;
import com.sun.net.httpserver.HttpExchange;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.ErrorDic;
import de.espend.idea.php.toolbox.remote.provider.ProviderInterface;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import de.espend.idea.php.toolbox.remote.util.RemoteUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectPostActionHandler {

    public void handleProject(Project project, HttpExchange xchg) throws IOException {

        String[] pathElements = StringUtils.strip(xchg.getRequestURI().getPath(), "/").split("/");
        if(pathElements.length < 3) {
            HttpExchangeUtil.sendResponse(xchg, "invalid request");
            return;
        }

        ProviderInterface provider = RemoteUtil.getProvider(pathElements[2]);
        if(provider == null) {
            HttpExchangeUtil.sendResponse(xchg, new JsonResponse(ErrorDic.create("provider not found: " + pathElements[2])));
            return;
        }

        String content = StreamUtil.readText(xchg.getRequestBody(), "UTF-8");

        RemoteStorage instance = RemoteStorage.getInstance(project);
        instance.set(provider, content);

        HttpExchangeUtil.sendResponse(xchg, new JsonResponse("OK"));
    }
}
