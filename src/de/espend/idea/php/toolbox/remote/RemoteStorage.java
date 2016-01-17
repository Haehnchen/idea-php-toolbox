package de.espend.idea.php.toolbox.remote;

import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.project.Project;
import de.espend.idea.php.toolbox.remote.provider.ProviderInterface;
import de.espend.idea.php.toolbox.remote.provider.ProviderStorageInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class RemoteStorage {

    protected static Map<Project, RemoteStorage> instance = new HashMap<Project, RemoteStorage>();

    protected Project project;
    protected Map<String, ProviderStorageInterface> instances = new HashMap<String, ProviderStorageInterface>();

    public boolean has(@NotNull String provider) {
        return instances.containsKey(provider);
    }


    @NotNull
    public Map<String, ProviderStorageInterface> all() {
        return this.instances;
    }

    @NotNull
    public Collection<String> getNames() {
        return instances.keySet();
    }

    @Nullable
    public ProviderStorageInterface get(@NotNull String provider) {

        if(instances.containsKey(provider)) {
            return instances.get(provider);
        }

        return null;
    }

    @Nullable
    public ProviderStorageInterface get(@NotNull ProviderInterface provider) {

        if(instances.containsKey(provider.getAlias())) {
            return instances.get(provider.getAlias());
        }

        return null;
    }

    public void set(@NotNull ProviderInterface provider, @NotNull String content) {
        try {
            instances.put(provider.getAlias(), provider.getData(content));
        } catch (JsonSyntaxException ignored) {
        }
    }

    synchronized public static RemoteStorage getInstance(Project project){

        if(instance.containsKey(project)) {
            return instance.get(project);
        }

        instance.put(project, new RemoteStorage());

        return instance.get(project);

    }

    synchronized public static void removeInstance(Project project){
        if(instance.containsKey(project)) {
            instance.remove(project);
        }
    }

}