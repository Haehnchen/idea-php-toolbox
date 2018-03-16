package de.espend.idea.php.toolbox.tests.provider;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.provider.php.ClassInterfaceProvider;
import de.espend.idea.php.toolbox.provider.php.ClassProvider;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ClassProviderTest extends SymfonyLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("class-ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    /**
     * @see ClassProvider
     */
    public void testClassProvider() {
        // @TODO: some tests disabled because of: WI-35795

        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php date('Traversabl<caret>')", "Traversable");

        //assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('DateT<caret>')", "DateTime");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date('DateTime<caret>')", PlatformPatterns.psiElement(PhpClass.class));

        // LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('DateT<caret>')", "DateTime");
        // assertNotNull(element.getIcon());
    }

    /**
     * @see ClassInterfaceProvider
     */
    public void testClassInterfaceProvider() {
        // @TODO: some tests disabled because of: WI-35795

        // assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('DateT<caret>')", "DateTime");
        // assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_2('Iterato<caret>')", "Iterator");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date_2('Iterator<caret>')", PlatformPatterns.psiElement(PhpClass.class));

        // LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date_2('Iterato<caret>')", "Iterator");
        // assertNotNull(element.getIcon());
    }
}
