package de.espend.idea.php.toolbox.remote.httpHandler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonStorageHandler implements HttpHandler {
    @Override
    public void handle(final HttpExchange xchg) throws IOException {

        String[] pathElements = StringUtils.strip(xchg.getRequestURI().getPath(), "/").split("/");
        if(pathElements.length < 2) {
            HttpExchangeUtil.sendResponse(xchg, "invalid project name");
            return;
        }

        String projectName = pathElements[1];

        final Project project = HttpExchangeUtil.getProject(projectName);
        if(project == null) {
            HttpExchangeUtil.sendResponse(xchg, "invalid project");
            return;
        }

        final Collection<JsonConfigFile> files = new ArrayList<JsonConfigFile>();

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                files.addAll(ExtensionProviderUtil.getJsonConfigs(
                    project,
                    ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class)
                ));
            }
        });

        HttpExchangeUtil.sendResponse(
            xchg,
            new JsonResponse(files)
        );
    }
}
