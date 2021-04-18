package de.espend.idea.php.toolbox.remote.httpHandler.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.httpHandler.project.ProjectHttpActionAbstract;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProjectStorageHandler extends ProjectHttpActionAbstract {

    @Override
    protected Response handle(final @NotNull Project project, @NotNull RequestMatcher requestMatcher) {

        final Collection<JsonConfigFile> files = new ArrayList<>();

        ApplicationManager.getApplication().runReadAction(() -> {
            files.addAll(ExtensionProviderUtil.getJsonConfigs(
                project,
                ServiceManager.getService(PhpToolboxApplicationService.class)
            ));
        });

        return new JsonResponse(files);
    }
}
