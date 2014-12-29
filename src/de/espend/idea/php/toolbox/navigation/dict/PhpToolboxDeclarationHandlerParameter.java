package de.espend.idea.php.toolbox.navigation.dict;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PhpToolboxDeclarationHandlerParameter {

    private final PsiElement psiElement;
    private final String element;

    public PhpToolboxDeclarationHandlerParameter(@NotNull PsiElement psiElement, @NotNull String element) {
        this.psiElement = psiElement;
        this.element = element;
    }

    @NotNull
    public String getContents() {
        return element;
    }

    @NotNull
    public Project getProject() {
        return psiElement.getProject();
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

}
