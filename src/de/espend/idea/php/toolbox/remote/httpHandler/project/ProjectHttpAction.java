package de.espend.idea.php.toolbox.remote.httpHandler.project;

import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.ProjectStorageDic;
import de.espend.idea.php.toolbox.remote.provider.ProviderStorageInterface;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectHttpAction extends ProjectHttpActionAbstract {

    @Override
    protected Response handle(@NotNull Project project, @NotNull RequestMatcher requestMatcher) {
        ProjectStorageDic storageDic = new ProjectStorageDic();

        storageDic.name = project.getName();
        storageDic.presentableUrl = project.getPresentableUrl();

        for (Map.Entry<String, ProviderStorageInterface> provider : RemoteStorage.getInstance(project).all().entrySet()) {
            try {
                storageDic.storages.put(provider.getKey(), provider.getValue().getData());
            } catch (JsonSyntaxException ignored) {
            }
        }

        return new JsonResponse(storageDic);
    }
}
