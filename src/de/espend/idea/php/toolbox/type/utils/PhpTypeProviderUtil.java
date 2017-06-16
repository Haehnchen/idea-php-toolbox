package de.espend.idea.php.toolbox.type.utils;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.symfony.utils.PhpElementsUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpTypeProviderUtil {

    /**
     * 1#foobar
     */
    public static char PARAMETER_SPLIT_CHAR = '\u0180';

    @Nullable
    public static String getReferenceSignature(FunctionReference functionReference, char trimKey, Set<Integer> parameterIndexes) {

        String refSignature = functionReference.getSignature();
        if(StringUtil.isEmpty(refSignature)) {
            return null;
        }

        PsiElement[] parameters = functionReference.getParameters();

        Map<Integer, String> parameterSignatures = new HashMap<>();

        for (int parameterIndex : parameterIndexes) {
            if (parameterIndex + 1 > parameters.length) {
                continue;
            }

            PsiElement parameter = parameters[parameterIndex];

            // we already have a string value
            if ((parameter instanceof StringLiteralExpression)) {
                String param = ((StringLiteralExpression)parameter).getContents();
                if (StringUtil.isEmpty(param)) {
                    continue;
                }

                parameterSignatures.put(parameterIndex, param);
            }

            // whitelist here; we can also provide some more but think of performance
            // Service::NAME, $this->name and Entity::CLASS;
            if (parameter instanceof PhpReference && (parameter instanceof ClassConstantReference || parameter instanceof FieldReference)) {
                String signature = ((PhpReference) parameter).getSignature();
                if (StringUtil.isEmpty(signature)) {
                    continue;
                }

                parameterSignatures.put(parameterIndex, signature);
            }
        }

        if (parameterSignatures.isEmpty()) {
            return null;
        }

        String parametersSignature = parameterSignatures
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + String.valueOf(PARAMETER_SPLIT_CHAR) + entry.getValue())
                .collect(Collectors.joining(String.valueOf(trimKey)));

        return refSignature + trimKey + parametersSignature;
    }

    /**
     * we can also pipe php references signatures and resolve them here
     * overwrite parameter to get string value
     */
    @Nullable
    public static String getResolvedParameter(PhpIndex phpIndex, String parameter) {

        // PHP 5.5 class constant: "Class\Foo::class"
        if(parameter.startsWith("#K#C")) {
            // PhpStorm9: #K#C\Class\Foo.class
            if(parameter.endsWith(".class")) {
                return parameter.substring(4, parameter.length() - 6);
            }

            // PhpStorm8: #K#C\Class\Foo.
            // workaround since signature has empty type
            if(parameter.endsWith(".")) {
                return parameter.substring(4, parameter.length() - 1);
            }
        }

        // #K#C\Class\Foo.property
        // #K#C\Class\Foo.CONST
        if(parameter.startsWith("#")) {

            // get psi element from signature
            Collection<? extends PhpNamedElement> signTypes = phpIndex.getBySignature(parameter, null, 0);
            if(signTypes.size() == 0) {
                return null;
            }

            // get string value
            parameter = PhpElementsUtil.getStringValue(signTypes.iterator().next());
            if(parameter == null) {
                return null;
            }

        }

        return parameter;
    }

}

