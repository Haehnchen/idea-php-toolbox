package de.espend.idea.php.toolbox.dict.matcher;

import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LanguageMatcherParameter {

    @NotNull
    private final PsiElement psiElement;

    @NotNull
    private final JsonRegistrar registrar;

    public LanguageMatcherParameter(@NotNull PsiElement psiElement, @NotNull JsonRegistrar registrar) {
        this.psiElement = psiElement;
        this.registrar = registrar;
    }

    @NotNull
    public PsiElement getElement() {
        return psiElement;
    }

    @NotNull
    public JsonRegistrar getRegistrar() {
        return registrar;
    }

    @NotNull
    public Collection<String> getSignatures() {
        return getRegistrar().getSignatures();
    }
}
