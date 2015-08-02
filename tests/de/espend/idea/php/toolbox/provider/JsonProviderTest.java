package de.espend.idea.php.toolbox.provider;

import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.jetbrains.php.lang.PhpFileType;
import de.espend.idea.php.toolbox.SymfonyLightCodeInsightFixtureTestCase;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonProviderTest extends SymfonyLightCodeInsightFixtureTestCase {

    public void setUp() throws Exception {
        super.setUp();

        myFixture.configureByText(".ide-toolbox.metadata.json", "{\n" +
                "  \"registrar\":[\n" +
                "    {\n" +
                "      \"signature\":\"foo\",\n" +
                "      \"signatures\":[\"date\"],\n" +
                "      \"provider\":\"date_format\",\n" +
                "      \"language\":\"php\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"providers\":[\n" +
                "    {\n" +
                "      \"name\": \"date_format\",\n" +
                "      \"defaults\": {\n" +
                "        \"icon\":\"com.jetbrains.php.PhpIcons.METHOD\",\n" +
                "        \"tail_text\":\"(TailTest)\",\n" +
                "        \"type_text\":\"(TypeText)\"\n" +
                "      },\n" +
                "      \"items\":[\n" +
                "        {\n" +
                "          \"lookup_string\":\"foo\",\n" +
                "          \"type_text\":\"Day of month (01..31)\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"lookup_string\":\"bar\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
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
