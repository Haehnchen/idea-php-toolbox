package de.espend.idea.php.toolbox.provider.source.contributor.utils;

import com.intellij.openapi.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ReturnSourceUtilTest extends Assert {

    @Test
    public void testExtractParameter() {
        Collection<Pair<String, String>> pairs = ReturnSourceUtil.extractParameter("Foo::bar, Foo:car, foo");
        assertEquals("[<Foo,bar>, <Foo,car>]", pairs.toString());

        assertEquals(0, ReturnSourceUtil.extractParameter("foo::").size());
    }

}
