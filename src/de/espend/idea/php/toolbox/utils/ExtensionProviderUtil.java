package de.espend.idea.php.toolbox.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.*;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.extension.cache.JsonFileCache;
import de.espend.idea.php.toolbox.provider.ClassInterfaceProvider;
import de.espend.idea.php.toolbox.provider.ClassProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class ExtensionProviderUtil {

    final private static Map<Project, JsonFileCache> PROJECT_CACHE = new HashMap<Project, JsonFileCache>();
    final private static JsonFileCache APPLICATION_CACHE = new JsonFileCache();

    @NotNull
    public static PhpToolboxProviderInterface[] getProviders(Project project) {

        PhpToolboxApplicationService phpToolboxApplicationService = ApplicationManager.getApplication().getComponent(PhpToolboxApplicationService.class);

        Collection<PhpToolboxProviderInterface> providers = new ArrayList<PhpToolboxProviderInterface>();
        providers.add(new ClassProvider());
        providers.add(new ClassInterfaceProvider());

        for (Map.Entry<String, Collection<JsonRawLookupElement>> entry : ExtensionProviderUtil.getProviders(project, phpToolboxApplicationService).entrySet()) {
            providers.add(new JsonRawContainerProvider(entry.getKey(), entry.getValue()));
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
    public static Collection<JsonType> getTypes(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {

        Collection<JsonType> jsonRegistrars = new ArrayList<JsonType>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            jsonRegistrars.addAll(jsonConfig.getTypes());
        }

        return jsonRegistrars;
    }

    @NotNull
    public static Map<String, Collection<JsonRawLookupElement>> getProviders(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {

        Map<String, Collection<JsonRawLookupElement>> jsonRegistrars = new HashMap<String, Collection<JsonRawLookupElement>>();
        for(JsonConfigFile jsonConfig: getJsonConfigs(project, phpToolboxApplicationService)) {
            jsonRegistrars.putAll(JsonParseUtil.getProviderJsonRawLookupElements(jsonConfig.getProviders()));
        }

        return jsonRegistrars;
    }

    @NotNull
    private static Collection<JsonConfigFile> getJsonConfigs(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {

        Collection<JsonConfigFile> jsonConfigFiles = new ArrayList<JsonConfigFile>();

        synchronized (PROJECT_CACHE) {
            if(!PROJECT_CACHE.containsKey(project)) {
                PROJECT_CACHE.put(project, new JsonFileCache());
            }

            jsonConfigFiles.addAll(PROJECT_CACHE.get(project).get(getProjectJsonFiles(project)));
        }

        synchronized (APPLICATION_CACHE) {
            jsonConfigFiles.addAll(APPLICATION_CACHE.get(new HashSet<File>(Arrays.asList(phpToolboxApplicationService.getApplicationJsonFiles()))));
        }

        return jsonConfigFiles;
    }

    @NotNull
    public static Set<File> getProjectJsonFiles(Project project) {
        VirtualFile phpToolbox = VfsUtil.findRelativeFile(project.getBaseDir(), ".idea", "phpToolbox");

        Set<File> files = new HashSet<File>();

        if(phpToolbox != null) {
            File dir = VfsUtil.virtualToIoFile(phpToolbox);
            if(dir.isDirectory()) {
                files.addAll(Arrays.asList(dir.listFiles(new JsonParseUtil.JsonFileFilter())));
            }
        }

        VirtualFile rootFile = VfsUtil.findRelativeFile(project.getBaseDir(), ".phpToolbox.json");
        if(rootFile != null) {
            files.add(VfsUtil.virtualToIoFile(rootFile));
        }

        return files;
    }

}
