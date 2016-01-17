package de.espend.idea.php.toolbox.tests.matcher.php;

import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ArrayKeyValueSignatureRegistrarMatcherTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("ArrayKeyValue.php");
        myFixture.copyFileToProject("ArrayKeyValue.ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testNewExpression() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php new \\Foo\\Foo(['bar' => '<caret>'])", "car");
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php new \\Foo\\Foo(['bar' => 'car<caret>'])", PlatformPatterns.psiElement(Method.class).withName("format"));

        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php new \\Foo\\Bar(['bar' => '<caret>'])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php new \\Foo\\Bar(['bar_car' => '<caret>'])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php new \\Foo\\Bar(['bar_car' => '', '<caret>''])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php new \\Foo\\Foo(['bar_car' => '<caret>'])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php new \\Foo\\Foo(['bar_car' => '', '<caret>''])", "car");
    }

    public void testMethodReferences() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Bar)->foo(['bar' => '<caret>'])", "car");
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Bar)->foo(['bar' => 'car<caret>'])", PlatformPatterns.psiElement(Method.class).withName("format"));

        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Bar)->foo(['bar_car' => '<caret>'])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Bar)->foo(['bar_car' => '', '<caret>''])", "car");
    }

    public void testFunctionReferences() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php foo(['bar' => '<caret>'])", "car");
        assertNavigationMatch(PhpFileType.INSTANCE,  "<?php foo(['bar' => 'car<caret>'])", PlatformPatterns.psiElement(Method.class).withName("format"));

        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php date(['bar_car' => '<caret>'])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php foo(['bar_car' => '<caret>'])", "car");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php foo(['bar_car' => '', '<caret>''])", "car");
    }
}
