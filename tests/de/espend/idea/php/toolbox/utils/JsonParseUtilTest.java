package de.espend.idea.php.toolbox.utils;

import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.dict.json.JsonProvider;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonParseUtilTest extends Assert {

    @Test
    public void testGetProviderJsonFromFile() {

        File testFile = new File(this.getClass().getResource("fixtures/ide-toolbox.metadata.json").getFile());

        Map<String, Collection<JsonRawLookupElement>> elements = JsonParseUtil.getProviderJsonRawLookupElements(JsonParseUtil.getDeserializeConfig(testFile).getProviders());
        assertTrue(elements.keySet().contains("date_format"));

        JsonRawLookupElement date_format = elements.get("date_format").iterator().next();

        assertEquals("d", date_format.getLookupString());

        // defaults
        assertEquals("com.jetbrains.php.PhpIcons.METHOD", date_format.getIcon());
        assertEquals("(TailTest)", date_format.getTailText());
        assertEquals("test", date_format.getTarget());

        Collection<JsonRawLookupElement> source1 = elements.get("source_2");
        JsonRawLookupElement source1Item = source1.iterator().next();
        assertEquals("d", source1Item.getLookupString());
    }

    @Test
    public void testGetRegistrarJsonFromFile() {

        File testFile = new File(this.getClass().getResource("fixtures/ide-toolbox.metadata.json").getFile());

        Collection<JsonRegistrar> elements = JsonParseUtil.getDeserializeConfig(testFile).getRegistrar();
        JsonRegistrar next = elements.iterator().next();

        assertTrue(next.getSignatures().contains("\\DateTime::format"));
        assertTrue(next.getSignatures().contains("date"));

        // single signature append
        assertTrue(next.getSignatures().contains("foo"));

        assertEquals("foo", next.getSignature());

    }

    @Test
    public void testConfigDeserialize() {
        JsonConfigFile elements = null;
        try {
            elements = JsonParseUtil.getDeserializeConfig(this.getClass().getResource("fixtures/ide-toolbox.metadata.json").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<JsonProvider> registrar = new ArrayList<JsonProvider>(elements.getProviders());;

        assertEquals("date_format", registrar.get(0).getName());

        assertEquals("source_1", registrar.get(1).getName());
        assertEquals("return", registrar.get(1).getSource().getContributor());
        assertEquals("\\Twig_Environment::getExtension", registrar.get(1).getSource().getParameter());

        assertEquals("source_2", registrar.get(2).getName());
        assertTrue(registrar.get(2).getItems().size() > 0);
    }

}
