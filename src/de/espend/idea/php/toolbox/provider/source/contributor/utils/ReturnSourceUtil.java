package de.espend.idea.php.toolbox.provider.source.contributor.utils;

import com.intellij.openapi.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ReturnSourceUtil {

    /**
     * Extract valid class method syntax
     * "Foo::bar, Foo:car"
     */
    public static Collection<Pair<String, String>> extractParameter(@NotNull String parameters) {
        Collection<Pair<String, String>> methods = new ArrayList<Pair<String, String>>();

        String[] sourceParameter = parameters.split(",");
        for (String s : sourceParameter) {
            String[] split = s.trim().replaceAll("(:)\\1", "$1").split(":");
            if(split.length < 2 || StringUtils.isBlank(split[0]) || StringUtils.isBlank(split[1])) {
                continue;
            }

            methods.add(Pair.create(split[0], split[1]));
        }

        return methods;
    }

}
