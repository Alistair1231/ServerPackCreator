package de.griefed.serverpackcreator.gui;

import de.griefed.serverpackcreator.i18n.LocalizationManager;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.File;

public class ModloaderInstallerLog extends Component {

    JComponent modloaderInstallerLog() {
        JComponent modloaderInstallerLog = new JPanel(false);
        modloaderInstallerLog.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.weighty = 0.9;

        //Log Panel
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        Tailer.create(new File("./logs/modloader_installer.log"), new TailerListenerAdapter() {
            public void handle(String line) {
                synchronized (this) {
                    if (line.contains(LocalizationManager.getLocalizedString("serversetup.log.info.installserver.fabric.enter")) || line.contains(LocalizationManager.getLocalizedString("serversetup.log.info.installserver.forge.enter"))) {
                        textArea.setText("");
                    }
                    textArea.append(line.substring(line.indexOf(") - ")+4) + "\n");
                }
            }
        }, 2000, false);

        JScrollPane scrollPane = new JScrollPane(
                textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane.setMinimumSize(new Dimension(775,getMaximumSize().height));
        scrollPane.setPreferredSize(new Dimension(775,getMaximumSize().height));
        scrollPane.setMaximumSize(new Dimension(775,getMaximumSize().height));

        modloaderInstallerLog.add(scrollPane, constraints);

        return modloaderInstallerLog;
    }
}