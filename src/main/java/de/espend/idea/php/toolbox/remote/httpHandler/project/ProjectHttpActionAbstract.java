package de.espend.idea.php.toolbox.remote.httpHandler.project;

import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.remote.http.HttpAction;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.ErrorDic;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import org.jetbrains.annotations.NotNull;

public abstract class ProjectHttpActionAbstract implements HttpAction {

    @NotNull
    @Override
    public Response handle(@NotNull RequestMatcher requestMatcher) {

        String projectName = requestMatcher.getVar("project");
        if(projectName == null) {
            return new Response("Project var missing");
        }

        Project project = HttpExchangeUtil.getProject(projectName);
        if(project == null) {
            return new JsonResponse(ErrorDic.create("invalid project"));
        }

        return handle(project, requestMatcher);
    }

    abstract protected Response handle(@NotNull Project project, @NotNull RequestMatcher requestMatcher);
}
