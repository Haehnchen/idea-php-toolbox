package de.espend.idea.php.toolbox.tests.utils;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import de.espend.idea.php.toolbox.dict.json.*;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

        assertTrue(ContainerUtil.filter(next.getSignatures(), new MyFunctionJsonSignatureCondition("foo")).size() > 0);
        assertTrue(ContainerUtil.filter(next.getSignatures(), new MyFunctionJsonSignatureCondition("date")).size() > 0);

        assertTrue(ContainerUtil.filter(next.getSignatures(), new Condition<JsonSignature>() {
            @Override
            public boolean value(JsonSignature jsonSignature) {
                return "DateTime".equals(jsonSignature.getClassName());
            }
        }).size() > 0);
    }

    @Test
    public void testGetRegistrarJsonFromFileWithShortcut() {
        File testFile = new File(this.getClass().getResource("fixtures/ide-toolbox.metadata.json").getFile());

        Collection<JsonRegistrar> elements = JsonParseUtil.getDeserializeConfig(testFile).getRegistrar();
        JsonRegistrar next = elements.iterator().next();

        JsonSignature object = ContainerUtil.find(next.getSignatures(), new MyFunctionJsonSignatureCondition("apple"));

        assertNotNull(object);
        assertEquals(object.hashCode(), ContainerUtil.find(next.getSignatures(), new MyFunctionJsonSignatureCondition("apple")).hashCode());

        assertTrue(ContainerUtil.filter(next.getSignatures(), new Condition<JsonSignature>() {
            @Override
            public boolean value(JsonSignature jsonSignature) {
                return "apple".equals(jsonSignature.getClassName()) && "car".equals(jsonSignature.getMethod());
            }
        }).size() > 0);
    }

    @Test
    public void testConfigDeserialize() {
        JsonConfigFile elements = null;
        try {
            elements = JsonParseUtil.getDeserializeConfig(StreamUtil.readText(this.getClass().getResource("fixtures/ide-toolbox.metadata.json").openStream(), Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<JsonProvider> registrar = new ArrayList<JsonProvider>(elements.getProviders());

        assertEquals("date_format", registrar.get(0).getName());

        Collection<JsonRawLookupElement> dateFromatProvider = registrar.get(0).getItems();
        JsonRawLookupElement item = ContainerUtil.find(dateFromatProvider, new MyJsonRawLookupElementStringCondition("d"));
        assertNotNull(item);
        assertEquals(item.hashCode(), ContainerUtil.find(dateFromatProvider, new MyJsonRawLookupElementStringCondition("d")).hashCode());

        assertNotNull(ContainerUtil.find(dateFromatProvider, new MyJsonRawLookupElementStringCondition("car")));
        assertNotNull(ContainerUtil.find(dateFromatProvider, new MyJsonRawLookupElementStringCondition("apple")));

        assertEquals("source_1", registrar.get(1).getName());
        assertEquals("return", registrar.get(1).getSource().getContributor());
        assertEquals("\\Twig_Environment::getExtension", registrar.get(1).getSource().getParameter());

        assertEquals("source_2", registrar.get(2).getName());
        assertTrue(registrar.get(2).getItems().size() > 0);
    }

    @Test
    public void testCreateSignaturesFromStrings() {
        JsonSignature sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("\\Foo\\Bar:foo")).iterator().next();
        assertEquals("\\Foo\\Bar", sign.getClassName());
        assertEquals(0, sign.getIndex());
        assertEquals("foo", sign.getMethod());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("\\Foo\\Bar:f_oo")).iterator().next();
        assertEquals("\\Foo\\Bar", sign.getClassName());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("\\Foo\\Bar:fo-o")).iterator().next();
        assertEquals("\\Foo\\Bar", sign.getClassName());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("Foo\\B__--ar:foo")).iterator().next();
        assertEquals("Foo\\B__--ar", sign.getClassName());
        assertEquals("foo", sign.getMethod());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("\\Foo\\Bar:::::fo-o")).iterator().next();
        assertEquals("\\Foo\\Bar", sign.getClassName());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("\\Foo\\Bar:foo:1")).iterator().next();
        assertEquals("\\Foo\\Bar", sign.getClassName());
        assertEquals(1, sign.getIndex());
        assertEquals("foo", sign.getMethod());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("Foo\\B__--ar:foo:1")).iterator().next();
        assertEquals("Foo\\B__--ar", sign.getClassName());
        assertEquals(1, sign.getIndex());
        assertEquals("foo", sign.getMethod());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("foo_bar")).iterator().next();
        assertEquals("foo_bar", sign.getFunction());
        assertEquals(0, sign.getIndex());

        sign = JsonParseUtil.createSignaturesFromStrings(Collections.singletonList("foo_bar:1")).iterator().next();
        assertEquals("foo_bar", sign.getFunction());
        assertEquals(1, sign.getIndex());
    }

    private static class MyFunctionJsonSignatureCondition implements Condition<JsonSignature> {
        final private String function;

        public MyFunctionJsonSignatureCondition(@NotNull String function) {
            this.function = function;
        }

        @Override
        public boolean value(JsonSignature signature) {
            return function.equals(signature.getFunction());
        }
    }

    private static class MyJsonRawLookupElementStringCondition implements Condition<JsonRawLookupElement> {
        final private String string;

        public MyJsonRawLookupElementStringCondition(@NotNull String string) {
            this.string = string;
        }

        @Override
        public boolean value(JsonRawLookupElement element) {
            return string.equals(element.getLookupString());
        }
    }
}
