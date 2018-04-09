package de.espend.idea.php.toolbox.type;

import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxTypeProviderArguments {

    final private Project project;
    final private String parameter;
    final private Collection<JsonRawLookupElement> lookupElements;

    public PhpToolboxTypeProviderArguments(@NotNull Project project, @NotNull String parameter, @NotNull Collection<JsonRawLookupElement> lookupElements) {
        this.project = project;
        this.parameter = parameter;
        this.lookupElements = lookupElements;
    }

    @NotNull
    public String getParameter() {
        return parameter;
    }

    @NotNull
    public Project getProject() {
        return project;
    }

    @NotNull
    public Collection<JsonRawLookupElement> getLookupElements() {
        return lookupElements;
    }
}
