package de.espend.idea.php.toolbox.remote.httpHandler.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.StreamUtil;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.Response;
import de.espend.idea.php.toolbox.remote.httpHandler.dic.ErrorDic;
import de.espend.idea.php.toolbox.remote.provider.ProviderInterface;
import de.espend.idea.php.toolbox.remote.util.RemoteUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectProviderPostHttpAction extends ProjectHttpActionAbstract {

    @Override
    protected Response handle(@NotNull Project project, @NotNull RequestMatcher requestMatcher) {

        String providerName = requestMatcher.getVar("provider");
        if(providerName == null) {
            return new Response("invalid request");
        }

        ProviderInterface provider = RemoteUtil.getProvider(providerName);
        if(provider == null) {
            return new JsonResponse(ErrorDic.create("provider not found: " + providerName));
        }

        String content;
        try {
            content = StreamUtil.readText(requestMatcher.getHttpExchange().getRequestBody(), "UTF-8");
        } catch (IOException ignored) {
            return new JsonResponse("error");
        }

        RemoteStorage instance = RemoteStorage.getInstance(project);
        instance.set(provider, content);

        return new JsonResponse("OK");
    }
}
