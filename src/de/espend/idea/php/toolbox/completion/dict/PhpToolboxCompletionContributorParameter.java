package de.espend.idea.php.toolbox.completion.dict;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class PhpToolboxCompletionContributorParameter {

    @NotNull
    private final CompletionParameters completionParameters;
    private final ProcessingContext processingContext;
    @NotNull
    private final CompletionResultSet completionResultSet;

    public PhpToolboxCompletionContributorParameter(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

        this.completionParameters = completionParameters;
        this.processingContext = processingContext;
        this.completionResultSet = completionResultSet;
    }

    @NotNull
    public CompletionParameters getCompletionParameters() {
        return completionParameters;
    }

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

}
