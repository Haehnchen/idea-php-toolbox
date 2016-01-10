package de.espend.idea.php.toolbox.remote.httpHandler;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.remote.http.HttpAction;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonStorageHandler implements HttpAction {

    @NotNull
    @Override
    public Response handle(@NotNull RequestMatcher requestMatcher) {

        String[] pathElements = StringUtils.strip(requestMatcher.getHttpExchange().getRequestURI().getPath(), "/").split("/");
        if(pathElements.length < 2) {
            return new Response("invalid project name");
        }

        String projectName = pathElements[1];

        final Project project = HttpExchangeUtil.getProject(projectName);
        if(project == null) {
            return new Response("invalid project");
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

        return new JsonResponse(files);
    }
}
