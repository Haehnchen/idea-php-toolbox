package de.espend.idea.php.toolbox.remote.httpHandler.dic;

import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ErrorDic {
    public String content;
    public int status = 404;

    public static ErrorDic create(@NotNull String content) {
        ErrorDic dic = new ErrorDic();
        dic.content = content;
        return dic;
    }
}
