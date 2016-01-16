package de.espend.idea.php.toolbox.utils;

import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.containers.ContainerUtil;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.*;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.extension.cache.JsonFileCache;
import de.espend.idea.php.toolbox.matcher.php.container.ContainerConditions;
import de.espend.idea.php.toolbox.provider.SourceProvider;
import de.espend.idea.php.toolbox.remote.RemoteStorage;
import de.espend.idea.php.toolbox.remote.provider.ProviderStorageInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ExtensionProviderUtil {

    private static final ExtensionPointName<PhpToolboxProviderInterface> TOOLBOX_PROVIDER_EP = new ExtensionPointName<PhpToolboxProviderInterface>(
        "de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface"
    );

    private static final ExtensionPointName<SourceContributorInterface> SOURCE_CONTRIBUTOR_EP = new ExtensionPointName<SourceContributorInterface>(
        "de.espend.idea.php.toolbox.extension.SourceContributorInterface"
    );

    public static final ExtensionPointName<LanguageRegistrarMatcherInterface> REGISTRAR_MATCHER = new ExtensionPointName<LanguageRegistrarMatcherInterface>(
        "de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcher"
    );

    final private static JsonFileCache APPLICATION_CACHE = new JsonFileCache();
    private static Collection<JsonConfigFile> RESOURCE_FILES = null;

    private static final Key<CachedValue<Collection<JsonRegistrar>>> TYPE_CACHE = new Key<CachedValue<Collection<JsonRegistrar>>>("PHP_TOOLBOX_TYPE_CACHE");
    private static final Key<CachedValue<Collection<PhpToolboxProviderInterface>>> PROVIDER_CACHE = new Key<CachedValue<Collection<PhpToolboxProviderInterface>>>("PHP_TOOLBOX_PROVIDER_CACHE");
    private static final Key<CachedValue<Collection<JsonConfigFile>>> CONFIGS_CACHE = new Key<CachedValue<Collection<JsonConfigFile>>>("PHP_TOOLBOX_CONFIGS");
    private static final Key<CachedValue<Collection<JsonRegistrar>>> REGISTRAR_CACHE = new Key<CachedValue<Collection<JsonRegistrar>>>("PHP_TOOLBOX_REGISTRAR");

    @Nullable
    public static PhpToolboxProviderInterface getProvider(@NotNull Project project, final @NotNull String key) {
        return ContainerUtil.find(getProviders(project), new Condition<PhpToolboxProviderInterface>() {
            @Override
            public boolean value(PhpToolboxProviderInterface provider) {
                return key.equalsIgnoreCase(provider.getName());
            }
        });
    }

    @NotNull
    synchronized public static Collection<PhpToolboxProviderInterface> getProviders(final @NotNull Project project) {
        CachedValue<Collection<PhpToolboxProviderInterface>> cache = project.getUserData(PROVIDER_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(new CachedValueProvider<Collection<PhpToolboxProviderInterface>>() {
                @Nullable
                @Override
                public Result<Collection<PhpToolboxProviderInterface>> compute() {
                    return Result.create(getProvidersInner(project), PsiModificationTracker.MODIFICATION_COUNT);
                }
            }, false);

            project.putUserData(PROVIDER_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    public static Collection<PhpToolboxProviderInterface> getProvidersInner(@NotNull Project project) {
        PhpToolboxApplicationService phpToolboxApplicationService = ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class);

        Collection<PhpToolboxProviderInterface> providers = new ArrayList<PhpToolboxProviderInterface>(
            Arrays.asList(TOOLBOX_PROVIDER_EP.getExtensions())
        );

        for (Map.Entry<String, Collection<JsonRawLookupElement>> entry : ExtensionProviderUtil.getProviders(project, phpToolboxApplicationService).entrySet()) {
            providers.add(new JsonRawContainerProvider(entry.getKey(), entry.getValue()));
        }

        Map<String, Collection<JsonProvider>> sourceProviders = null;

        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {

            Collection<JsonProvider> fileProviders = jsonConfig.getProviders();
            if(fileProviders == null) {
                continue;
            }

            for (JsonProvider provider : fileProviders) {
                JsonProviderSource source = provider.getSource();
                if(source != null) {

                    if(sourceProviders == null) {
                        sourceProviders = new HashMap<String, Collection<JsonProvider>>();
                    }

                    String name = provider.getName();
                    if(!sourceProviders.containsKey(name)) {
                        sourceProviders.put(name, new ArrayList<JsonProvider>(Collections.singletonList(provider)));
                    } else {
                        sourceProviders.get(name).add(provider);
                    }
                }
            }
        }

        if(sourceProviders != null && sourceProviders.size() > 0) {
            for (Map.Entry<String, Collection<JsonProvider>> entry : sourceProviders.entrySet()) {
                providers.add(new SourceProvider(entry.getKey(), entry.getValue()));
            }
        }

        return providers;
    }
    @NotNull
    synchronized public static Collection<JsonRegistrar> getRegistrar(final @NotNull Project project, final @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        CachedValue<Collection<JsonRegistrar>> cache = project.getUserData(REGISTRAR_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(new CachedValueProvider<Collection<JsonRegistrar>>() {
                @Nullable
                @Override
                public Result<Collection<JsonRegistrar>> compute() {
                    return Result.create(getRegistrarInner(project, phpToolboxApplicationService), PsiModificationTracker.MODIFICATION_COUNT);
                }
            }, false);

            project.putUserData(REGISTRAR_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    private static Collection<JsonRegistrar> getRegistrarInner(@NotNull Project project, @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        Collection<JsonRegistrar> jsonRegistrars = new ArrayList<JsonRegistrar>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            jsonRegistrars.addAll(jsonConfig.getRegistrar());
        }

        return jsonRegistrars;
    }

    @NotNull
    synchronized public static Collection<JsonRegistrar> getTypes(final @NotNull Project project) {
        CachedValue<Collection<JsonRegistrar>> cache = project.getUserData(TYPE_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(new CachedValueProvider<Collection<JsonRegistrar>>() {
                @Nullable
                @Override
                public Result<Collection<JsonRegistrar>> compute() {
                    return Result.create(getTypesInner(project), PsiModificationTracker.MODIFICATION_COUNT);
                }
            }, false);

            project.putUserData(TYPE_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    private static Collection<JsonRegistrar> getTypesInner(@NotNull Project project) {
        Collection<JsonRegistrar> jsonRegistrars = new ArrayList<JsonRegistrar>();
        PhpToolboxApplicationService component = ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class);
        if(component == null) {
            return jsonRegistrars;
        }

        for(JsonConfigFile jsonConfig: getJsonConfigs(project, component)) {
            Collection<JsonRegistrar> registrar = jsonConfig.getRegistrar();
            for (JsonRegistrar jsonRegistrar : registrar) {
                if(
                    !"php".equals(jsonRegistrar.getLanguage()) ||
                    ContainerUtil.find(jsonRegistrar.getSignatures(), ContainerConditions.RETURN_TYPE_TYPE) == null
                  )
                {
                    continue;
                }

                jsonRegistrars.addAll(registrar);
            }
        }

        return jsonRegistrars;
    }

    @NotNull
    public static Map<String, Collection<JsonRawLookupElement>> getProviders(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {
        Map<String, Collection<JsonRawLookupElement>> providers = new HashMap<String, Collection<JsonRawLookupElement>>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            providers.putAll(JsonParseUtil.getProviderJsonRawLookupElements(jsonConfig.getProviders()));
        }

        return providers;
    }

    synchronized public static Collection<JsonConfigFile> getJsonConfigs(final @NotNull Project project, final @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        CachedValue<Collection<JsonConfigFile>> cache = project.getUserData(CONFIGS_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(new CachedValueProvider<Collection<JsonConfigFile>>() {
                @Nullable
                @Override
                public Result<Collection<JsonConfigFile>> compute() {
                    return Result.create(getJsonConfigsInner(project, phpToolboxApplicationService), PsiModificationTracker.MODIFICATION_COUNT);
                }
            }, false);

            project.putUserData(CONFIGS_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    synchronized private static Collection<JsonConfigFile> getJsonConfigsInner(@NotNull Project project, @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        Collection<JsonConfigFile> jsonConfigFiles = new ArrayList<JsonConfigFile>();

        for (final PsiFile psiFile : FilenameIndex.getFilesByName(project, ".ide-toolbox.metadata.json", GlobalSearchScope.allScope(project))) {
            JsonConfigFile cachedValue = CachedValuesManager.getCachedValue(psiFile, new CachedValueProvider<JsonConfigFile>() {
                @Nullable
                @Override
                public Result<JsonConfigFile> compute() {
                    return new Result<JsonConfigFile>(
                        JsonParseUtil.getDeserializeConfig(psiFile.getText()),
                        psiFile,
                        psiFile.getVirtualFile()
                    );
                }
            });

            if(cachedValue != null) {
                jsonConfigFiles.add(cachedValue);
            }
        }

        synchronized (APPLICATION_CACHE) {
            jsonConfigFiles.addAll(APPLICATION_CACHE.get(new HashSet<File>(Arrays.asList(phpToolboxApplicationService.getApplicationJsonFiles()))));
        }


        if(RESOURCE_FILES == null) {
            Collection<JsonConfigFile> files = new ArrayList<JsonConfigFile>();
            for (String s : new String[]{"behat", "core", "phpunit", "symfony"}) {
                InputStream stream = ExtensionProviderUtil.class.getClassLoader().getResourceAsStream("resources/json/" + s + "/.ide-toolbox.metadata.json");
                if(stream == null) {
                    continue;
                }

                String contents;
                try {
                    contents = StreamUtil.readText(stream, "UTF-8");
                } catch (IOException e) {
                    continue;
                }

                JsonConfigFile config = JsonParseUtil.getDeserializeConfig(contents);
                if(config != null) {
                    files.add(config);
                }
            }

            RESOURCE_FILES = files;
        }

        jsonConfigFiles.addAll(RESOURCE_FILES);

        // @TODO: solve object and cache issue
        ProviderStorageInterface providerStorage = RemoteStorage.getInstance(project).get("php-toolbox-json");
        if(providerStorage != null) {
            Object data = providerStorage.getData();
            if(data instanceof JsonConfigFile) {
                jsonConfigFiles.add((JsonConfigFile) data);
            }
        }

        return jsonConfigFiles;
    }

    @NotNull
    public static SourceContributorInterface[] getSourceContributors() {
        return SOURCE_CONTRIBUTOR_EP.getExtensions();
    }

    @Nullable
    public static SourceContributorInterface getSourceContributor(@NotNull String name) {
        for (SourceContributorInterface sourceContributor : getSourceContributors()) {
            if(name.equals(sourceContributor.getName())) {
                return sourceContributor;
            }
        }

        return null;
    }
}
