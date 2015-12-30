package de.espend.idea.php.toolbox.tests.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see de.espend.idea.php.toolbox.type.PhpToolboxTypeProvider
 */
public class PhpToolboxTypeProviderTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testPhpTypeForMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $f \\Foo\\Bar */\n" +
            "$f->foo('datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $f \\Foo\\Bar */\n" +
            "$f->foo(\\Foo\\Bar::DATETIME)->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }

    public void testPhpTypeForFunctions() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "car('datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "car(\\Foo\\Bar::DATETIME)->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }

    public void testPhpTypeForStaticMethods() {
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "\\Foo\\Bar::app('datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "\\Foo\\Bar::app(\\Foo\\Bar::DATETIME)->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }
}
