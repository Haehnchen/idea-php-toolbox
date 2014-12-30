package de.espend.idea.php.toolbox.extension.cache;

import de.espend.idea.php.toolbox.dict.json.JsonConfigFile;
import de.espend.idea.php.toolbox.utils.JsonParseUtil;

import java.io.File;
import java.util.*;

public class JsonFileCache {

    final Map<File, Long> filesMTime = new HashMap<File, Long>();
    final Map<File, JsonConfigFile> configs = new HashMap<File, JsonConfigFile>();

    public Collection<JsonConfigFile> get(Set<File> files) {

        for(File file: files) {
            long lastModified = file.lastModified();
            if(!this.filesMTime.containsKey(file) || this.filesMTime.get(file) != lastModified) {
                this.filesMTime.put(file, lastModified);
                this.configs.put(file, JsonParseUtil.getDeserializeConfig(file));
                System.out.println("new: " + file);
            } else {
                System.out.println("cached: " + file);
            }
        }

        // clean cached files
        Set<File> knownFiles = new HashSet<File>() {{
            addAll(filesMTime.keySet());
            addAll(configs.keySet());
        }};

        for (File cachedFile : knownFiles) {

            if(files.contains(cachedFile)) {
                continue;
            }

            if(this.filesMTime.containsKey(cachedFile)) {
                this.filesMTime.remove(cachedFile);
            }
            if(this.configs.containsKey(cachedFile)) {
                this.configs.remove(cachedFile);
            }

            System.out.println("clean: " + cachedFile);

        }

        // filter null values, if we get invalid json file
        List<JsonConfigFile> configList = new ArrayList<JsonConfigFile>();
        for (JsonConfigFile jsonConfigFile : configs.values()) {
            if(jsonConfigFile != null) {
                configList.add(jsonConfigFile);
            }
        }

        return configList;
    }

    public void clear() {
        this.configs.clear();
        this.filesMTime.clear();
    }

}
