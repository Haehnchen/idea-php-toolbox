package de.espend.idea.php.toolbox.matcher.php;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.twig.TwigFileType;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.matcher.php.container.ContainerConditions;
import de.espend.idea.php.toolbox.matcher.php.util.PhpMatcherUtil;
import jdk.nashorn.internal.runtime.logging.DebugLogger;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author eater <=@eater.me>
 */
public class ArrayAccessSignatureRegistrarMatcher implements LanguageRegistrarMatcherInterface {
    @Override
    public boolean matches(@NotNull LanguageMatcherParameter parameter) {
        PsiElement parent = parameter.getElement().getParent();
        if (!(parent instanceof StringLiteralExpression)) {
            return false;
        }

        PsiElement location = parent.getParent().getParent();

        if (!(location instanceof ArrayAccessExpression)) {
            return false;
        }

        Collection<JsonSignature> signatures = ContainerUtil.filter(
                parameter.getRegistrar().getSignatures(),
                signature -> StringUtils.isNotBlank(signature.getArrayAccess())
        );

        if (signatures.size() == 0) {
            return false;
        }

        PsiElement access_to = ((ArrayAccessExpression) location).getValue();

        if (!(access_to instanceof PhpTypedElement)) {
            return false;
        }

        PhpType access_type = ((PhpTypedElement)access_to).getDeclaredType();

        for (JsonSignature signature : signatures) {
            String type_name = signature.getArrayAccess();

            if (type_name.charAt(0) != '\\') {
                type_name = "\\" + type_name;
            }

            if (access_type.getTypes().contains(type_name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean supports(@NotNull FileType fileType) {

        return fileType == PhpFileType.INSTANCE;
    }
}
