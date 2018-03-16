package de.espend.idea.php.toolbox.tests.matcher.php;

import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class IssueCasesTest  extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject("classes.php");
        myFixture.copyFileToProject("issue.php");
        myFixture.copyFileToProject("issue-ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return "src/test/java/de/espend/idea/php/toolbox/tests/matcher/php/fixtures";
    }

    public void testProviderAlias() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Car())->foo('<caret>');", "DateTime");
        assertCompletionNotContains(PhpFileType.INSTANCE,  "<?php (new \\Foo\\Foo())->foo('<caret>');", "DateTime");
    }
}
