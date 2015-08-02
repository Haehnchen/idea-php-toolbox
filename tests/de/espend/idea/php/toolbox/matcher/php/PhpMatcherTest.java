package de.espend.idea.php.toolbox.matcher.php;

import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.toolbox.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

public class PhpMatcherTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();
        myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject(".ide-toolbox.metadata.json"));
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testSignatures() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('<caret>')", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_create()->format('<caret>')", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php new \\DateTime('<caret>')", "bar", "foo");
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_create()->format(['<caret>'])", "bar", "foo");
    }

    public void testSignatureInArrays() {
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date(['<caret>'])", "bar", "foo");

        // @TODO: we need some better signature splitting in json
        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date_create()->format(['foo' => '<caret>'])", "bar", "foo");
    }
}
