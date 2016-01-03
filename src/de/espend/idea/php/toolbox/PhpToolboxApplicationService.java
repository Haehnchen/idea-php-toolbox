package de.espend.idea.php.toolbox;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxApplicationService implements ApplicationComponent {

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

}
