package de.espend.idea.php.toolbox.remote.util;

import de.espend.idea.php.toolbox.remote.provider.JsonToolboxProvider;
import de.espend.idea.php.toolbox.remote.provider.ProviderInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RemoteUtil {

    private static ProviderInterface[] PROVIDERS = new ProviderInterface[] {
        new JsonToolboxProvider(),
    };

    public static ProviderInterface[] getProviders() {
        return PROVIDERS;
    }

    @Nullable
    public static ProviderInterface getProvider(@NotNull final String provider) {
        for (ProviderInterface providerInterface : PROVIDERS) {
            if(provider.equals(providerInterface.getAlias())) {
                return providerInterface;
            }
        }

        return null;
    }
}
