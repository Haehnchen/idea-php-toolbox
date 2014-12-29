package de.espend.idea.php.toolbox.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import de.espend.idea.php.toolbox.dict.json.JsonRawContainerProvider;
import de.espend.idea.php.toolbox.dict.json.JsonRawLookupElement;
import de.espend.idea.php.toolbox.dict.json.JsonRegistrar;
import de.espend.idea.php.toolbox.extension.PhpToolboxProviderInterface;
import de.espend.idea.php.toolbox.provider.ClassInterfaceProvider;
import de.espend.idea.php.toolbox.provider.ClassProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class ExtensionProviderUtil {

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
        for(File file: getJsonFiles(project, phpToolboxApplicationService)) {
            jsonRegistrars.addAll(JsonParseUtil.getRegistrarJsonFromFile(file));
        }

        return jsonRegistrars;
    }

    @NotNull
    public static Map<String, Collection<JsonRawLookupElement>> getProviders(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {

        Map<String, Collection<JsonRawLookupElement>> jsonRegistrars = new HashMap<String, Collection<JsonRawLookupElement>>();

        for(File file: getJsonFiles(project, phpToolboxApplicationService)) {
            jsonRegistrars.putAll(JsonParseUtil.getProviderJsonFromFile(file));
        }

        return jsonRegistrars;
    }

    @NotNull
    private static List<File> getJsonFiles(Project project, PhpToolboxApplicationService phpToolboxApplicationService) {
        List<File> files = new ArrayList<File>(Arrays.asList(phpToolboxApplicationService.getApplicationJsonFiles()));
        files.addAll(new ArrayList<File>(Arrays.asList(getProjectJsonFiles(project))));
        return files;
    }

    @NotNull
    public static File[] getProjectJsonFiles(Project project) {
        VirtualFile phpToolbox = VfsUtil.findRelativeFile(project.getBaseDir(), ".idea", "phpToolbox");
        if(phpToolbox != null) {
            File dir = VfsUtil.virtualToIoFile(phpToolbox);
            if(dir.isDirectory()) {
                return dir.listFiles(new JsonParseUtil.JsonFileFilter());
            }
        }

        return new File[0];
    }

}
