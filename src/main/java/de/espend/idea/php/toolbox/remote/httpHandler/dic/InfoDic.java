package de.espend.idea.php.toolbox.remote.httpHandler.dic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class InfoDic {
    public String fullVersion;
    public String apiVersion;
    public String versionName;
    public Collection<Map<String, String>> projects = new ArrayList<>();
    public Collection<Map<String, String>> plugins = new ArrayList<>();
    public Collection<Map<String, String>> routes;
}
