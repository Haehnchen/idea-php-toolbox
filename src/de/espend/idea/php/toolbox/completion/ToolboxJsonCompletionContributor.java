package de.espend.idea.php.toolbox.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.PsiFilePattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import de.espend.idea.php.toolbox.PhpToolboxIcons;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.provider.presentation.ProviderPresentation;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
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
        extend(CompletionType.BASIC, getNextToPropertyPattern("provider"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {

                for (PhpToolboxProviderInterface provider : ExtensionProviderUtil.getProviders(completionParameters.getPosition().getProject())) {

                    LookupElementBuilder lookupElement = LookupElementBuilder
                        .create(provider.getName());

                    ProviderPresentation presentation = provider.getPresentation();
                    String description = null;
                    if(presentation != null) {
                        if(presentation.getIcon() != null) {
                            lookupElement = lookupElement.withIcon(presentation.getIcon());
                        }
                        description = presentation.getDescription();
                    } else {
                        lookupElement = lookupElement.withIcon(PhpToolboxIcons.TOOLBOX);
                    }

                    // Overwrite with class name
                    if(description == null) {
                        String s = provider.getClass().toString();
                        int i = s.lastIndexOf(".");
                        if(i > 0) {
                            lookupElement = lookupElement.withTypeText(s.substring(i + 1), true);
                        }
                    }

                    if(description != null) {
                        lookupElement = lookupElement.withTypeText(description, true);
                    }

                    completionResultSet.addElement(lookupElement);
                }
            }
        });

        // "language":"twig"
        extend(
            CompletionType.BASIC,
            getNextToPropertyPattern("language"),
            new MyStringCompletionProvider("php", "twig")
        );

        // "function":"date"
        extend(CompletionType.BASIC, getNextToPropertyPattern("function"), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
                for (String s : PhpIndex.getInstance(completionParameters.getPosition().getProject()).getAllFunctionNames(PrefixMatcher.ALWAYS_TRUE)) {
                    completionResultSet.addElement(LookupElementBuilder.create(s).withIcon(com.jetbrains.php.PhpIcons.METHOD));
                }
            }
        });

        // "class":"date"
        extend(CompletionType.BASIC, getNextToPropertyPattern("class"), new CompletionProvider<CompletionParameters>() {
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
        extend(CompletionType.BASIC, getNextToPropertyPattern("icon"), new MyIconCompletionProvider());

        // "contributor":"date"
        extend(CompletionType.BASIC, getNextToPropertyPattern("contributor"), new CompletionProvider<CompletionParameters>() {
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
        extend(
            CompletionType.BASIC,
            getNextToPropertyPattern("type"),
            new MyStringCompletionProvider("default", "return", "array_key", "type")
        );

        // "signatures":[{"date": "foo"}]
        extend(
            CompletionType.BASIC,
            getAfterPropertyAndInsideArrayObjectPattern("signatures"),
            new MyStringCompletionProvider("index", "type", "array", "function", "class", "method")
        );

        // "defaults|items":[{"date": "foo"}]
        extend(
            CompletionType.BASIC,
            PlatformPatterns.or(getAfterPropertyAndInsideArrayObjectPattern("defaults"), getAfterPropertyAndInsideArrayObjectPattern("items")),
            new MyStringCompletionProvider("lookup_string", "presentable_text", "type_text", "type", "icon", "target")
        );

        // "providers":[{"date": "foo"}]
        extend(
            CompletionType.BASIC,
            getAfterPropertyAndInsideArrayObjectPattern("providers"),
            new MyStringCompletionProvider("name", "items", "defaults", "source", "lookup_strings")
        );

        // "providers":[{"date": "foo"}]
        extend(CompletionType.BASIC,
            getAfterPropertyAndInsideArrayObjectPattern("registrar"),
            new MyStringCompletionProvider("signatures", "provider", "language", "signature", "parameters")
        );

        // root: {"providers": ..., "registrar": ...}
        extend(
            CompletionType.BASIC,
            getPropertyAfterRootPattern(),
            new MyStringCompletionProvider("providers", "registrar")
        );

        // "source":[{"date": "foo"}]
        extend(CompletionType.BASIC,
            getAfterPropertyAndInsideObjectPattern("source"),
            new MyStringCompletionProvider("contributor", "parameter")
        );
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

    /**
     * "key": "<caret>"
     */
    public PsiElementPattern.Capture<PsiElement> getNextToPropertyPattern(@NotNull String key) {
        return PlatformPatterns.psiElement().inFile(getMetadataFilePattern()).withParent(
            PlatformPatterns.psiElement(JsonStringLiteral.class).withParent(
                PlatformPatterns.psiElement(JsonProperty.class).withFirstChild(
                    PlatformPatterns.psiElement(JsonStringLiteral.class).withText("\"" + key + "\"")
                )
            )
        );
    }

    private PsiFilePattern.Capture<PsiFile> getMetadataFilePattern() {
        return PlatformPatterns.psiFile().withName(PlatformPatterns.string().endsWith("toolbox.metadata.json"));
    }

    /**
     * foo:[{"key": "<caret>"}]
     */
    public PsiElementPattern.Capture<PsiElement> getAfterPropertyAndInsideArrayObjectPattern(@NotNull String key) {
        return PlatformPatterns.psiElement().inFile(getMetadataFilePattern()).withParent(
            PlatformPatterns.psiElement(JsonStringLiteral.class).with(FirstItemInTreePatternCondition.getInstance()).withParent(
                PlatformPatterns.psiElement(JsonProperty.class).withParent(
                    PlatformPatterns.psiElement(JsonObject.class).withParent(
                        PlatformPatterns.psiElement(JsonArray.class).withParent(
                            PlatformPatterns.psiElement(JsonProperty.class).withFirstChild(
                                PlatformPatterns.psiElement(JsonStringLiteral.class).withText("\"" + key + "\"")
                            )
                        )
                    )
                )
            )
        );
    }

    /**
     * foo:{"key": "<caret>"}
     */
    public PsiElementPattern.Capture<PsiElement> getAfterPropertyAndInsideObjectPattern(@NotNull String key) {
        return PlatformPatterns.psiElement().inFile(getMetadataFilePattern()).withParent(
            PlatformPatterns.psiElement(JsonStringLiteral.class).with(FirstItemInTreePatternCondition.getInstance()).withParent(
                PlatformPatterns.psiElement(JsonProperty.class).withParent(
                    PlatformPatterns.psiElement(JsonObject.class).withParent(

                            PlatformPatterns.psiElement(JsonProperty.class).withFirstChild(
                                PlatformPatterns.psiElement(JsonStringLiteral.class).withText("\"" + key + "\"")
                            )

                    )
                )
            )
        );
    }

    /**
     * foo:[{"key": "<caret>"}]
     */
    public PsiElementPattern.Capture<PsiElement> getPropertyAfterRootPattern() {
        return PlatformPatterns.psiElement().inFile(getMetadataFilePattern()).withParent(
            PlatformPatterns.psiElement(JsonStringLiteral.class).withParent(
                PlatformPatterns.psiElement(JsonProperty.class).withParent(
                    PlatformPatterns.psiElement(JsonObject.class).withParent(
                        PlatformPatterns.psiElement(JsonFile.class)
                    )
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

    private static class FirstItemInTreePatternCondition extends PatternCondition<PsiElement> {

        private static FirstItemInTreePatternCondition instance;

        public static FirstItemInTreePatternCondition getInstance() {
            return instance != null ? instance : (instance = new FirstItemInTreePatternCondition());
        }

        private FirstItemInTreePatternCondition() {
            super("FirstItemInTree");
        }

        @Override
        public boolean accepts(@NotNull PsiElement psiElement, ProcessingContext processingContext) {
            return psiElement.getPrevSibling() == null;
        }
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

    private static class MyStringCompletionProvider extends CompletionProvider<CompletionParameters> {

        private String[] strings;

        public MyStringCompletionProvider(@NotNull String... strings) {
            this.strings = strings;
        }

        @Override
        protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
            for (String s : strings) {
                resultSet.addElement(
                    LookupElementBuilder.create(s).withIcon(PhpToolboxIcons.TOOLBOX)
                );
            }
        }
    }
}
