package de.espend.idea.php.toolbox.navigation.locator;

import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TargetLocatorParameter {

    @NotNull
    private final PhpToolboxDeclarationHandlerParameter parameter;

    @NotNull
    private final String target;

    public TargetLocatorParameter(@NotNull PhpToolboxDeclarationHandlerParameter parameter, @NotNull String target) {
        this.parameter = parameter;
        this.target = target;
    }

    @NotNull
    public PhpToolboxDeclarationHandlerParameter getDeclarationParameter() {
        return parameter;
    }

    @NotNull
    public String getTarget() {
        return target;
    }

    @NotNull
    public Project getProject() {
        return parameter.getProject();
    }
}
