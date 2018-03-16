package de.espend.idea.php.toolbox.remote.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.sun.net.httpserver.HttpExchange;
import de.espend.idea.php.toolbox.remote.http.Response;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class HttpExchangeUtil {

    public static void sendResponse(@NotNull HttpExchange xchg, @NotNull String content, int status) throws IOException {
        xchg.sendResponseHeaders(status, content.length());
        OutputStream os = xchg.getResponseBody();
        os.write(content.getBytes());
        os.close();
    }

    public static void sendResponse(@NotNull HttpExchange xchg, @NotNull Response response) throws IOException {
        response.send(xchg);
    }

    public static void sendResponse(HttpExchange xchg, String content) throws IOException {
        sendResponse(xchg, content, 200);
    }

    public static void sendResponse(HttpExchange xchg, Collection<String> contents) throws IOException {
        sendResponse(xchg, StringUtils.join(contents, "\n"));
    }

    public static void sendResponse(HttpExchange xchg, StringBuilder response) throws IOException {
        xchg.sendResponseHeaders(200, response.length());
        OutputStream os = xchg.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }


    @Nullable
    public static Project getProject(@NotNull String name) {
        for(Project project: ProjectManager.getInstance().getOpenProjects()) {
            if(name.equals(project.getName())) {
                return project;
            }
        }

        return null;
    }
}
