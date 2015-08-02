package de.espend.idea.php.toolbox.provider;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ClassProviderTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();

        myFixture.configureByText(".ide-toolbox.metadata.json", "" +
                "{\n" +
                "  \"registrar\":[\n" +
                "    {\n" +
                "      \"signatures\":[\"date\"],\n" +
                "      \"provider\":\"Class\",\n" +
                "      \"language\":\"php\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"signatures\":[\"date_2\"],\n" +
                "      \"provider\":\"ClassInterface\",\n" +
                "      \"language\":\"php\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");
    }


    /**
     * @see de.espend.idea.php.toolbox.provider.ClassProvider
     */

    public void testClassProvider() {
        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php date('Traversabl<caret>')", "Traversable");

        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('DateT<caret>')", "DateTime");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date('DateTime<caret>')", PlatformPatterns.psiElement(PhpClass.class));

        LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('DateT<caret>')", "DateTime");
        assertNotNull(element.getIcon());
    }

    /**
     * @see de.espend.idea.php.toolbox.provider.ClassInterfaceProvider
     */
    public void testClassInterfaceProvider() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('DateT<caret>')", "DateTime");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_2('Iterato<caret>')", "Iterator");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date_2('Iterator<caret>')", PlatformPatterns.psiElement(PhpClass.class));

        LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date_2('Iterato<caret>')", "Iterator");
        assertNotNull(element.getIcon());
    }
}
