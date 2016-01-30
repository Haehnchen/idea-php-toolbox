package de.espend.idea.php.toolbox.completion.dict;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ToolboxJsonFileCompletionArguments {

    public enum TYPE {
        LANGUAGE, SIGNATURE_TYPE,
        SIGNATURE_KEY, PROVIDER_KEY
    }

    @NotNull
    private final TYPE type;

    @NotNull
    private final CompletionResultSet resultSet;

    public ToolboxJsonFileCompletionArguments(@NotNull TYPE type, @NotNull CompletionResultSet resultSet) {
        this.type = type;
        this.resultSet = resultSet;
    }

    @NotNull
    public CompletionResultSet getResultSet() {
        return resultSet;
    }

    public void addLookupString(@NotNull String lookup) {
        resultSet.addElement(LookupElementBuilder.create(lookup));
    }

    @NotNull
    public TYPE getType() {
        return type;
    }
}
