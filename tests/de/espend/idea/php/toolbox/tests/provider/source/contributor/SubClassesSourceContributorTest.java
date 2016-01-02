package de.espend.idea.php.toolbox.tests.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.toolbox.provider.source.contributor.SubClassesSourceContributor
 */
public class SubClassesSourceContributorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    /**
     * @see de.espend.idea.php.toolbox.provider.source.contributor.SubClassesSourceContributor
     */
    public void testSubClasses() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php readdir('<caret>')", "ReturnClass");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php readdir('ReturnClass<caret>')", PlatformPatterns.psiElement(PhpClass.class).withName("ReturnClass"));
    }
}
