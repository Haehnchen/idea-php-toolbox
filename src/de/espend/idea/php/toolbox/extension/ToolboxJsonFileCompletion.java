package de.espend.idea.php.toolbox.extension;

import de.espend.idea.php.toolbox.completion.dict.ToolboxJsonFileCompletionArguments;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface ToolboxJsonFileCompletion {
    void addCompletions(@NotNull ToolboxJsonFileCompletionArguments arguments);
}
