package de.espend.idea.php.toolbox;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import de.espend.idea.php.toolbox.remote.util.PersistentStorageUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxProjectComponent {
    public static class PostStartupActivity implements StartupActivity {
        @Override
        public void runActivity(@NotNull Project project) {
            DumbService.getInstance(project).smartInvokeLater(() -> PersistentStorageUtil.load(project));
        }
    }

    public static class ProjectCloseService implements Disposable {
        private final Project project;

        public ProjectCloseService(@NotNull Project project) {
            this.project = project;
        }

        @Override
        public void dispose() {
            PersistentStorageUtil.write(project);
        }
    }
}
