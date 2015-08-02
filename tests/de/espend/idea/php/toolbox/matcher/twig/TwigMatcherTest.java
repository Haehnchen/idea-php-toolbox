package de.espend.idea.php.toolbox.matcher.twig;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.twig.TwigFileType;
import de.espend.idea.php.toolbox.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TwigMatcherTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject("classes.php"));
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject(".ide-toolbox.metadata.json"));
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testFunction() {
        assertCompletionContains(TwigFileType.INSTANCE,  "{{ foo('<caret>') }}", "bar", "foo");
    }

    public void testFunctionPhpReferences() {

        assertCompletionContains(TwigFileType.INSTANCE,  "{{ foo('<caret>') }}", "foo_item", "foo_return");

        // @TODO: bring this working
        //assertNavigationMatch(TwigFileType.INSTANCE,  "{{ foo('foo_return<caret>') }}", PlatformPatterns.psiElement(Method.class));
        assertCompletionLookupTailEquals(TwigFileType.INSTANCE,  "{{ foo('<caret>') }}", "foo_return", "DefaultTail");

        LookupElementPresentation element = getCompletionLookupElement(TwigFileType.INSTANCE,  "{{ foo('<caret>') }}", "foo_return");
        assertEquals(element.getIcon(), PhpIcons.METHOD);
    }
}
