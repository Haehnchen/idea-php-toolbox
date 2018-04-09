package de.espend.idea.php.toolbox.provider.source;

import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.dict.json.JsonProviderSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SourceContributorParameter {

    @NotNull
    private final PhpToolboxCompletionContributorParameter parameter;

    @NotNull
    private final JsonProvider jsonProvider;

    public SourceContributorParameter(@NotNull PhpToolboxCompletionContributorParameter parameter, @NotNull JsonProvider jsonProvider) {
        this.parameter = parameter;
        this.jsonProvider = jsonProvider;
    }

    @NotNull
    public JsonProvider getJsonProvider() {
        return jsonProvider;
    }

    @NotNull
    public PhpToolboxCompletionContributorParameter getContributor() {
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
