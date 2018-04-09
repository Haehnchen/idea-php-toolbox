package de.espend.idea.php.toolbox.symfony.util;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.*;
import de.espend.idea.php.toolbox.symfony.Symfony2InterfacesUtil;
import de.espend.idea.php.toolbox.symfony.utils.PhpElementsUtil;
import de.espend.idea.php.toolbox.symfony.dic.MethodReferenceBag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MethodMatcher {

    @Nullable
    public static MethodMatchParameter getMatchedSignatureWithDepth(PsiElement psiElement, CallToSignature... callToSignatures) {
        return getMatchedSignatureWithDepth(psiElement, callToSignatures, 0);
    }

    @Nullable
    public static MethodMatchParameter getMatchedSignatureWithDepth(PsiElement psiElement, CallToSignature[] callToSignatures, int defaultParameterIndex) {

        MethodMatchParameter methodMatchParameter = new StringParameterMatcher(psiElement, defaultParameterIndex)
            .withSignature(callToSignatures)
            .match();

        if(methodMatchParameter != null) {
            return methodMatchParameter;
        }

        // try on resolved method
        return new StringParameterRecursiveMatcher(psiElement, defaultParameterIndex)
            .withSignature(callToSignatures)
            .match();
    }

    public static class CallToSignature {

        private final String instance;
        private final String method;

        public CallToSignature(String instance, String method) {
            this.instance = instance;
            this.method = method;
        }

        public String getInstance() {
            return instance;
        }

        public String getMethod() {
            return method;
        }
    }

    public static class MethodMatchParameter {

        final private CallToSignature signature;
        final private ParameterBag parameterBag;
        final private PsiElement[] parameters;
        final private MethodReference methodReference;

        public MethodMatchParameter(CallToSignature signature, ParameterBag parameterBag, PsiElement[] parameters, MethodReference methodReference) {
            this.signature = signature;
            this.parameterBag = parameterBag;
            this.parameters = parameters;
            this.methodReference = methodReference;
        }

        @Nullable
        public CallToSignature getSignature() {
            return signature;
        }

        public ParameterBag getParameterBag() {
            return this.parameterBag;
        }

        public PsiElement[] getParameters() {
            return parameters;
        }

        public MethodReference getMethodReference() {
            return methodReference;
        }

    }

    public static class StringParameterMatcher extends AbstractMethodParameterMatcher {

        public StringParameterMatcher(PsiElement psiElement, int parameterIndex) {
            super(psiElement, parameterIndex);
        }

        @Nullable
        public MethodMatchParameter match() {

            MethodReferenceBag bag = PhpElementsUtil.getMethodParameterReferenceBag(psiElement, this.parameterIndex);
            if(bag == null) {
                return null;
            }

            CallToSignature matchedMethodSignature = this.isCallTo(bag.getMethodReference());
            if(matchedMethodSignature == null) {
                return null;
            }

            return new MethodMatchParameter(matchedMethodSignature, bag.getParameterBag(), bag.getParameterList().getParameters(), bag.getMethodReference());
        }

    }

    public static class StringParameterAnyMatcher extends AbstractMethodParameterMatcher {

        public StringParameterAnyMatcher(PsiElement psiElement) {
            super(psiElement, -1);
        }

        @Nullable
        public MethodMatchParameter match() {

            MethodReferenceBag bag = PhpElementsUtil.getMethodParameterReferenceBag(psiElement);
            if(bag == null) {
                return null;
            }

            CallToSignature matchedMethodSignature = this.isCallTo(bag.getMethodReference());
            if(matchedMethodSignature == null) {
                return null;
            }

            return new MethodMatchParameter(matchedMethodSignature, bag.getParameterBag(), bag.getParameterList().getParameters(), bag.getMethodReference());
        }

    }

    public static class StringParameterRecursiveMatcher extends AbstractMethodParameterMatcher {

        public StringParameterRecursiveMatcher(PsiElement psiElement, int parameterIndex) {
            super(psiElement, parameterIndex);
        }

        @Nullable
        public MethodMatchParameter match() {

            MethodReferenceBag bag = PhpElementsUtil.getMethodParameterReferenceBag(psiElement);
            if(bag == null) {
                return null;
            }

            // try on current method
            MethodMatchParameter methodMatchParameter = new StringParameterMatcher(psiElement, parameterIndex)
                .withSignature(this.signatures)
                .match();

            if(methodMatchParameter != null) {
                return methodMatchParameter;
            }

            // walk down next method
            MethodReference methodReference = bag.getMethodReference();
            Method[] methods = Symfony2InterfacesUtil.getMultiResolvedMethod(methodReference);
            if(methods == null) {
                return null;
            }

            for (Method method : methods) {

                PsiElement[] parameterReferences = PhpElementsUtil.getMethodParameterReferences(method, bag.getParameterBag().getIndex());
                if(parameterReferences == null || parameterReferences.length == 0) {
                    continue;
                }

                for(PsiElement var: parameterReferences) {

                    MethodMatcher.MethodMatchParameter methodMatchParameterRef = new MethodMatcher.StringParameterMatcher(var, parameterIndex)
                            .withSignature(this.signatures)
                            .match();

                    if(methodMatchParameterRef != null) {
                        return methodMatchParameterRef;
                    }

                }

            }

            return null;
        }

    }


    /**
     * Matches: new \DateTime('FOO')
     */
    public static class NewExpressionParameterMatcher extends AbstractMethodParameterMatcher {

        public NewExpressionParameterMatcher(PsiElement psiElement, int parameterIndex) {
            super(psiElement, parameterIndex);
        }

        @Nullable
        public MethodMatchParameter match() {

            for (CallToSignature signature : this.signatures) {
                if(matchInstance(psiElement, signature.getInstance(), this.parameterIndex)) {
                    return new MethodMatchParameter(signature, null, null, null);
                }
            }

            return null;
        }

        private boolean matchInstance(PsiElement psiElement, String instance, int index) {

            PsiElement parameterList = psiElement.getParent();
            if(parameterList instanceof ParameterList) {
                ParameterBag currentParameterIndex = PhpElementsUtil.getCurrentParameterIndex(psiElement);
                if(currentParameterIndex != null && currentParameterIndex.getIndex() == index) {
                    PsiElement newExpression = parameterList.getParent();
                    if(newExpression instanceof NewExpression) {
                        ClassReference classReference = ((NewExpression) newExpression).getClassReference();
                        if(classReference != null) {
                            String fqn = classReference.getFQN();
                            if(fqn != null) {
                                return new Symfony2InterfacesUtil().isInstanceOf(psiElement.getProject(), fqn, instance);
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    public static class ArrayParameterMatcher extends AbstractMethodParameterMatcher {

        public ArrayParameterMatcher(PsiElement psiElement, int parameterIndex) {
            super(psiElement, parameterIndex);
        }

        @Nullable
        public MethodMatchParameter match() {

            ArrayCreationExpression arrayCreationExpression = PhpElementsUtil.getCompletableArrayCreationElement(this.psiElement);
            if (arrayCreationExpression == null) {
                return null;
            }

            PsiElement parameterList = arrayCreationExpression.getContext();
            if (!(parameterList instanceof ParameterList)) {
                return null;
            }

            PsiElement methodParameters[] = ((ParameterList) parameterList).getParameters();
            if (methodParameters.length < this.parameterIndex) {
                return null;
            }

            if (!(parameterList.getContext() instanceof MethodReference)) {
                return null;
            }
            MethodReference methodReference = (MethodReference) parameterList.getContext();

            ParameterBag currentIndex = PsiElementUtils.getCurrentParameterIndex(arrayCreationExpression);
            if (currentIndex == null || currentIndex.getIndex() != this.parameterIndex) {
                return null;
            }

            CallToSignature matchedMethodSignature = this.isCallTo(methodReference);
            if(matchedMethodSignature == null) {
                return null;
            }

            return new MethodMatchParameter(matchedMethodSignature, currentIndex, methodParameters, methodReference);
        }

    }

    public interface MethodParameterMatcherInterface {
        @Nullable
        public MethodMatchParameter match();
    }

    public abstract static class AbstractMethodParameterMatcher implements MethodParameterMatcherInterface {

        final protected List<CallToSignature> signatures;
        final protected int parameterIndex;
        final protected PsiElement psiElement;

        public AbstractMethodParameterMatcher(PsiElement psiElement, int parameterIndex) {
            this.signatures = new ArrayList<>();
            this.parameterIndex = parameterIndex;
            this.psiElement = psiElement;
        }

        public AbstractMethodParameterMatcher withSignature(String instance, String method) {
            this.signatures.add(new CallToSignature(instance, method));
            return this;
        }

        public AbstractMethodParameterMatcher withSignature(Collection<CallToSignature> signatures) {
            this.signatures.addAll(signatures);
            return this;
        }

        public AbstractMethodParameterMatcher withSignature(CallToSignature[] callToSignatures) {
            this.signatures.addAll(Arrays.asList(callToSignatures));
            return this;
        }

        @Nullable
        protected CallToSignature isCallTo(MethodReference methodReference) {
            Symfony2InterfacesUtil interfacesUtil = new Symfony2InterfacesUtil();

            for(CallToSignature signature: this.signatures) {
                if(interfacesUtil.isCallTo(methodReference, signature.getInstance(), signature.getMethod())) {
                    return signature;
                }
            }

            return null;
        }
    }
}
