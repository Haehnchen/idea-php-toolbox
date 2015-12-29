package de.espend.idea.php.toolbox.matcher.php.util;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.dict.json.JsonSignature;
import de.espend.idea.php.toolbox.dict.matcher.LanguageMatcherParameter;
import fr.adrienbrault.idea.symfony2plugin.Symfony2InterfacesUtil;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpMatcherUtil {

    public static boolean isMachingReturnArray(@NotNull LanguageMatcherParameter parameter, @NotNull PsiElement phpReturn) {

        Function function = PsiTreeUtil.getParentOfType(phpReturn, Function.class);
        if(function == null) {
            return false;
        }

        // inside class method
        if(function instanceof Method) {
            Symfony2InterfacesUtil symfony2InterfacesUtil = new Symfony2InterfacesUtil();

            for (JsonSignature signature : parameter.getSignatures()) {
                if(StringUtils.isBlank(signature.getMethod()) || StringUtils.isBlank(signature.getClassName())) {
                    continue;
                }
                if(symfony2InterfacesUtil.isCallTo((Method) function, signature.getClassName(), signature.getMethod())) {
                    return true;
                }
            }

            return false;
        }

        // @TODO: class instance check for "function"
        // fallback in function
        for (JsonSignature signature : filterFunctionSignatures(parameter.getSignatures())) {
            if(function.getName().equals(signature.getFunction())) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesArraySignature(@NotNull  PsiElement parent, @NotNull  Collection<JsonSignature> signatures) {
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
        return new HashSet<JsonSignature>(ContainerUtil.filter(signatures, new Condition<JsonSignature>() {
            @Override
            public boolean value(JsonSignature s) {
                return StringUtils.isNotBlank(s.getFunction());
            }
        }));
    }
}
