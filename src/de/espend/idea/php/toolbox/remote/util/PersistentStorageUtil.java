package de.espend.idea.php.toolbox.remote.util;

import com.google.gson.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.provider.ProviderInterface;
import de.espend.idea.php.toolbox.remote.provider.ProviderStorageInterface;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PersistentStorageUtil {

    private static String PERSISTENT_FILE = "php-toolbox-server-storage.json";

    public static void load(@NotNull Project project) {
        VirtualFile json = VfsUtil.findRelativeFile(project.getBaseDir(), ".idea", PERSISTENT_FILE);
        if(json == null) {
            return;
        }

        String s;
        try {
            s = StreamUtil.readText(json.getInputStream(), "UTF-8");
        } catch (IOException ignored) {
            PhpToolboxApplicationService.LOG.error(String.format("Error loading file '%s'", json));
            return;
        }

        JsonParser jsonParser = new JsonParser();

        JsonObject parse = null;
        try {
            parse = jsonParser.parse(s).getAsJsonObject();
        } catch (JsonSyntaxException ignored) {
        } catch (JsonIOException ignored) {
        } catch (IllegalStateException ignored) {
        }

        if(parse == null)  {
            PhpToolboxApplicationService.LOG.error(String.format("Error parsing file '%s'", json));
            return;
        }

        for (Map.Entry<String, JsonElement> entry : parse.entrySet()) {
            String providerName = entry.getKey();

            ProviderInterface provider = RemoteUtil.getProvider(providerName);
            if(provider == null) {
                continue;
            }

            RemoteStorage instance = RemoteStorage.getInstance(project);
            instance.set(provider, new Gson().toJson(entry.getValue()));
        }
    }

    public static void write(@NotNull Project project) {
        VirtualFile ideaFolder = VfsUtil.findRelativeFile(project.getBaseDir(), ".idea");
        if(ideaFolder == null) {
            return;
        }

        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        boolean hasData = false;
        for (Map.Entry<String, ProviderStorageInterface> entry : RemoteStorage.getInstance(project).all().entrySet()) {
            jsonObject.add(entry.getKey(), gson.toJsonTree(entry.getValue().getData()));
            hasData = true;
        }

        File file = new File(ideaFolder.getPath() + "/" + PERSISTENT_FILE);

        // drop file on empty content
        if(!hasData) {
            try {
                file.delete();
            } catch (SecurityException ignored) {
                PhpToolboxApplicationService.LOG.error(String.format("Can not delete '%s'", file));
                return;
            }

            PhpToolboxApplicationService.LOG.info(String.format("Delete empty file '%s'", file));
            return;
        }

        String str = gson.toJson(jsonObject);

        try {
            FileUtil.writeToFile(file, str);
        } catch (IOException ignored) {
            PhpToolboxApplicationService.LOG.error(String.format("Can not write to '%s' file", file));
        }

    }
}
