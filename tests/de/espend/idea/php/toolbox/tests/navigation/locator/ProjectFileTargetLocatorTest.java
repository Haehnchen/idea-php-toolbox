package de.espend.idea.php.toolbox.tests.navigation.locator;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ProjectFileTargetLocatorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testFunction() {
        createDummyFiles("foo/foo.html.twig");
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php date('foo<caret>');", PlatformPatterns.psiFile());
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php date('bla<caret>');", PlatformPatterns.psiFile());
        deleteDummyFiles("foo/foo.html.twig");
    }
}
