package de.espend.idea.php.toolbox.navigation.dict;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpToolboxDeclarationHandlerParameter {

    @NotNull
    private final PsiElement psiElement;

    @NotNull
    private final String element;

    @NotNull
    private final FileType fileType;

    public PhpToolboxDeclarationHandlerParameter(@NotNull PsiElement psiElement, @NotNull String element, @NotNull FileType fileType) {
        this.psiElement = psiElement;
        this.element = element;
        this.fileType = fileType;
    }

    @NotNull
    public String getContents() {
        return element;
    }

    @NotNull
    public Project getProject() {
        return psiElement.getProject();
    }

    @NotNull
    public PsiElement getPsiElement() {
        return psiElement;
    }

    @NotNull
    public FileType getFileType() {
        return fileType;
    }
}
