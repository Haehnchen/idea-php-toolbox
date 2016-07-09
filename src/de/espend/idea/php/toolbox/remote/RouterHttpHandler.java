package de.espend.idea.php.toolbox.remote;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.espend.idea.php.toolbox.remote.dic.RouteInterface;
import de.espend.idea.php.toolbox.remote.http.JsonResponse;
import de.espend.idea.php.toolbox.remote.http.RequestMatcher;
import de.espend.idea.php.toolbox.remote.util.HttpExchangeUtil;
import de.espend.idea.php.toolbox.remote.util.RemoteUtil;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RouterHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String path = httpExchange.getRequestURI().getPath();
        String method = httpExchange.getRequestMethod();

        Collection<RouteInterface> routes = RemoteUtil.getRoutes();

        for (RouteInterface route : routes) {
            if(!method.equalsIgnoreCase(route.getMethod())) {
                continue;
            }

            String routeRegex = route.getPath().replaceAll("\\{[\\w-]+\\}", "([^/]++)");

            Matcher matcher = Pattern.compile("^" + routeRegex + "$").matcher(path);
            if(!matcher.find()) {
                continue;
            }

            List<String> matches = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                matches.add(matcher.group(i));
            }

            Matcher varMatcher = Pattern.compile("\\{([\\w-]+)\\}").matcher(route.getPath());

            int n = 0;
            Map<String, String> map = new HashMap<>();
            while (varMatcher.find() ) {
                map.put(varMatcher.group(1), matches.get(n++));
            }

            route.getHttpAction()
                .handle(new RequestMatcher(httpExchange, map))
                .send(httpExchange);

            return;
        }

        HttpExchangeUtil.sendResponse(httpExchange, new JsonResponse("Not found"));
    }
}
