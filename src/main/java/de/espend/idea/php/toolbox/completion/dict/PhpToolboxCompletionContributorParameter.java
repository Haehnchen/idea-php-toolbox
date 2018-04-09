package de.espend.idea.php.toolbox.completion.dict;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import de.espend.idea.php.toolbox.dict.MapObjectBag;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxCompletionContributorParameter {

    @NotNull
    private final CompletionParameters completionParameters;

    @NotNull
    private final ProcessingContext processingContext;

    @NotNull
    private final CompletionResultSet completionResultSet;

    @NotNull
    private final Collection<JsonRegistrar> registrars;

    private MapObjectBag bag;

    public PhpToolboxCompletionContributorParameter(
        @NotNull CompletionParameters completionParameters,
        @NotNull ProcessingContext processingContext,
        @NotNull CompletionResultSet completionResultSet,
        @NotNull Collection<JsonRegistrar> registrars
    ) {
        this.completionParameters = completionParameters;
        this.processingContext = processingContext;
        this.completionResultSet = completionResultSet;
        this.registrars = registrars;
    }

    @NotNull
    public CompletionParameters getCompletionParameters() {
        return completionParameters;
    }

    @NotNull
    public ProcessingContext getProcessingContext() {
        return processingContext;
    }

    @NotNull
    public CompletionResultSet getCompletionResultSet() {
        return completionResultSet;
    }

    @NotNull
    public Project getProject () {
        return completionParameters.getPosition().getProject();
    }

    @NotNull
    public JsonRegistrar getRegistrar() {
        return registrars.iterator().next();
    }

    @NotNull
    public MapObjectBag getParameterBag() {
        return this.bag != null ? this.bag : (this.bag = new MapObjectBag(registrars.iterator().next().getParameters()));
    }

    @NotNull
    public Collection<JsonRegistrar> getRegistrars() {
        return registrars;
    }
}
