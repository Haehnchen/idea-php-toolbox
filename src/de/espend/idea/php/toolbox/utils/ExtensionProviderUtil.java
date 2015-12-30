package de.espend.idea.php.toolbox.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.*;
import de.espend.idea.php.toolbox.extension.LanguageRegistrarMatcherInterface;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.SourceContributorInterface;
import de.espend.idea.php.toolbox.extension.cache.JsonFileCache;
import de.espend.idea.php.toolbox.provider.SourceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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

    private static final Key<CachedValue<Collection<JsonConfigFile>>> PROJECT_JSON_CACHE = new Key<CachedValue<Collection<JsonConfigFile>>>("PHP_TOOLBOX_PROJECT_JSON_CACHE");

    @NotNull
    public static PhpToolboxProviderInterface[] getProviders(Project project) {

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

        return providers.toArray(new PhpToolboxProviderInterface[providers.size()]);
    }

    @NotNull
    public static Collection<JsonRegistrar> getRegistrar(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {

        Collection<JsonRegistrar> jsonRegistrars = new ArrayList<JsonRegistrar>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            jsonRegistrars.addAll(jsonConfig.getRegistrar());
        }

        return jsonRegistrars;
    }

    @NotNull
    public static Collection<JsonType> getTypes(@NotNull Project project) {

        Collection<JsonType> jsonRegistrars = new ArrayList<JsonType>();
        PhpToolboxApplicationService component = ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class);
        if(component == null) {
            return jsonRegistrars;
        }

        for(JsonConfigFile jsonConfig: getJsonConfigs(project, component)) {
            jsonRegistrars.addAll(jsonConfig.getTypes());
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

    @NotNull
    synchronized private static Collection<JsonConfigFile> getJsonConfigs(@NotNull Project project, @NotNull PhpToolboxApplicationService phpToolboxApplicationService) {

        Collection<JsonConfigFile> jsonConfigFiles = new ArrayList<JsonConfigFile>();

        CachedValue<Collection<JsonConfigFile>> cache = project.getUserData(PROJECT_JSON_CACHE);

        if(cache == null) {
            final PsiFile[] files = FilenameIndex.getFilesByName(project, ".ide-toolbox.metadata.json", GlobalSearchScope.allScope(project));
            if(files.length > 0) {
                cache = CachedValuesManager.getManager(project).createCachedValue(
                    new MyJsonProjectCachedValueProvider(files),
                    false
                );

                // @TODO: why tests are not clear cache after project destroy?
                if(!ApplicationManager.getApplication().isUnitTestMode()) {
                    project.putUserData(PROJECT_JSON_CACHE, cache);
                }
            }
        }

        if(cache != null) {
            jsonConfigFiles.addAll(cache.getValue());
        }

        synchronized (APPLICATION_CACHE) {
            jsonConfigFiles.addAll(APPLICATION_CACHE.get(new HashSet<File>(Arrays.asList(phpToolboxApplicationService.getApplicationJsonFiles()))));
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

    private static class MyJsonProjectCachedValueProvider implements CachedValueProvider<Collection<JsonConfigFile>> {
        private final PsiFile[] files;

        public MyJsonProjectCachedValueProvider(PsiFile[] files) {
            this.files = files;
        }

        @Nullable
        @Override
        public Result<Collection<JsonConfigFile>> compute() {

            Collection<JsonConfigFile> jsonConfigFiles = new ArrayList<JsonConfigFile>();

            for (final PsiFile psiFile : files) {
                JsonConfigFile deserializeConfig = JsonParseUtil.getDeserializeConfig(psiFile.getText());
                if (deserializeConfig != null) {
                    jsonConfigFiles.add(deserializeConfig);
                }
            }

            return Result.create(jsonConfigFiles, files);
        }
    }
}
