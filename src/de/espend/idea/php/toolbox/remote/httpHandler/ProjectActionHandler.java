package de.espend.idea.php.toolbox.remote.httpHandler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.ErrorDic;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectActionHandler implements HttpHandler {

    public void handle(HttpExchange xchg) throws IOException {

        String path = xchg.getRequestURI().getPath();
        if(path.equals("/projects")) {
            HttpExchangeUtil.sendResponse(xchg, new JsonResponse(getProjects(xchg)));
            return;
        }

        String[] pathElements = StringUtils.strip(path, "/").split("/");
        if(pathElements.length < 2) {
            HttpExchangeUtil.sendResponse(xchg, new JsonResponse(ErrorDic.create("Invalid project name")));
            return;
        }

        String projectName = pathElements[1];

        Project project = HttpExchangeUtil.getProject(projectName);
        if(project == null) {
            HttpExchangeUtil.sendResponse(xchg, new JsonResponse(ErrorDic.create("invalid project")));
            return;
        }

        if(xchg.getRequestMethod().equals("GET")) {
            new ProjectGetActionHandler().handleProject(project, xchg, pathElements);
            return;
        }

        try {
            new ProjectPostActionHandler().handleProject(project, xchg);
        } catch (Exception e) {
            HttpExchangeUtil.sendResponse(xchg, new JsonResponse(ErrorDic.create(e.getMessage())));
            return;
        }

    }

    @NotNull
    public static Collection<Map<String, String>> getProjects(@NotNull HttpExchange xchg) {

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
