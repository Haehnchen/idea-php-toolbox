package de.espend.idea.php.toolbox.extension;

import com.intellij.openapi.fileTypes.FileType;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface LanguageRegistrarMatcherInterface {

    boolean matches(@NotNull LanguageMatcherParameter parameter);

    boolean supports(@NotNull FileType fileType);
}
