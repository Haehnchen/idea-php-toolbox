package de.espend.idea.php.toolbox.utils;

import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class JsonParseUtilTest extends Assert {

    @Test
    public void testGetProviderJsonFromFile() {

        // travis disabled
        if(true == true) {
            return;
        }

        File testFile = new File(this.getClass().getResource("test.json").getFile());

        Map<String, Collection<JsonRawLookupElement>> elements = JsonParseUtil.getProviderJsonRawLookupElements(JsonParseUtil.getDeserializeConfig(testFile).getProviders());
        assertTrue(elements.keySet().contains("date_format"));

        JsonRawLookupElement date_format = elements.get("date_format").iterator().next();

        assertEquals("d", date_format.getLookupString());

        // defaults
        assertEquals("com.jetbrains.php.PhpIcons.METHOD", date_format.getIcon());
        assertEquals("(TailTest)", date_format.getTailText());
        assertEquals("test", date_format.getTarget());
    }

    @Test
    public void testGetRegistrarJsonFromFile() {

        // travis disabled
        if(true == true) {
            return;
        }

        File testFile = new File(this.getClass().getResource("test.json").getFile());

        Collection<JsonRegistrar> elements = JsonParseUtil.getDeserializeConfig(testFile).getRegistrar();
        JsonRegistrar next = elements.iterator().next();

        assertTrue(next.getSignatures().contains("\\DateTime::format"));
        assertTrue(next.getSignatures().contains("date"));

        // single signature append
        assertTrue(next.getSignatures().contains("foo"));

        assertEquals("foo", next.getSignature());

    }

}
