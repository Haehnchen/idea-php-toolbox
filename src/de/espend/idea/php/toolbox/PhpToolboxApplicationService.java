package de.espend.idea.php.toolbox;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.XmlSerializerUtil;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
@State(name = "PHP Toolbox", storages = @Storage(id = "php-toolbox-application", file = "$APP_CONFIG$/php-toolbox.xml"))
public class PhpToolboxApplicationService implements ApplicationComponent, PersistentStateComponent<PhpToolboxApplicationService> {

    final public static Logger LOG = Logger.getInstance("Symfony-Plugin");

    public boolean serverEnabled = false;
    public boolean listenAll = false;
    public int serverPort = 48734;

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {

    }

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

    @NotNull
    @Override
    public String getComponentName() {
        return "PhpToolboxApplicationService";
    }

    @Nullable
    @Override
    public PhpToolboxApplicationService getState() {
        return this;
    }

    @Override
    public void loadState(PhpToolboxApplicationService applicationService) {
        XmlSerializerUtil.copyBean(applicationService, this);
    }
}
