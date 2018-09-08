package de.espend.idea.php.toolbox.tests.matcher.php;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Method;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class OffsetGetTest extends SymfonyLightCodeInsightFixtureTestCase {
    private static String HEADER = "<?php $al = new ArrayLike(); $og = new ArrayLikeA();";

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("OffsetGet.php");
        myFixture.copyFileToProject("OffsetGet.ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/toolbox/tests/matcher/php/fixtures";
    }

    public void testAccessExpression() {
        assertCompletionContains(PhpFileType.INSTANCE,  HEADER + "$al['<caret>']", "car");
        assertCompletionContains(PhpFileType.INSTANCE,  HEADER + "$og['<caret>']", "car");
    }
}
