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
            "ClassInterface"
        );
    }

    public void testProviderIsInWrongFile() {
        assertCompletionNotContains("foo-toolbox.metadata_bar.json",
            "{\"provider\":\"<caret>\"}",
            "ClassInterface"
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
}
