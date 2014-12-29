package de.espend.idea.php.toolbox.dict.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;

public class JsonRegistrar {

    private Collection<String> signatures = new HashSet<String>();
    private String signature;
    private String provider;
    private String language;
    private int index = 0;
    private boolean references = false ;

    public JsonRegistrar() {
    }

    @Nullable
    public String getSignature() {
        return signature;
    }

    @Nullable
    public String getProvider() {
        return provider;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public Collection<String> getSignatures() {
        HashSet<String> strings = new HashSet<String>(this.signatures);
        if(this.signature != null) {
            strings.add(this.signature);
        }
        return strings;
    }

    public boolean isReferences() {
        return references;
    }

}

