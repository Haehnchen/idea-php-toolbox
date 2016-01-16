package de.espend.idea.php.toolbox.matcher.php.docTag;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.*;
import fr.adrienbrault.idea.symfony2plugin.util.ParameterBag;
import fr.adrienbrault.idea.symfony2plugin.util.PsiElementUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class PhpDocUtil {

    /**
     * Collects all doc tag text for an index. does also visit also method implements cases
     */
    @Nullable
    public static Collection<PhpDocParamTag> getDocTagsForScope(@NotNull PsiElement psiElement) {

        ParameterList parameterList = PsiTreeUtil.getParentOfType(psiElement, ParameterList.class);
        if (parameterList == null) {
            return null;
        }

        ParameterBag currentIndex = PsiElementUtils.getCurrentParameterIndex(psiElement);
        if(currentIndex == null) {
            return null;
        }

        PsiElement parent = parameterList.getParent();
        if(!(parent instanceof FunctionReference)) {
            return null;
        }

        FunctionReference methodReference = (FunctionReference) parent;
        PsiReference psiReference = methodReference.getReference();
        if (null == psiReference) {
            return null;
        }

        PsiElement resolvedReference = psiReference.resolve();
        if (!(resolvedReference instanceof Function)) {
            return null;
        }

        Function method = (Function) resolvedReference;
        Parameter[] methodParameter = method.getParameters();
        if(methodParameter.length -1 < currentIndex.getIndex()) {
            return null;
        }

        Collection<PhpDocParamTag> phpDocParamTags = new ArrayList<PhpDocParamTag>();

        if(method instanceof Method) {
            for(Method implementedMethod: getImplementedMethods((Method) method)) {
                Parameter[] implementedParameters = implementedMethod.getParameters();
                if(!(implementedParameters.length -1 < currentIndex.getIndex())) {
                    Parameter parameter = implementedParameters[currentIndex.getIndex()];
                    PsiElement implementedParameterList = parameter.getContext();

                    if(implementedParameterList instanceof ParameterList) {
                        PhpDocParamTag phpDocParamTag = parameter.getDocTag();
                        if(phpDocParamTag != null) {
                            phpDocParamTags.add(phpDocParamTag);
                        }
                    }
                }
            }
        } else {
            PhpDocParamTag docTag = methodParameter[currentIndex.getIndex()].getDocTag();
            if(docTag != null) {
                phpDocParamTags.add(docTag);
            }
        }

        return phpDocParamTags;
    }

    private static Method[] getImplementedMethods(@NotNull Method method) {
        ArrayList<Method> items = getImplementedMethods(method.getContainingClass(), method, new ArrayList<Method>());
        return items.toArray(new Method[items.size()]);
    }

    private static ArrayList<Method> getImplementedMethods(@Nullable PhpClass phpClass, @NotNull Method method, ArrayList<Method> implementedMethods) {
        if (phpClass == null) {
            return implementedMethods;
        }

        Method[] methods = phpClass.getOwnMethods();
        for (Method ownMethod : methods) {
            if (PhpLangUtil.equalsMethodNames(ownMethod.getName(), method.getName())) {
                implementedMethods.add(ownMethod);
            }
        }

        for(PhpClass interfaceClass: phpClass.getImplementedInterfaces()) {
            getImplementedMethods(interfaceClass, method, implementedMethods);
        }

        getImplementedMethods(phpClass.getSuperClass(), method, implementedMethods);

        return implementedMethods;
    }
}
