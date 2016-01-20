package de.espend.idea.php.toolbox.dict.json;

import com.intellij.util.containers.HashMap;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.HashSet;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class JsonRegistrar {

    @NotNull
    private Collection<String> signature = new HashSet<String>();

    @NotNull
    private Collection<JsonSignature> signatures = new ArrayList<JsonSignature>();

    private Collection<JsonSignature> mySignatures = null;

    @Nullable
    private String provider;

    @Nullable
    private String language;

    @NotNull
    private Map<String, Object> parameters = new HashMap<String, Object>();

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
        if(this.mySignatures != null) {
            return this.mySignatures;
        }

        this.mySignatures = new ArrayList<JsonSignature>(signatures);

        if(this.signature.size() > 0) {
            this.mySignatures.addAll(JsonParseUtil.createSignaturesFromStrings(this.signature));
        }

        return this.mySignatures;
    }

    public boolean isReferences() {
        return references;
    }

    @NotNull
    public Map<String, Object> getParameters() {
        return parameters;
    }

}

