package de.espend.idea.php.toolbox.provider;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import fr.adrienbrault.idea.symfony2plugin.codeInsight.utils.PhpElementsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ReturnSignatureProvider implements PhpToolboxProviderInterface {

    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull PhpToolboxCompletionContributorParameter parameter) {

        String className = "\\Twig_ExtensionInterface:getName";
        String[] split = className.split(":");

        Collection<LookupElement> lookupElements = new ArrayList<LookupElement>();

        for (PhpClass phpClass : PhpIndex.getInstance(parameter.getProject()).getAllSubclasses(split[0])) {

            Method method = phpClass.findOwnMethodByName("getName");
            if(method == null) {
                continue;
            }

            String string = PhpElementsUtil.getMethodReturnAsString(method);
            if(string != null) {
                lookupElements.add(LookupElementBuilder.create(string));
            }
        }

        return lookupElements;
    }

    @NotNull
    @Override
    public Collection<PsiElement> getPsiTargets(@NotNull PhpToolboxDeclarationHandlerParameter parameter) {

        String className = "\\Twig_ExtensionInterface:getName";
        String[] split = className.split(":");

        Collection<PsiElement> psiElements = new ArrayList<PsiElement>();

        for (PhpClass phpClass : PhpIndex.getInstance(parameter.getProject()).getAllSubclasses(split[0])) {

            Method method = phpClass.findOwnMethodByName("getName");
            if(method == null) {
                continue;
            }

            String string = PhpElementsUtil.getMethodReturnAsString(method);
            if(string != null && string.equals(parameter.getContents())) {
                psiElements.add(phpClass);
            }
        }

        return psiElements;
    }

    @NotNull
    @Override
    public String getName() {
        return "return";
    }


}
