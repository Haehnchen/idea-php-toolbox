package de.espend.idea.php.toolbox.provider.source.contributor;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ReturnSourceContributorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("classes.php"));
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject(".ide-toolbox.metadata.json"));
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
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

    /**
     * @see de.espend.idea.php.toolbox.provider.source.contributor.ArrayReturnSourceContributor
     */
    public void testArrayStringReturn() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_2('<caret>')", "foo_array", "foo_array_1");
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php date_2('foo_array_1<caret>')", PlatformPatterns.psiElement(Method.class));
        assertCompletionLookupTailEquals(PhpFileType.INSTANCE, "<?php date_2('<caret>')", "foo_array_1", "DefaultTail");

        LookupElementPresentation element = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('<caret>')", "foo_return");
        assertEquals(element.getIcon(), PhpIcons.METHOD);
    }
}
