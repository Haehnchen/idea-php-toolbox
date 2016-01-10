package de.espend.idea.php.toolbox.remote.httpHandler.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import de.espend.idea.php.toolbox.remote.http.HttpAction;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectIndexHttpAction implements HttpAction {

    @NotNull
    @Override
    public Response handle(@NotNull RequestMatcher requestMatcher) {
        return new JsonResponse(getProjects());
    }

    @NotNull
    public static Collection<Map<String, String>> getProjects() {

        Collection<Map<String, String>> projects = new ArrayList<Map<String, String>>();

        for(Project project: ProjectManager.getInstance().getOpenProjects()) {
            Map<String, String> projectMap = new TreeMap<String, String>();

            projectMap.put("name", project.getName());
            projectMap.put("basePath", project.getBasePath());

            try {
                projectMap.put("url", "/projects/" + URLDecoder.decode(project.getName(), "UTF-8"));
            } catch (UnsupportedEncodingException ignored) {
            }

            projects.add(projectMap);
        }

        return projects;
    }
}
