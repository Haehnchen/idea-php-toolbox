package de.espend.idea.php.toolbox.dict;

import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import org.jetbrains.annotations.NotNull;

public class RegistrarMatch {

    private final PhpToolboxProviderInterface provider;
    private final JsonRegistrar registrar;

    public RegistrarMatch(@NotNull PhpToolboxProviderInterface provider, @NotNull JsonRegistrar registrar) {
        this.provider = provider;
        this.registrar = registrar;
    }

    @NotNull
    public PhpToolboxProviderInterface getProvider() {
        return provider;
    }

    @NotNull
    public JsonRegistrar getRegistrar() {
        return registrar;
    }

}
