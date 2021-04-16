package de.griefed.serverpackcreator.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerPackCreatorLog extends Component {
    private static final Logger appLogger = LogManager.getLogger(ServerPackCreatorLog.class);

    JComponent serverPackCreatorLog() {
        JComponent serverPackCreatorLog = new JPanel(false);
        serverPackCreatorLog.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;

        //Log Panel
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try { textArea.read(new FileReader("./logs/serverpackcreator.log"),null); }
                catch (IOException ex) { appLogger.error("Error reading the modloader_installer.log.", ex); }
            }
        }, 2, 1, TimeUnit.SECONDS);



        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setMinimumSize(new Dimension(getMaximumSize().width,520));
        scrollPane.setPreferredSize(new Dimension(getMaximumSize().width,520));
        scrollPane.setMaximumSize(new Dimension(getMaximumSize().width,520));
        serverPackCreatorLog.add(scrollPane, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(15,10,10,10);
        constraints.weightx = 0.1;
        constraints.weighty = 0;
        constraints.ipady = 40;
        constraints.gridwidth = 1;

        //Button to upload log file to pastebin
        JButton buttonCreatePasteBin = new JButton();
        buttonCreatePasteBin.setToolTipText("Upload ServerPackCreator.log to PasteBin.");
        buttonCreatePasteBin.setIcon(ReferenceGUI.folderIcon);
        buttonCreatePasteBin.setPreferredSize(ReferenceGUI.miscButtonDimension);
        constraints.gridx = 0;
        constraints.gridy = 1;
        serverPackCreatorLog.add(buttonCreatePasteBin, constraints);

        //Button to open a new issue on GitHub
        JButton buttonOpenIssue = new JButton();
        buttonOpenIssue.setToolTipText("Create an issue on GitHub.");
        buttonOpenIssue.setIcon(ReferenceGUI.folderIcon);
        buttonOpenIssue.setPreferredSize(ReferenceGUI.miscButtonDimension);
        constraints.gridx = 1;
        constraints.gridy = 1;
        serverPackCreatorLog.add(buttonOpenIssue, constraints);

        //Button to open the invite link to the discord server
        JButton buttonDiscord = new JButton();
        buttonDiscord.setToolTipText("Get support on the ServerPackCreator Discord server.");
        buttonDiscord.setIcon(ReferenceGUI.folderIcon);
        buttonDiscord.setPreferredSize(ReferenceGUI.miscButtonDimension);
        constraints.gridx = 2;
        constraints.gridy = 1;
        serverPackCreatorLog.add(buttonDiscord, constraints);

        return serverPackCreatorLog;
    }
}
