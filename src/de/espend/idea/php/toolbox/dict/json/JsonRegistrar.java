package de.espend.idea.php.toolbox.dict.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonRegistrar {

    @NotNull
    private Collection<JsonSignature> signatures = new ArrayList<JsonSignature>();

    @Nullable
    private String provider;

    @Nullable
    private String language;

    private boolean references = false ;

    public JsonRegistrar() {
    }

    @Nullable
    public String getProvider() {
        return provider;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    @NotNull
    public Collection<JsonSignature> getSignatures() {
        return this.signatures;
    }

    public boolean isReferences() {
        return references;
    }
}

