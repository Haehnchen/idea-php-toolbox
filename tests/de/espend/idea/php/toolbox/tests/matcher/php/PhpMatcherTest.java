package de.espend.idea.php.toolbox.tests.matcher.php;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpMatcherTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testSignatures() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('<caret>')", "bar", "foo", "car");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_create()->format('<caret>')", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php new \\DateTime('<caret>')", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_create()->format(['<caret>'])", "bar", "foo");
    }

    public void testArrayCompletionInsideNewExpression() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php new \\DateTime(['<caret>'])", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php new \\DateTime(['bar' => '<caret>'])", "bar", "foo");
    }

    public void testSignatureInArrays() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date(['<caret>'])", "bar", "foo");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php foo(['<caret>'])", "bar");
    }

    public void testSignatureInArrayKeys() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_create()->format(['foo' => '<caret>'])", "bar", "foo");
    }

    public void testSignatureInReturn() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php function foo() { return '<caret>'; }", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php class Foo { function getFoo() { return '<caret>'; } }", "bar", "foo");

        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php function foo1() { return '<caret>'; }", "bar", "foo");
        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php class Foo1 { function getFoo() { return '<caret>'; } }", "bar", "foo");

        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php function foobar() { return '<caret>'; }", "bar");
    }

    public void testSignatureInReturnArray() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php function foo() { return ['<caret>']; }", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php class Foo { function getFoo() { return ['<caret>']; } }", "bar", "foo");

        assertCompletionContains(PhpFileType.INSTANCE,  "<?php function foo() { return ['', '<caret>']; }", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php class Foo { function getFoo() { return ['', '<caret>']; } }", "bar", "foo");

        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php function foo1() { return ['<caret>']; }", "bar", "foo");
        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php class Foo1 { function getFoo() { return ['<caret>']; } }", "bar", "foo");
    }

    public void testThatUnknownTypDoesNotProvideCompletion() {
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php unknown_type('<caret>')", "bar");
    }

    public void testThatLookupTargetNavigates() {
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php date('foo<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
    }

    public void testThatParameterIndexStartsByZero() {
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n parameter('', '<caret>')", "bar", "car");
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n parameter(null, '<caret>')", "bar", "foo");

        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php\n parameter('', '', '<caret>')", "bar", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE, "<?php\n parameter('<caret>'', '')", "bar", "car");

        assertNavigationMatch(PhpFileType.INSTANCE, "<?php\n parameter('', 'foo<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php\n parameter(null, 'foo<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));

        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n/** @var $f \\Foo\\Parameter */\n$f->getFoo('', '<caret>')", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE, "<?php\n/** @var $f \\Foo\\Parameter */\n$f->getFoo(null, '<caret>')", "bar", "foo");

        assertNavigationMatch(PhpFileType.INSTANCE, "<?php\n/** @var $f \\Foo\\Parameter */\n$f->getFoo(null, 'foo<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
    }
}
