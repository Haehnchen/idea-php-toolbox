package de.espend.idea.php.toolbox.provider;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.toolbox.SymfonyLightCodeInsightFixtureTestCase;

import java.io.File;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProviderTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();

        myFixture.copyFileToProject("json-ide-toolbox.metadata.json", ".ide-toolbox.metadata.json");
    }

    public String getTestDataPath() {
        return new File(this.getClass().getResource("fixtures").getFile()).getAbsolutePath();
    }

    public void testClassProvider() {

        assertCompletionContains(PhpFileType.INSTANCE,  "<?php date('<caret>')", "foo", "bar");

        LookupElementPresentation elementFoo = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('fo<caret>o')", "foo");
        assertNotNull(elementFoo.getIcon());
        assertEquals("Day of month (01..31)", elementFoo.getTypeText());

        LookupElementPresentation elementBar = getCompletionLookupElement(PhpFileType.INSTANCE, "<?php date('ba<caret>r')", "bar");
        assertNotNull(elementBar.getIcon());
        assertEquals("(TypeText)", elementBar.getTypeText());
    }

}
