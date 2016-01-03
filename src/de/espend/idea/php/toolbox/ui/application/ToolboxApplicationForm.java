package de.espend.idea.php.toolbox.ui.application;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import de.espend.idea.php.toolbox.PhpToolboxApplicationService;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ToolboxApplicationForm extends JDialog implements Configurable {
    private JPanel contentPane;
    private JButton buttonAppFolder;
    private JLabel labelFolder;

    public ToolboxApplicationForm() {
        setContentPane(contentPane);
    }

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
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}
