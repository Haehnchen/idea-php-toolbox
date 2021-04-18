package de.espend.idea.php.toolbox.ui.application;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ToolboxApplicationForm extends JDialog implements Configurable {
    private JPanel contentPane;
    private JButton buttonAppFolder;
    private JLabel labelFolder;
    private JCheckBox checkBoxServerListenAll;
    private JCheckBox checkBoxServerEnabled;
    private JTextField textBoxServerPort;

    @Nls
    @Override
    public String getDisplayName() {
        return "PHP Toolbox";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        final String applicationFolder = PhpToolboxApplicationService.getApplicationFolder();
        labelFolder.setText(applicationFolder);

        buttonAppFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                File file = new File(applicationFolder);
                if(!file.isDirectory()) {
                    file = file.getParentFile();
                    if(file == null || !file.isDirectory()) {
                        file = null;
                    }
                }

                if(file == null) {
                    JOptionPane.showMessageDialog(null, "Directory not found");
                    return;
                }

                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Ops can not open Directory");
                }

            }
        });

        return contentPane;
    }

    @Override
    public boolean isModified() {
        PhpToolboxApplicationService applicationService = getApplicationService();

        Integer integer = 0;
        try {
            integer = Integer.parseInt(textBoxServerPort.getText());
        } catch (NumberFormatException ignored) {
        }

        return
            applicationService.serverEnabled != checkBoxServerEnabled.isSelected() ||
            applicationService.listenAll != checkBoxServerListenAll.isSelected() ||
            integer != applicationService.serverPort
            ;
    }

    @Override
    public void apply() throws ConfigurationException {
        PhpToolboxApplicationService applicationService = getApplicationService();

        Integer port = 0;
        try {
            port = Integer.parseInt(textBoxServerPort.getText());
        } catch (NumberFormatException ignored) {
        }

        if(port <= 0 || port > 65535) {
            throw new ConfigurationException("Invalid port range");
        }

        applicationService.serverEnabled = checkBoxServerEnabled.isSelected();
        applicationService.listenAll = checkBoxServerListenAll.isSelected();
        applicationService.serverPort = port;
    }

    @Override
    public void reset() {
        PhpToolboxApplicationService applicationService = getApplicationService();

        checkBoxServerEnabled.setSelected(applicationService.serverEnabled);
        checkBoxServerListenAll.setSelected(applicationService.listenAll);
        textBoxServerPort.setText(Integer.toString(applicationService.serverPort));
    }

    protected static PhpToolboxApplicationService getApplicationService() {
        return ServiceManager.getService(PhpToolboxApplicationService.class);
    }
}
