package de.espend.idea.php.toolbox.matcher.twig;

import com.intellij.openapi.fileTypes.FileType;
import com.jetbrains.twig.TwigFileType;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.utils.TwigUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TwigBlockRegistrarMatcher implements LanguageRegistrarMatcherInterface {

    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {
        Collection<String> signatures = parameter.getSignatures();
        return TwigUtil.getPrintBlockFunctionPattern(signatures.toArray(new String[signatures.size()])).accepts(parameter.getElement());
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {
        return fileType == TwigFileType.INSTANCE;
    }
}
