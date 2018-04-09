package de.espend.idea.php.toolbox.tests.utils;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.util.containers.ContainerUtil;
import de.espend.idea.php.toolbox.dict.json.*;
import de.espend.idea.php.toolbox.tests.SymfonyLightCodeInsightFixtureTestCase;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

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
