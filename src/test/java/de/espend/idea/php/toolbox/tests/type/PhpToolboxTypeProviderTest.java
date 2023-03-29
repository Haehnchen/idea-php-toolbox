package de.espend.idea.php.toolbox.tests.type;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

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
        return "src/test/java/de/espend/idea/php/toolbox/tests/type/fixtures";
    }

    public void testPhpTypeForMethods() {
        // skip
        if (true) return;

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $f \\Foo\\Bar */\n" +
            "$f->foo('datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        // Same method name but different class and parameter index
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $f \\Foo\\Baz */\n" +
            "$f->foo('', 'datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "/** @var $f \\Foo\\Bar */\n" +
            "$f->foo(\\Foo\\Bar::DATETIME)->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }

    public void testPhpTypeForFunctions() {
        // skip
        if (true) return;

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "car('', 'datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "car('', \\Foo\\Bar::DATETIME)->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }

    public void testPhpTypeForStaticMethods() {
        // skip
        if (true) return;

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "\\Foo\\Bar::app('datetime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "\\Foo\\Bar::app(\\Foo\\Bar::DATETIME)->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }

    public void testClassProvider() {
        // skip
        if (true) return;

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
            "clazz('DateTime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }

    public void testClassBackslash() {
        // skip
        if (true) return;

        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "clazz('\\DateTime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "clazz('\\\\DateTime')->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
        assertPhpReferenceResolveTo(PhpFileType.INSTANCE, "<?php\n" +
                "clazz(new DateTime())->for<caret>mat()",
            PlatformPatterns.psiElement(Method.class).withName("format")
        );
    }
}
