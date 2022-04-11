package de.espend.idea.php.toolbox;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.XmlSerializerUtil;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
@Service
@State(name = "PHP Toolbox", storages = @Storage(file = "$APP_CONFIG$/php-toolbox.xml"))
public class PhpToolboxApplicationService implements PersistentStateComponent<PhpToolboxApplicationService> {

    final public static Logger LOG = Logger.getInstance("Toolbox-Plugin");

    public File[] getApplicationJsonFiles() {

        File file = new File(getApplicationFolder());
        if(!file.isDirectory()) {
            return new File[0];
        }

        return file.listFiles(new JsonParseUtil.JsonFileFilter());

    }

    @NotNull
    public static String getApplicationFolder() {
        return PathManager.getConfigPath() + "/php-toolbox";
    }

    @Nullable
    @Override
    public PhpToolboxApplicationService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PhpToolboxApplicationService applicationService) {
        XmlSerializerUtil.copyBean(applicationService, this);
    }
}
