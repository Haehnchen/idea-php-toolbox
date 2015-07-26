package de.espend.idea.php.toolbox.extension;

import com.intellij.openapi.fileTypes.FileType;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import org.jetbrains.annotations.NotNull;

public interface LanguageRegistrarMatcherInterface {
    public boolean matches(@NotNull LanguageMatcherParameter parameter);
    public boolean supports(@NotNull FileType fileType);
}
