package de.espend.idea.php.toolbox.dict.json;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import de.espend.idea.php.toolbox.completion.dict.JsonLookupElement;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderAbstract;
import de.espend.idea.php.toolbox.gotoCompletion.contributor.GlobalStringClassGoto;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.type.PhpToolboxTypeProviderArguments;
import de.espend.idea.php.toolbox.type.PhpToolboxTypeProviderInterface;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonRawContainerProvider extends PhpToolboxProviderAbstract implements PhpToolboxTypeProviderInterface {

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
        Collection<PsiElement> psiElements = new ArrayList<PsiElement>();

        for (JsonRawLookupElement item : items) {
            if(item.getTarget() == null) {
                continue;
            }

            String lookupString = item.getLookupString();
            if(lookupString != null && lookupString.equals(parameter.getContents())) {
                Collections.addAll(psiElements, GlobalStringClassGoto.getPsiElements(parameter.getProject(), item.getTarget()));
            }
        }

        return psiElements;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public Collection<PhpNamedElement> resolveParameter(@NotNull PhpToolboxTypeProviderArguments args) {

        Collection<PhpNamedElement> elements = new ArrayList<PhpNamedElement>();

        for (JsonRawLookupElement jsonRawLookupElement: args.getLookupElements()) {
            String type = jsonRawLookupElement.getType();
            if(type == null || StringUtils.isBlank(type)) {
                continue;
            }

            type = type.replaceAll("\\\\+", "\\\\");

            // internal fully fqn needed by converter since phpstorm9;
            // we normalize it on our side for a unique collection
            if(!type.startsWith("\\")) {
                type = "\\" + type;
            }

            if(args.getParameter().equals(jsonRawLookupElement.getLookupString())) {
                elements.addAll(PhpIndex.getInstance(args.getProject()).getAnyByFQN(type));
            }
        }

        return elements;
    }
}
