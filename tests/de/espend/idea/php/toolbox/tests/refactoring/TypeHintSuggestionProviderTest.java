package de.espend.idea.php.toolbox.tests.refactoring;

import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;
import de.espend.idea.php.toolbox.refactoring.TypeHintSuggestionProvider;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see TypeHintSuggestionProvider
 */
public class TypeHintSuggestionProviderTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void testTypeHintVariableCompletionExtendsListWithStrippedWordList() {
        Collection<String[]> providers = Arrays.asList(
            new String[] {"FooBarInterface", "fooBar"},
            new String[] {"FooBarInterface", "bar"},
            new String[] {"FooBarAbstract", "fooBar"},
            new String[] {"FooAbstract", "foo"}
        );

        for (String[] provider : providers) {
            assertCompletionContains(
                PhpFileType.INSTANCE,
                String.format("<?php function f(%s $<caret>);", provider[0]),
                "$" + provider[1]
            );
        }
    }
}
