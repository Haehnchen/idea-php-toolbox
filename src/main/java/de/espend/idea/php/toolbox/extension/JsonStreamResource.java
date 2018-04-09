package de.espend.idea.php.toolbox.extension;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface JsonStreamResource {

    /**
     * Should provider streams eg of "getResourceAsStream",
     * so that plugins can provider valid json files
     */
    @NotNull
    Collection<InputStream> getInputStreams();
}
