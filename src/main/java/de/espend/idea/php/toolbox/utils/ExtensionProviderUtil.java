package de.espend.idea.php.toolbox.utils;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
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
import de.espend.idea.php.toolbox.extension.JsonStreamResource;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.extension.cache.JsonFileCache;
import de.espend.idea.php.toolbox.matcher.php.container.ContainerConditions;
import de.espend.idea.php.toolbox.provider.SourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ExtensionProviderUtil {

    private static final ExtensionPointName<PhpToolboxProviderInterface> TOOLBOX_PROVIDER_EP = new ExtensionPointName<>(
        "de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface"
    );

    private static final ExtensionPointName<SourceContributorInterface> SOURCE_CONTRIBUTOR_EP = new ExtensionPointName<>(
        "de.espend.idea.php.toolbox.extension.SourceContributorInterface"
    );

    public static final ExtensionPointName<LanguageRegistrarMatcherInterface> REGISTRAR_MATCHER = new ExtensionPointName<>(
        "de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcher"
    );

    private static final ExtensionPointName<JsonStreamResource> STREAM_RESOURCES = new ExtensionPointName<>(
        "de.espend.idea.php.toolbox.extension.JsonStreamResource"
    );

    private static final Object STREAM_RESOURCES_LOCK = new Object();

    final private static JsonFileCache APPLICATION_CACHE = new JsonFileCache();
    private static Collection<JsonConfigFile> RESOURCE_FILES = null;

    private static final Key<CachedValue<Collection<JsonRegistrar>>> TYPE_CACHE = new Key<>("PHP_TOOLBOX_TYPE_CACHE");
    private static final Key<CachedValue<Collection<PhpToolboxProviderInterface>>> PROVIDER_CACHE = new Key<>("PHP_TOOLBOX_PROVIDER_CACHE");
    private static final Key<CachedValue<Collection<JsonConfigFile>>> CONFIGS_CACHE = new Key<>("PHP_TOOLBOX_CONFIGS");
    private static final Key<CachedValue<Collection<JsonConfigFile>>> CONFIGS_CACHE_INDEX = new Key<>("PHP_TOOLBOX_CONFIGS_INDEX");
    private static final Key<CachedValue<Collection<JsonRegistrar>>> REGISTRAR_CACHE = new Key<>("PHP_TOOLBOX_REGISTRAR");

    @Nullable
    public static PhpToolboxProviderInterface getProvider(@NotNull Project project, final @NotNull String key) {
        return ContainerUtil.find(getProviders(project), provider -> key.equalsIgnoreCase(provider.getName()));
    }

    @NotNull
    synchronized public static Collection<PhpToolboxProviderInterface> getProviders(final @NotNull Project project) {
        CachedValue<Collection<PhpToolboxProviderInterface>> cache = project.getUserData(PROVIDER_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(() -> CachedValueProvider.Result.create(getProvidersInner(project), PsiModificationTracker.MODIFICATION_COUNT), false);

            project.putUserData(PROVIDER_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    public static Collection<PhpToolboxProviderInterface> getProvidersInner(@NotNull Project project) {
        PhpToolboxApplicationService phpToolboxApplicationService = ServiceManager.getService(PhpToolboxApplicationService.class);

        Collection<PhpToolboxProviderInterface> providers = new ArrayList<>(
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
                        sourceProviders = new HashMap<>();
                    }

                    String name = provider.getName();
                    if(name == null) {
                        continue;
                    }

                    if(!sourceProviders.containsKey(name)) {
                        sourceProviders.put(name, new ArrayList<>(Collections.singletonList(provider)));
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
            cache = CachedValuesManager.getManager(project).createCachedValue(() -> CachedValueProvider.Result.create(getRegistrarInner(project, phpToolboxApplicationService), PsiModificationTracker.MODIFICATION_COUNT), false);

            project.putUserData(REGISTRAR_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    private static Collection<JsonRegistrar> getRegistrarInner(@NotNull Project project, @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        Collection<JsonRegistrar> jsonRegistrars = new ArrayList<>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            jsonRegistrars.addAll(jsonConfig.getRegistrar());
        }

        return jsonRegistrars;
    }

    @NotNull
    synchronized public static Collection<JsonRegistrar> getTypes(final @NotNull Project project) {
        CachedValue<Collection<JsonRegistrar>> cache = project.getUserData(TYPE_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(() -> CachedValueProvider.Result.create(getTypesInner(project), PsiModificationTracker.MODIFICATION_COUNT), false);

            project.putUserData(TYPE_CACHE, cache);
        }

        return cache.getValue();
    }

    @NotNull
    private static Collection<JsonRegistrar> getTypesInner(@NotNull Project project) {
        Collection<JsonRegistrar> jsonRegistrars = new ArrayList<>();
        PhpToolboxApplicationService component = ServiceManager.getService(PhpToolboxApplicationService.class);
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
        Map<String, Collection<JsonRawLookupElement>> providers = new HashMap<>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            providers.putAll(JsonParseUtil.getProviderJsonRawLookupElements(jsonConfig.getProviders()));
        }

        return providers;
    }

    synchronized public static Collection<JsonConfigFile> getJsonConfigs(final @NotNull Project project, final @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        CachedValue<Collection<JsonConfigFile>> cache = project.getUserData(CONFIGS_CACHE);

        if(cache == null) {
            cache = CachedValuesManager.getManager(project).createCachedValue(() -> CachedValueProvider.Result.create(getJsonConfigsInner(project, phpToolboxApplicationService), PsiModificationTracker.MODIFICATION_COUNT), false);
            project.putUserData(CONFIGS_CACHE, cache);
        }

        Collection<JsonConfigFile> jsonConfigFiles = new ArrayList<>(cache.getValue());

        // prevent reindex issues
        if (!DumbService.getInstance(project).isDumb()) {
            CachedValue<Collection<JsonConfigFile>> indexCache = project.getUserData(CONFIGS_CACHE_INDEX);

            if (indexCache == null) {
                indexCache = CachedValuesManager.getManager(project).createCachedValue(() -> {
                    Collection<JsonConfigFile> jsonConfigFiles1 = new ArrayList<>();

                    for (final PsiFile psiFile : FilenameIndex.getFilesByName(project, ".ide-toolbox.metadata.json", GlobalSearchScope.allScope(project))) {
                        JsonConfigFile cachedValue = CachedValuesManager.getCachedValue(psiFile, () -> new CachedValueProvider.Result<>(
                            JsonParseUtil.getDeserializeConfig(psiFile.getText()),
                            psiFile,
                            psiFile.getVirtualFile()
                        ));

                        if(cachedValue != null) {
                            jsonConfigFiles1.add(cachedValue);
                        }
                    }

                    return CachedValueProvider.Result.create(jsonConfigFiles1, PsiModificationTracker.MODIFICATION_COUNT);
                }, false);
            }

            project.putUserData(CONFIGS_CACHE_INDEX, indexCache);
            jsonConfigFiles.addAll(indexCache.getValue());
        }

        return jsonConfigFiles;
    }

    @NotNull
    synchronized private static Collection<JsonConfigFile> getJsonConfigsInner(@NotNull Project project, @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {
        Collection<JsonConfigFile> jsonConfigFiles = new ArrayList<>();

        synchronized (APPLICATION_CACHE) {
            jsonConfigFiles.addAll(APPLICATION_CACHE.get(new HashSet<>(Arrays.asList(phpToolboxApplicationService.getApplicationJsonFiles()))));
        }

        // resources files by plugins
        jsonConfigFiles.addAll(getResourceFiles());

        return jsonConfigFiles;
    }

    @NotNull
    private static Collection<JsonConfigFile> getResourceFiles() {
        synchronized (STREAM_RESOURCES_LOCK) {
            if(RESOURCE_FILES != null) {
                return RESOURCE_FILES;
            }

            Collection<JsonConfigFile> files = new ArrayList<>();
            for (JsonStreamResource resource : STREAM_RESOURCES.getExtensions()) {
                for (InputStream stream : resource.getInputStreams()) {
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
            }

            return RESOURCE_FILES = files;
        }
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
