package de.espend.idea.php.toolbox.provider.source;

import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.dict.json.JsonProviderSource;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SourceContributorDeclarationHandlerParameter {

    @NotNull
    private final PhpToolboxDeclarationHandlerParameter parameter;

    @NotNull
    private final JsonProvider jsonProvider;

    public SourceContributorDeclarationHandlerParameter(@NotNull PhpToolboxDeclarationHandlerParameter parameter, @NotNull JsonProvider jsonProvider) {
        this.parameter = parameter;
        this.jsonProvider = jsonProvider;
    }

    @NotNull
    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }

    @NotNull
    public PhpToolboxDeclarationHandlerParameter getHandlerParameter() {
        return parameter;
    }

    @NotNull
    public Project getProject() {
        return parameter.getProject();
    }

    @Nullable
    public JsonProviderSource getSource() {
        return jsonProvider.getSource();
    }

    @Nullable
    public String getSourceParameter() {
        return getSource() != null ? getSource().getParameter() : null;
    }
}
