package de.espend.idea.php.toolbox;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.remote.util.PersistentStorageUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxProjectComponent implements ProjectComponent {

    private Project project;

    public PhpToolboxProjectComponent(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        DumbService.getInstance(project).smartInvokeLater(() -> PersistentStorageUtil.load(project));
    }

    @Override
    public void projectClosed() {
        PersistentStorageUtil.write(project);
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Php-Toolbox";
    }
}
