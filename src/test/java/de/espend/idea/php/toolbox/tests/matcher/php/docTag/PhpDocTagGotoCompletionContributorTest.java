package de.espend.idea.php.toolbox.tests.matcher.php.docTag;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpDocTagGotoCompletionContributorTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/toolbox/tests/matcher/php/docTag/fixtures";
    }

    public void testDirectMethod() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Car())->foo('<caret>', '');", "DateTime");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Car())->foo(null, '<caret>');", "Foo\\FooTrait");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Car())->foo(null, null, '<caret>');", "DateTime");

        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Car())->foo('DateTime<caret>', '');", PlatformPatterns.psiElement(PhpClass.class));
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Car())->foo('DateTime', 'Foo\\FooTrait<caret>');", PlatformPatterns.psiElement(PhpClass.class));
    }

    public void testOverwriteOfMethod() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Bike())->foo(null, '<caret>');", "Foo\\FooTrait");
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Bike())->foo('', 'Foo\\FooTrait<caret>');", PlatformPatterns.psiElement(PhpClass.class));
    }

    public void testProviderAlias() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Compatibility())->foo('<caret>');", "DateTime");
    }

    public void testFunctionSupport() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php foo('<caret>');", "DateTime");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php foo(null, '<caret>');", "DateTime");

        assertCompletionContains(PhpFileType.INSTANCE,  "<?php car(null, '<caret>');", "DateTime");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php car('<caret>');", "DateTime");
    }

    public void testConstructorSupport() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php new \\TestClass('<caret>', null);", "DateTime");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php new \\TestClass(null, '<caret>');", "DateTime");
    }
}
