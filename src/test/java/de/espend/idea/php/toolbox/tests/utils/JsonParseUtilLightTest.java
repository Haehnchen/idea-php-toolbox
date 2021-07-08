package de.espend.idea.php.toolbox.tests.utils;

import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonParseUtilLightTest extends SymfonyLightCodeInsightFixtureTestCase {

    /**
     * @see JsonParseUtil#getLookupIconOnString
     */
    public void testGetProviderJsonFromFile() {
        assertNotNull(JsonParseUtil.getLookupIconOnString("com.intellij.icons.AllIcons$Actions.Back"));
        assertNotNull(JsonParseUtil.getLookupIconOnString("com.intellij.icons.AllIcons.Actions.Back"));
    }

}
