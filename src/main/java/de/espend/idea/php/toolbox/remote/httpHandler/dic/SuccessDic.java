package de.espend.idea.php.toolbox.remote.httpHandler.dic;

import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class SuccessDic {
    public boolean success = true;
    public String content;

    public static SuccessDic create(@NotNull String content) {
        SuccessDic dic = new SuccessDic();
        dic.content = content;
        return dic;
    }
}
