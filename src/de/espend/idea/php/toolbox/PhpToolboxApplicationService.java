package de.espend.idea.php.toolbox;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PhpToolboxApplicationService implements ApplicationComponent {

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    public File[] getApplicationJsonFiles() {

        File file = new File(PathManager.getConfigPath() + "/php-toolbox");
        if(!file.isDirectory()) {
            return new File[0];
        }

        return file.listFiles(new JsonParseUtil.JsonFileFilter());

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "PhpToolboxApplicationService";
    }

}
