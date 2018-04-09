package de.espend.idea.php.toolbox.tests.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class StringReturnSourceContributorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/toolbox/tests/provider/source/contributor/fixtures";
    }

    /**
     * @see de.espend.idea.php.toolbox.provider.source.contributor.StringReturnSourceContributor
     */
    public void testStringReturn() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('<caret>')", "foo_item", "foo_return");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date('foo_return<caret>')", PlatformPatterns.psiElement(Method.class));
        assertCompletionLookupTailEquals(PhpFileType.INSTANCE, "<?php date('<caret>')", "foo_return", "DefaultTail");

        LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('<caret>')", "foo_return");
        assertEquals(element.getIcon(), PhpIcons.METHOD);
    }
}
