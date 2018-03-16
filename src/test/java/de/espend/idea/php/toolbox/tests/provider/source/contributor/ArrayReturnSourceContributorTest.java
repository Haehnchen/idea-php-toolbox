package de.espend.idea.php.toolbox.tests.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.provider.source.contributor.ArrayReturnSourceContributor;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ArrayReturnSourceContributorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/toolbox/tests/provider/source/contributor/fixtures";
    }

    /**
     * @see ArrayReturnSourceContributor
     */
    public void testArrayStringReturn() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_2('<caret>')", "foo_array", "foo_array_1");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date_2('foo_array_1<caret>')", PlatformPatterns.psiElement(Method.class));
        assertCompletionLookupTailEquals(PhpFileType.INSTANCE, "<?php date_2('<caret>')", "foo_array_1", "DefaultTail");

        LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('<caret>')", "foo_return");
        assertEquals(element.getIcon(), PhpIcons.METHOD);
    }
}
