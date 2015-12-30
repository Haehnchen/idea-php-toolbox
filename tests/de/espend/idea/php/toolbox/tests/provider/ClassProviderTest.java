package de.espend.idea.php.toolbox.tests.provider;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ClassProviderTest extends SymfonyLightCodeInsightFixtureTestCase {


    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("class-ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
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
