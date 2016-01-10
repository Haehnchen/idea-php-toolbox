package de.espend.idea.php.toolbox.remote;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import com.sun.net.httpserver.HttpServer;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.remote.httpHandler.InfoActionHandler;
import de.espend.idea.php.toolbox.remote.httpHandler.JsonStorageHandler;
import de.espend.idea.php.toolbox.remote.httpHandler.ProjectActionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RemoteListenerComponent implements ApplicationComponent {

    private final PhpToolboxApplicationService settings;
    private HttpServer server;
    private Thread listenerThread;

    public RemoteListenerComponent(PhpToolboxApplicationService settings) {
        this.settings = settings;
    }

    public void initComponent() {
        if(!settings.serverEnabled) {
            return;
        }

        final int port = settings.serverPort;
        final String host = !settings.listenAll ? "localhost" : "0.0.0.0";

        try {
            server = HttpServer.create(new InetSocketAddress(host, port), 0);
        } catch (IOException e) {
            PhpToolboxApplicationService.LOG.error(String.format("Can't bind with server to %s:%s", host, port));
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    Messages.showMessageDialog(
                        String.format("Can't bind with server to %s:%s", host, port), "PHP Toolbox",
                        Messages.getErrorIcon()
                    );
                }
            });

            return;
        }

        server.createContext("/projects", new ProjectActionHandler());
        server.createContext("/json-debug", new JsonStorageHandler());
        server.createContext("/", new InfoActionHandler());

        final HttpServer finalServer = server;
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                finalServer.start();
                PhpToolboxApplicationService.LOG.info(String.format("Starting server on %s:%s", host, port));
            }
        });

        listenerThread.start();
    }

    public void disposeComponent() {

        if (listenerThread != null) {
            listenerThread.interrupt();
        }

        if(server != null) {
            server.stop(0);
        }

    }

    @NotNull
    public String getComponentName() {
        return "RemoteStorage";
    }
}