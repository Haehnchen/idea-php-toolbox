package de.espend.idea.php.toolbox.matcher.php.docTag;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.toolbox.completion.dict.PhpToolboxCompletionContributorParameter;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.gotoCompletion.GotoCompletionContributor;
import de.espend.idea.php.toolbox.navigation.dict.PhpToolboxDeclarationHandlerParameter;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpDocTagGotoCompletionContributor implements GotoCompletionContributor {
    @Override
    public void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {

        PsiElement psiElement = completionParameters.getPosition().getParent();

        Collection<PhpDocParamTag> docTags = PhpDocUtil.getDocTagsForScope(psiElement);
        if(docTags == null || docTags.size() == 0) {
            return;
        }

        PhpToolboxCompletionContributorParameter parameter = null;

        for(PhpDocParamTag phpDocParamTag: docTags) {
            String providerName = extractProviderName(phpDocParamTag.getText());
            if(providerName == null) {
                continue;
            }

            List<PhpToolboxProviderInterface> filter = getProvidersByName(
                psiElement,
                providerName
            );

            if(filter.size() == 0) {
                continue;
            }

            if(parameter == null) {
                parameter = new PhpToolboxCompletionContributorParameter(completionParameters, context, resultSet, new HashSet<>());
            }

            for (PhpToolboxProviderInterface provider : filter) {
                resultSet.addAllElements(provider.getLookupElements(parameter));
            }
        }
    }

    /**
     * #Foo, #Foo, #Foo.Foo
     */
    @Nullable
    private String extractProviderName(String text) {
        Matcher matcher = Pattern.compile("\\s*#([\\.\\w-]+)\\s*").matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    @NotNull
    private List<PhpToolboxProviderInterface> getProvidersByName(PsiElement psiElement, String providerName) {
        // find provider on name or interface alias
        return ContainerUtil.filter(
            ExtensionProviderUtil.getProviders(psiElement.getProject()),
            new PhpToolboxProviderInterfaceCondition(providerName)
        );
    }

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(@NotNull PsiElement psiElement, int i, Editor editor) {

        PsiElement element = psiElement.getParent();
        if (!(element instanceof StringLiteralExpression)) {
            return new PsiElement[0];
        }

        String contents = ((StringLiteralExpression) element).getContents();
        if (StringUtils.isBlank(contents)) {
            return new PsiElement[0];
        }

        PhpToolboxDeclarationHandlerParameter args = null;

        Collection<PsiElement> targets = new ArrayList<>();

        Collection<PhpDocParamTag> docTags = PhpDocUtil.getDocTagsForScope(psiElement);
        if (docTags == null || docTags.size() == 0) {
            return new PsiElement[0];
        }

        for (PhpDocParamTag phpDocParamTag : docTags) {
            String providerName = extractProviderName(phpDocParamTag.getText());
            if (providerName == null) {
                continue;
            }

            List<PhpToolboxProviderInterface> filter = getProvidersByName(
                psiElement,
                providerName
            );

            if (filter.size() == 0) {
                continue;
            }

            if (args == null) {
                args = new PhpToolboxDeclarationHandlerParameter(psiElement, contents, psiElement.getContainingFile().getFileType());
            }

            for (PhpToolboxProviderInterface provider : filter) {
                targets.addAll(provider.getPsiTargets(args));
            }
        }

        return targets.toArray(new PsiElement[targets.size()]);
    }

    @NotNull
    @Override
    public ElementPattern<? extends PsiElement> getPattern() {
        return PlatformPatterns.psiElement().withParent(
            PlatformPatterns.psiElement(StringLiteralExpression.class)
                .withParent(ParameterList.class)
        );
    }

    private static class PhpToolboxProviderInterfaceCondition implements Condition<PhpToolboxProviderInterface> {
        private final String providerName;

        public PhpToolboxProviderInterfaceCondition(String providerName) {
            this.providerName = providerName;
        }

        @Override
        public boolean value(PhpToolboxProviderInterface provider) {
            return providerName.equalsIgnoreCase(provider.getName()) || (
                provider instanceof ToolboxDocTagAliasInterface &&
                ArrayUtils.contains(((ToolboxDocTagAliasInterface) provider).getInlineStrings(), providerName)
            );
        }
    }
}
