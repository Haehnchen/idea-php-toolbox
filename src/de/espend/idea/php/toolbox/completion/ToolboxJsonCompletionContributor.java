package de.espend.idea.php.toolbox.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import de.espend.idea.php.toolbox.PhpToolboxIcons;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ToolboxJsonCompletionContributor extends CompletionContributor {

    public ToolboxJsonCompletionContributor() {

        // "provider":"twig_ext"
        extend(CompletionType.BASIC, getPattern("provider"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                for (PhpToolboxProviderInterface provider : ExtensionProviderUtil.getProviders(completionParameters.getPosition().getProject())) {

                    LookupElementBuilder lookupElement = LookupElementBuilder
                        .create(provider.getName())
                        .withIcon(PhpToolboxIcons.TOOLBOX);

                    String s = provider.getClass().toString();
                    int i = s.lastIndexOf(".");
                    if(i > 0) {
                        lookupElement = lookupElement.withTypeText(s.substring(i + 1), true);
                    }

                    completionResultSet.addElement(lookupElement);
                }
            }
        });

        // "language":"twig"
        extend(CompletionType.BASIC, getPattern("language"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                for (String language : new String[] {"php", "twig"}) {
                    completionResultSet.addElement(
                        LookupElementBuilder.create(language).withIcon(PhpToolboxIcons.TOOLBOX)
                    );
                }
            }
        });

        // "function":"date"
        extend(CompletionType.BASIC, getPattern("function"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                for (String s : PhpIndex.getInstance(completionParameters.getPosition().getProject()).getAllFunctionNames(PrefixMatcher.ALWAYS_TRUE)) {
                    completionResultSet.addElement(LookupElementBuilder.create(s).withIcon(com.jetbrains.php.PhpIcons.METHOD));
                }
            }
        });

        // "class":"date"
        extend(CompletionType.BASIC, getPattern("class"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
                PhpIndex phpIndex = PhpIndex.getInstance(completionParameters.getPosition().getProject());
                for (String className : phpIndex.getAllClassNames(resultSet.getPrefixMatcher())) {

                    for(PhpClass phpClass: phpIndex.getClassesByName(className)) {
                        resultSet.addElement(new MyPhpLookupElement(phpClass));
                    }

                    for(PhpClass phpClass: phpIndex.getInterfacesByName(className)) {
                        resultSet.addElement(new MyPhpLookupElement(phpClass));
                    }
                }
            }
        });

        // "icon":"com.jetbrains.php.PhpIcons"
        extend(CompletionType.BASIC, getPattern("icon"), new MyIconCompletionProvider());

        // "contributor":"date"
        extend(CompletionType.BASIC, getPattern("contributor"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
                for (SourceContributorInterface sourceContributor : ExtensionProviderUtil.getSourceContributors()) {
                    resultSet.addElement(
                        LookupElementBuilder.create(sourceContributor.getName()).withIcon(PhpToolboxIcons.TOOLBOX)
                    );
                }
            }
        });

        // "type":"date"
        extend(CompletionType.BASIC, getPattern("type"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
                for (String s : new String[] {"default", "return", "array_key"}) {
                    resultSet.addElement(
                        LookupElementBuilder.create(s).withIcon(PhpToolboxIcons.TOOLBOX)
                    );
                }
            }
        });
    }

    private void addKnownIconClasses(@NotNull CompletionResultSet resultSet) {
        String[] strings = {
            "icons.TwigIcons",
            "com.jetbrains.php.PhpIcons",
            "fr.adrienbrault.idea.symfony2plugin.Symfony2Icons",
            "com.intellij.icons.AllIcons",
            "com.intellij.util.PlatformIcons",
            "de.espend.idea.php.toolbox.PhpToolboxIcons",
        };

        for (String clazz : strings) {
            try {
                Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                continue;
            }

            resultSet.addElement(LookupElementBuilder.create(clazz).withIcon(PhpToolboxIcons.TOOLBOX));
        }
    }

    public PsiElementPattern.Capture<PsiElement> getPattern(@NotNull String key) {
        return PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile().withName(PlatformPatterns.string().endsWith("toolbox.metadata.json"))).withParent(
            PlatformPatterns.psiElement(JsonStringLiteral.class).withParent(
                PlatformPatterns.psiElement(JsonProperty.class).withFirstChild(
                    PlatformPatterns.psiElement(JsonStringLiteral.class).withText("\"" + key + "\"")
                )
            )
        );
    }

    private static class MyPhpLookupElement extends PhpLookupElement {
        public MyPhpLookupElement(@NotNull PhpNamedElement namedElement) {
            super(namedElement);
            this.handler = PhpReferenceTrimBackslashInsertHandler.getInstance();
        }
    }

    private static class PhpReferenceTrimBackslashInsertHandler implements InsertHandler<LookupElement> {

        private static final PhpReferenceTrimBackslashInsertHandler instance = new PhpReferenceTrimBackslashInsertHandler();

        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {

            // reuse core class + namespace insertHandler
            PhpReferenceInsertHandler.getInstance().handleInsert(context, lookupElement);

            // phpstorm8: remove leading backslash on PhpReferenceInsertHandler
            String backslash = context.getDocument().getText(new TextRange(context.getStartOffset(), context.getStartOffset() + 1));
            if("\\".equals(backslash)) {
                context.getDocument().deleteString(context.getStartOffset(), context.getStartOffset() + 1);
            }
        }

        public static PhpReferenceTrimBackslashInsertHandler getInstance(){
            return instance;
        }

    }

    private static String removeIdeaRuleHack(@NotNull String value) {
        // wtf: ???
        // looks like current cursor position is marked :)
        return value.replace("IntellijIdeaRulezzz", "").replace("IntellijIdeaRulezzz ", "").trim();
    }

    private class MyIconCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {

            PsiElement position = completionParameters.getPosition();
            PsiElement parent = position.getParent();
            if(!(parent instanceof JsonStringLiteral)) {
                return;
            }

            String value = removeIdeaRuleHack(((JsonStringLiteral) parent).getValue());

            if(value.length() == 0) {
                addKnownIconClasses(resultSet);
                return;
            }

            String[] split = value.split("\\.");
            if(split.length == 0) {
                return;
            }

            // strip fields
            // com.jetbrains.php.PhpIcons."ABS"
            String clazz = value;
            if(split[split.length - 1].toUpperCase().equals(split[split.length - 1])) {
                clazz = clazz.substring(0, clazz.lastIndexOf("."));
            };

            // strip empty namespace
            // com.jetbrains.php.PhpIcons.
            if(clazz.endsWith(".")) {
                clazz = clazz.substring(0, clazz.length() - 1);
            }

            Class<?> iconClass;
            try {
                iconClass = Class.forName(clazz);
            } catch (ClassNotFoundException e) {
                return;
            }

            for (Field field : iconClass.getDeclaredFields()) {
                int modifiers = field.getModifiers();

                if(!Modifier.isStatic(modifiers) ||
                    !Modifier.isPublic(modifiers) ||
                    !field.getType().isAssignableFrom(Icon.class)
                    )
                {
                    continue;
                }

                LookupElementBuilder lookupElement = LookupElementBuilder.create(clazz + "." + field.getName());

                try {
                    Object o = field.get(null);
                    if(o instanceof Icon) {
                        int iconWidth = ((Icon) o).getIconWidth();
                        int iconHeight = ((Icon) o).getIconHeight();

                        lookupElement = lookupElement.withTypeText(String.format("(%sx%s)", iconWidth, iconHeight), true);

                        // dont explode completion window; 32 for "@x2"
                        if( iconWidth <= 32 && iconHeight <= 32) {
                            lookupElement = lookupElement.withIcon((Icon) o);
                        }
                    }
                } catch (IllegalAccessException e) {
                    return;
                }

                resultSet.addElement(lookupElement);
            }
        }
    }
}
