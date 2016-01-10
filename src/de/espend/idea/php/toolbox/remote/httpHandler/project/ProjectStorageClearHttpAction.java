package de.espend.idea.php.toolbox.remote.httpHandler.project;

import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.Response;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectStorageClearHttpAction extends ProjectHttpActionAbstract{

    @Override
    protected Response handle(@NotNull Project project, @NotNull RequestMatcher requestMatcher) {
        RemoteStorage.removeInstance(project);
        return new Response("cleared");
    }
}
