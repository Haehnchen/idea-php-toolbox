package de.espend.idea.php.toolbox.remote;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public interface MessageNotifier extends Runnable {
    void addMessageHandler(MessageHandler handler);
}
