package de.espend.idea.php.toolbox.dict.json;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import de.espend.idea.php.toolbox.completion.dict.JsonLookupElement;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JsonRawContainerProvider implements PhpToolboxProviderInterface {

    private final String name;
    private final Collection<JsonRawLookupElement> items;

    public JsonRawContainerProvider(String name, Collection<JsonRawLookupElement> items) {
        this.name = name;
        this.items = items;
    }

    @NotNull
    @Override
    public Collection<LookupElement> getLookupElements(@NotNull PhpToolboxCompletionContributorParameter parameter) {

        Collection<LookupElement> elements = new ArrayList<LookupElement>();

        for (JsonRawLookupElement item : items) {
            elements.add(new JsonLookupElement(item));
        }

        return elements;
    }

    @NotNull
    @Override
    public Collection<PsiElement> getPsiTargets(@NotNull PhpToolboxDeclarationHandlerParameter parameter) {

        for (JsonRawLookupElement item : items) {

            if(item.getTarget() == null) {
                continue;
            }

            String lookupString = item.getLookupString();
            if(lookupString != null && lookupString.equals(parameter.getContents())) {
                System.out.println(item.getTarget());
            }

        }

        return Collections.emptyList();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

}
