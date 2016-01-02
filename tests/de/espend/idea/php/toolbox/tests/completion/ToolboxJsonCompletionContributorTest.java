package de.espend.idea.php.toolbox.tests.completion;


import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.toolbox.completion.ToolboxJsonCompletionContributor
 */
public class ToolboxJsonCompletionContributorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void testProvider() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"provider\":\"<caret>\"}",
            "class_interface"
        );
    }

    public void testProviderIsInWrongFile() {
        assertCompletionNotContains("foo-toolbox.metadata_bar.json",
            "{\"provider\":\"<caret>\"}",
            "class_interface"
        );
    }

    public void testLanguage() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"language\":\"<caret>\"}",
            "php", "twig"
        );
    }

    public void testFunction() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"function\":\"<caret>\"}",
            "date"
        );
    }

    public void testClass() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"class\":\"<caret>\"}",
            "DateTime"
        );
    }

    public void testIconWithClassPrefix() {
        String[] strings = {
            "com.jetbrains.php.PhpIcons.METH",
            "com.jetbrains.php.PhpIcons.",
            "com.jetbrains.php.PhpIcons"
        };

        for (String s : strings) {
            assertCompletionContains("foo-toolbox.metadata.json",
                String.format("{\"icon\":\"%s<caret>\"}", s),
                "com.jetbrains.php.PhpIcons.METHOD"
            );
        }
    }

    public void testIconIsBlankFallbackToClass() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"icon\":\"<caret>\"}",
            "com.intellij.icons.AllIcons"
        );
    }

    public void testSourceContributor() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"contributor\":\"<caret>\"}",
            "return", "return_array"
        );
    }

    public void testSignatureType() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"type\":\"<caret>\"}",
            "default", "array_key", "return"
        );
    }

    public void testSignaturesKeys() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"signatures\":[{\"<caret>\"}]}",
            "array", "function", "class"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"signatures\":[{\"<caret>\":\"datetime\"}]}",
            "array", "function", "class"
        );

        assertCompletionNotContains("foo-toolbox.metadata.json",
            "{\"signatures\":[{\"foo\":\"<caret>\"}]}",
            "array", "function", "class"
        );
    }

    public void testLookupElementKeys() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"items\":[{\"<caret>\":\"datetime\"}]}",
            "lookup_string"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"items\":[{\"<caret>\"}]}",
            "lookup_string"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"defaults\":[{\"<caret>\":\"datetime\"}]}",
            "lookup_string"
        );

        assertCompletionNotContains("foo-toolbox.metadata.json",
            "{\"default\":[{\"<caret>\":\"datetime\"}]}",
            "lookup_string"
        );
    }

    public void testProviderKeys() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"providers\":[{\"<caret>\":\"datetime\"}]}",
            "items", "source"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"providers\":[{\"<caret>\"}]}",
            "items", "source"
        );
    }

    public void testRegistrarKeys() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"registrar\":[{\"<caret>\":\"datetime\"}]}",
            "signatures", "provider", "language"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"registrar\":[{\"<caret>\"}]}",
            "signatures", "provider", "language"
        );
    }

    public void testRootKeys() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"<caret>\"}",
            "providers", "registrar"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"<caret>\":[{\"<caret>\"}]}",
            "providers", "registrar"
        );
    }

    public void testProviderSourceKeys() {
        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"source\":[{\"<caret>\":\"datetime\"}]}",
            "contributor", "parameter"
        );

        assertCompletionContains("foo-toolbox.metadata.json",
            "{\"source\":[{\"<caret>\"}]}",
            "contributor", "parameter"
        );
    }
}
