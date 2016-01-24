package de.espend.idea.php.toolbox.symfony.utils;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.symfony.dic.MethodReferenceBag;
import de.espend.idea.php.toolbox.symfony.util.ParameterBag;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PhpElementsUtil {

    @Nullable
    static public PhpClass getClassInterface(Project project, @NotNull String className) {

        // api workaround for at least interfaces
        if(!className.startsWith("\\")) {
            className = "\\" + className;
        }

        Collection<PhpClass> phpClasses = PhpIndex.getInstance(project).getAnyByFQN(className);
        return phpClasses.size() == 0 ? null : phpClasses.iterator().next();
    }


    @Nullable
    static public Method getClassMethod(Project project, String phpClassName, String methodName) {

        // we need here an each; because eg Command is non unique because phar file
        for(PhpClass phpClass: PhpIndex.getInstance(project).getClassesByFQN(phpClassName)) {
            Method method = getClassMethod(phpClass, methodName);
            if(method != null) {
                return method;
            }
        }

        return null;
    }

    @Nullable
    static public Method getClassMethod(PhpClass phpClass, String methodName) {
        for(Method method: phpClass.getMethods()) {
            if(method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    static public Collection<PhpClass> getClassesOrInterfaces(Project project, @NotNull String className) {

        // api workaround for at least interfaces
        if(!className.startsWith("\\")) {
            className = "\\" + className;
        }

        return PhpIndex.getInstance(project).getAnyByFQN(className);
    }

    @Nullable
    public static MethodReferenceBag getMethodParameterReferenceBag(PsiElement psiElement) {
        return getMethodParameterReferenceBag(psiElement, -1);
    }

    @Nullable
    public static MethodReferenceBag getMethodParameterReferenceBag(PsiElement psiElement, int wantIndex) {

        PsiElement variableContext = psiElement.getContext();
        if(!(variableContext instanceof ParameterList)) {
            return null;
        }

        ParameterList parameterList = (ParameterList) variableContext;
        if (!(parameterList.getContext() instanceof MethodReference)) {
            return null;
        }

        ParameterBag currentIndex = getCurrentParameterIndex(psiElement);
        if(currentIndex == null) {
            return null;
        }

        if(wantIndex >= 0 && currentIndex.getIndex() != wantIndex) {
            return null;
        }

        return new MethodReferenceBag(parameterList, (MethodReference) parameterList.getContext(), currentIndex);

    }

    public static boolean isFunctionReference(PsiElement psiElement, int wantIndex, String... funcName) {

        if(funcName.length == 0) {
            return false;
        }

        PsiElement variableContext = psiElement.getContext();
        if(!(variableContext instanceof ParameterList)) {
            return false;
        }

        ParameterList parameterList = (ParameterList) variableContext;
        PsiElement context = parameterList.getContext();
        if (!(context instanceof FunctionReference)) {
            return false;
        }

        FunctionReference methodReference = (FunctionReference) context;
        String name = methodReference.getName();

        if(name == null || !Arrays.asList(funcName).contains(name)) {
            return false;
        }

        ParameterBag currentIndex = getCurrentParameterIndex(psiElement);
        if(currentIndex == null) {
            return false;
        }

        return !(wantIndex >= 0 && currentIndex.getIndex() != wantIndex);

    }

    @Nullable
    public static ParameterBag getCurrentParameterIndex(PsiElement psiElement) {

        if (!(psiElement.getContext() instanceof ParameterList)) {
            return null;
        }

        ParameterList parameterList = (ParameterList) psiElement.getContext();
        if (!(parameterList.getContext() instanceof ParameterListOwner)) {
            return null;
        }

        return getCurrentParameterIndex(parameterList.getParameters(), psiElement);
    }

    @Nullable
    public static ParameterBag getCurrentParameterIndex(PsiElement[] parameters, PsiElement parameter) {
        int i;
        for(i = 0; i < parameters.length; i = i + 1) {
            if(parameters[i].equals(parameter)) {
                return new ParameterBag(i, parameters[i]);
            }
        }

        return null;
    }

    @Nullable
    public static PsiElement[] getMethodParameterReferences(Method method, int parameterIndex) {

        // we dont have a parameter on resolved method
        Parameter[] parameters = method.getParameters();
        if(parameters.length == 0 || parameterIndex >= parameters.length) {
            return null;
        }

        final String tempVariableName = parameters[parameterIndex].getName();
        return PsiTreeUtil.collectElements(method.getLastChild(), new PsiElementFilter() {
            @Override
            public boolean isAccepted(PsiElement element) {
                return element instanceof Variable && tempVariableName.equals(((Variable) element).getName());
            }
        });

    }

    @Nullable
    public static ArrayCreationExpression getCompletableArrayCreationElement(PsiElement psiElement) {

        // array('<test>' => '')
        if(PhpPatterns.psiElement(PhpElementTypes.ARRAY_KEY).accepts(psiElement.getContext())) {
            PsiElement arrayKey = psiElement.getContext();
            if(arrayKey != null) {
                PsiElement arrayHashElement = arrayKey.getContext();
                if(arrayHashElement instanceof ArrayHashElement) {
                    PsiElement arrayCreationExpression = arrayHashElement.getContext();
                    if(arrayCreationExpression instanceof ArrayCreationExpression) {
                        return (ArrayCreationExpression) arrayCreationExpression;
                    }
                }
            }

        }

        // on array creation key dont have value, so provide completion here also
        // array('foo' => 'bar', '<test>')
        if(PhpPatterns.psiElement(PhpElementTypes.ARRAY_VALUE).accepts(psiElement.getContext())) {
            PsiElement arrayKey = psiElement.getContext();
            if(arrayKey != null) {
                PsiElement arrayCreationExpression = arrayKey.getContext();
                if(arrayCreationExpression instanceof ArrayCreationExpression) {
                    return (ArrayCreationExpression) arrayCreationExpression;
                }

            }

        }

        return null;
    }

    @Nullable
    public static String getStringValue(@Nullable PsiElement psiElement) {
        return getStringValue(psiElement, 0);
    }

    @Nullable
    private static String getStringValue(@Nullable PsiElement psiElement, int depth) {

        if(psiElement == null || ++depth > 5) {
            return null;
        }

        if(psiElement instanceof StringLiteralExpression) {
            String resolvedString = ((StringLiteralExpression) psiElement).getContents();
            if(StringUtils.isEmpty(resolvedString)) {
                return null;
            }

            return resolvedString;
        }

        if(psiElement instanceof Field) {
            return getStringValue(((Field) psiElement).getDefaultValue(), depth);
        }

        if(psiElement instanceof PhpReference) {

            PsiReference psiReference = psiElement.getReference();
            if(psiReference == null) {
                return null;
            }

            PsiElement ref = psiReference.resolve();
            if(ref instanceof PhpReference) {
                return getStringValue(psiElement, depth);
            }

            if(ref instanceof Field) {
                PsiElement resolved = ((Field) ref).getDefaultValue();

                if(resolved instanceof StringLiteralExpression) {
                    return ((StringLiteralExpression) resolved).getContents();
                }
            }

        }

        return null;

    }

    /**
     * $this->methodName('service_name')
     * $this->methodName(SERVICE::NAME)
     * $this->methodName($this->name)
     */
    static public boolean isMethodWithFirstStringOrFieldReference(PsiElement psiElement, String... methodName) {

        if(!PlatformPatterns
            .psiElement(PhpElementTypes.METHOD_REFERENCE)
            .withChild(PlatformPatterns
                    .psiElement(PhpElementTypes.PARAMETER_LIST)
                    .withFirstChild(PlatformPatterns.or(
                        PlatformPatterns.psiElement(PhpElementTypes.STRING),
                        PlatformPatterns.psiElement(PhpElementTypes.FIELD_REFERENCE),
                        PlatformPatterns.psiElement(PhpElementTypes.CLASS_CONSTANT_REFERENCE)
                    ))
            ).accepts(psiElement)) {

            return false;
        }

        // cant we move it up to PlatformPatterns? withName condition dont looks working
        String methodRefName = ((MethodReference) psiElement).getName();

        return null != methodRefName && Arrays.asList(methodName).contains(methodRefName);
    }


    /**
     * Get array creation element and array key path:
     * ['test' => ['test2' => '<cursor>']]
     *
     * results in string path: ["test2", "test"]
     *
     *
     * @param current PsiLeaf or string array which is inside ARRAY_VALUE context
     * @param keyPath key path in reserve order
     * @return array element
     */
    @Nullable
    public static ArrayCreationExpression getArrayPath(final @NotNull PsiElement current, @NotNull String... keyPath) {

        // we need a php psielement like string element,
        // but we also support leaf items directly out of completion context
        PsiElement psiElement = current;
        if(current instanceof LeafPsiElement) {
            psiElement = current.getParent();
            if(psiElement == null) {
                return null;
            }
        }

        for (int i = 0; i < keyPath.length; i++) {

            // exit on invalid item
            psiElement = getArrayPathValue(psiElement, keyPath[i]);
            if(psiElement == null) {
                return null;
            }

            // last item we are done here
            if(i == keyPath.length - 1) {
                return (ArrayCreationExpression) psiElement;
            }

        }

        return null;

    }


    /**
     *
     * array('key' => '<cursor>')
     *
     * Helper for to get find value getArrayPath
     * @param psiElement array value as leaf
     * @param key key name in current content
     * @return parent array creation element
     */
    @Nullable
    private static ArrayCreationExpression getArrayPathValue(PsiElement psiElement, String key) {

        PsiElement arrayValue = psiElement.getContext();
        if(arrayValue == null) {
            return null;
        }

        if(arrayValue.getNode().getElementType() == PhpElementTypes.ARRAY_VALUE) {
            PsiElement arrayHashElement = arrayValue.getContext();
            if(arrayHashElement instanceof ArrayHashElement) {
                PhpPsiElement arrayKey = ((ArrayHashElement) arrayHashElement).getKey();
                if(arrayKey instanceof StringLiteralExpression && ((StringLiteralExpression) arrayKey).getContents().equals(key)) {
                    PsiElement innerArrayKey = arrayKey.getParent();
                    if(innerArrayKey != null && innerArrayKey.getNode().getElementType() == PhpElementTypes.ARRAY_KEY) {
                        PsiElement innerArrayHashElement = innerArrayKey.getParent();
                        if(innerArrayHashElement instanceof ArrayHashElement) {
                            PsiElement arrayCreation = innerArrayHashElement.getParent();
                            if(arrayCreation instanceof ArrayCreationExpression) {
                                return (ArrayCreationExpression) arrayCreation;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Find a string return value of a method context "function() { return 'foo'}"
     * First match wins
     */
    @Nullable
    static public String getMethodReturnAsString(@NotNull Method method) {

        final Set<String> values = new HashSet<String>();
        method.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {

                if(PhpElementsUtil.getMethodReturnPattern().accepts(element)) {
                    String value = PhpElementsUtil.getStringValue(element);
                    if(value != null && StringUtils.isNotBlank(value)) {
                        values.add(value);
                    }
                }

                super.visitElement(element);
            }
        });

        if(values.size() == 0) {
            return null;
        }

        // we support only first item
        return values.iterator().next();
    }

    /**
     * return 'value' inside class method
     */
    static public PsiElementPattern.Capture<StringLiteralExpression> getMethodReturnPattern() {
        return PlatformPatterns
                .psiElement(StringLiteralExpression.class)
                .withParent(PlatformPatterns.psiElement(PhpReturn.class).inside(Method.class))
                .withLanguage(PhpLanguage.INSTANCE);
    }

    public static boolean isEqualClassName(@NotNull PhpClass phpClass, @NotNull PhpClass compareClassName) {
        return isEqualClassName(phpClass, compareClassName.getPresentableFQN());
    }

    public static boolean isEqualClassName(@Nullable PhpClass phpClass, @Nullable String compareClassName) {

        if(phpClass == null || compareClassName == null) {
            return false;
        }

        String phpClassName = phpClass.getPresentableFQN();
        if(phpClassName == null) {
            return false;
        }

        if(phpClassName.startsWith("\\")) {
            phpClassName = phpClassName.substring(1);
        }

        if(compareClassName.startsWith("\\")) {
            compareClassName = compareClassName.substring(1);
        }

        return phpClassName.equals(compareClassName);
    }

}
