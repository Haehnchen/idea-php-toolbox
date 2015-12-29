package de.espend.idea.php.toolbox.matcher.twig;

import com.intellij.openapi.fileTypes.FileType;
import com.jetbrains.twig.TwigFileType;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.utils.TwigUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TwigBlockRegistrarMatcher implements LanguageRegistrarMatcherInterface {

    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {
        Set<String> functions = new HashSet<String>();

        for (JsonSignature signature : parameter.getSignatures()) {
            if(StringUtils.isBlank(signature.getFunction())) {
                continue;
            }

            functions.add(signature.getFunction());
        }

        return TwigUtil.getPrintBlockFunctionPattern(functions.toArray(new String[functions.size()])).accepts(parameter.getElement());
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {
        return fileType == TwigFileType.INSTANCE;
    }
}
