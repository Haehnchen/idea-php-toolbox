package de.espend.idea.php.toolbox.tests.gotoCompletion.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class GlobalStringClassGotoTest extends SymfonyLightCodeInsightFixtureTestCase {
    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/toolbox/tests/gotoCompletion/contributor/fixtures";
    }

    public void testClassNames() {
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('DateTime<caret>')", PlatformPatterns.psiElement(PhpClass.class));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('\\DateTime<caret>')", PlatformPatterns.psiElement(PhpClass.class));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php '\\DateTime<caret>'", PlatformPatterns.psiElement(PhpClass.class));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php \"\\DateTime<caret>\"", PlatformPatterns.psiElement(PhpClass.class));
    }

    public void testClassMethods() {
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('DateTime::format<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('DateTime:::format<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('DateTime:format<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('\\DateTime:format<caret>')", PlatformPatterns.psiElement(Method.class).withName("format"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php k('\\DateTime:setTimezone<caret>')", PlatformPatterns.psiElement(Method.class).withName("setTimezone"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php '\\DateTime:setTimezone<caret>'", PlatformPatterns.psiElement(Method.class).withName("setTimezone"));
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php \"\\DateTime:setTimezone<caret>\"", PlatformPatterns.psiElement(Method.class).withName("setTimezone"));
    }

    public void testFunctions() {
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php 'date<caret>'", PlatformPatterns.psiElement(Function.class).withName("date"));
    }

    public void testMultipleQuotedClassNames() {
        assertNavigationMatch(PhpFileType.INSTANCE, "<?php '\\\\\\DateTime<caret>'", PlatformPatterns.psiElement(PhpClass.class));
    }
}
