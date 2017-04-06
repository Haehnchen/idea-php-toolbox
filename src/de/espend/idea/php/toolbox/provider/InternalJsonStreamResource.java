package de.espend.idea.php.toolbox.provider;

import de.espend.idea.php.toolbox.extension.JsonStreamResource;
import de.espend.idea.php.toolbox.utils.ExtensionProviderUtil;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class InternalJsonStreamResource implements JsonStreamResource {
    @NotNull
    @Override
    public Collection<InputStream> getInputStreams() {
        Collection<InputStream> inputStreams = new ArrayList<>();

        // not scanning directory; performance because we are inside .jar file
        String[] strings = {
            "behat",
            "core",
            "doctrine/common",
            "doctrine/orm",
            "http",
            "mockery",
            "php-http/message-factory",
            "psr/http-message",
            "phpunit",
            "symfony/browserkit",
            "symfony/config",
            "symfony/form",
            "symfony/framework-bundle",
            "symfony/http-foundation",
            "twig"
        };

        for (String s : strings) {
            InputStream stream = ExtensionProviderUtil.class.getClassLoader()
                .getResourceAsStream("resources/json/" + s + "/.ide-toolbox.metadata.json");

            if (stream == null) {
                continue;
            }

            inputStreams.add(stream);
        }

        return inputStreams;
    }
}
