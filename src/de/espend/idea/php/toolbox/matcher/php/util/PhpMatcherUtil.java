package de.espend.idea.php.toolbox.matcher.php.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.matcher.php.container.ContainerConditions;
import de.espend.idea.php.toolbox.symfony.Symfony2InterfacesUtil;
import de.espend.idea.php.toolbox.symfony.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpMatcherUtil {

    public static boolean isMachingReturnArray(@NotNull Collection<JsonSignature> signatures, @NotNull PsiElement phpReturn) {

        Function function = PsiTreeUtil.getParentOfType(phpReturn, Function.class);
        if(function == null) {
            return false;
        }

        List<JsonSignature> filter = ContainerUtil.filter(signatures, ContainerConditions.RETURN_TYPE_FILTER);

        if(filter.size() == 0) {
            return false;
        }

        // inside class method
        if(function instanceof Method) {
            Symfony2InterfacesUtil symfony2InterfacesUtil = null;

            for (JsonSignature signature : filter) {
                if(StringUtils.isBlank(signature.getMethod()) || StringUtils.isBlank(signature.getClassName())) {
                    continue;
                }

                if(symfony2InterfacesUtil == null) {
                    symfony2InterfacesUtil = new Symfony2InterfacesUtil();

                }

                if(symfony2InterfacesUtil.isCallTo((Method) function, signature.getClassName(), signature.getMethod())) {
                    return true;
                }
            }

            return false;
        }

        // @TODO: class instance check for "function"
        // fallback in function
        for (JsonSignature signature : filterFunctionSignatures(filter)) {
            if(function.getName().equals(signature.getFunction())) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesArraySignature(@NotNull  PsiElement parent, @NotNull Collection<JsonSignature> signatures) {
        Collection<JsonSignature> functionSignatures = filterFunctionSignatures(signatures);
        if(functionSignatures.size() == 0) {
            return false;
        }

        for (JsonSignature signature : functionSignatures) {
            // @TODO: there ways to merge this
            if(PhpElementsUtil.isFunctionReference(parent, signature.getIndex(), signature.getFunction())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Filter signatures with a valid "function" value
     */
    @NotNull
    public static Collection<JsonSignature> filterFunctionSignatures(@NotNull Collection<JsonSignature> signatures) {
        return new HashSet<>(ContainerUtil.filter(signatures, ContainerConditions.FUNCTION_FILTER));
    }
}
