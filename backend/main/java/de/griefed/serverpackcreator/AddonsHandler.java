/* Copyright (C) 2021  Griefed
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 *
 * The full license can be found at https:github.com/Griefed/ServerPackCreator/blob/main/LICENSE
 */
package de.griefed.serverpackcreator;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.griefed.serverpackcreator.i18n.LocalizationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * <strong>Table of methods</strong><p>
 * 1. {@link #AddonsHandler(LocalizationManager)}<br>
 * 2. {@link #initializeAddons()}<br>
 * 3. {@link #getListOfAddons()}<br>
 * 4. {@link #setListOfAddons()}<br>
 * 5. {@link #getListOfServerPackAddons()}<br>
 * 6. {@link #setListOfServerPackAddons(List)}<br>
 * 7. {@link #runServerPackAddons(Configuration)}
 * <p>
 * The AddonHandler provides the ability to load JAR-files from the addons-directory.<br>
 * This allows the execution of additional code after a server pack has been generated.<br>
 * Please note: I am not responsible for data loss or damage caused by using addons. I have no way of verifying whether
 * any addon used does something harmful, neither can I verify their origin. The way to handle this is pretty much the
 * same as with all other software out there: <strong>If you do not trust it, do not use it.</strong><br>
 * <br>
 * An example of an addon can be viewed on GitHub at <a href="https://github.com/Griefed/ServerPackCreatorExampleAddon">ServerPackCreator-ServerPack-Addon-Example</a><br>
 * @author Griefed
 */
@Component
public class AddonsHandler {

    private static final Logger LOG = LogManager.getLogger(AddonsHandler.class);

    private static final Logger LOG_ADDONS = LogManager.getLogger("AddonsLogger");

    private List<String> listOfAddons;
    private List<String> listOfServerPackAddons;
    private final LocalizationManager LOCALIZATIONMANAGER;

    /**
     * <strong>Constructor</strong><p>
     * Used for Dependency Injection.<p>
     * Receives an instance of {@link LocalizationManager} or creates one if the received
     * one is null. Required for use of localization.<p>
     * @author Griefed
     * @param injectedLocalizationManager Instance of {@link LocalizationManager} required for localized log messages.
     */
    @Autowired
    public AddonsHandler(LocalizationManager injectedLocalizationManager) {
        if (injectedLocalizationManager == null) {
            this.LOCALIZATIONMANAGER = new LocalizationManager();
        } else {
            this.LOCALIZATIONMANAGER = injectedLocalizationManager;
        }
    }

    /**
     * Calls {@link #setListOfAddons()} and {@link #setListOfServerPackAddons(List)} in order to generate and set our
     * list of addons overall and a list of addons which will be run right after server pack-generation.
     * @author Griefed
     */
    void initializeAddons() {
        this.listOfAddons = setListOfAddons();

        listOfServerPackAddons = setListOfServerPackAddons(getListOfAddons());
    }

    /**
     * Getter for the list of all installed addons.
     * @author Griefed
     * @return String List. A list of all addons found when ServerPackCreator was started.
     */
    public List<String> getListOfAddons() {
        return listOfAddons;
    }

    /**
     * Setter for the list of installed addons. Creates a list of all JAR-files in the addon-directory.
     * @author Griefed
     * @return String List. A list of all addons found when ServerPackCreator was started.
     */
    private List<String> setListOfAddons() {

        LOG.debug(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.debug.setlistofaddons"));

        File[] listAddonsInstalled = new File("addons").listFiles();
        List<String> addonsInstalled = new ArrayList<>();

        try {
            assert listAddonsInstalled != null;

            for (File addon : listAddonsInstalled) {

                if (addon.isFile() && addon.toString().endsWith(".jar")) {
                    addonsInstalled.add(addon.getAbsolutePath().replace("\\","/"));
                }
            }
        } catch (NullPointerException np) {

            LOG.error(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.error.setlistofaddons"), np);
        }

        if (!addonsInstalled.isEmpty()) {
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.info.setlistofaddons"));
            for (String addon : addonsInstalled) {
                LOG.info(String.format("    %s", addon));
            }
        } else {
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.info.setlistofaddons.notfound"));
        }

        return addonsInstalled;
    }

    /**
     * Getter for the list of addons which will be executed after the generation of a server pack has finished.
     * @author Griefed
     * @return String List. The list of addons which will be executed after the generation of a server pack has finished.
     */
    public List<String> getListOfServerPackAddons() {
        return listOfServerPackAddons;
    }

    /**
     * Setter for the list of server pack addons. Server pack addons are executed right after a server pack was generated.
     * This checks all JAR-files previously gathered in {@link #setListOfAddons()} for the setting in their <code>addon.conf</code>
     * and whether it has a valid configuration of <code>serverpackcreator.addon.type=serverpack</code>. If said configuration
     * is found, the JAR-file is added to the list of server pack addons.
     * @author Griefed
     * @param listOfInstalledAddons Receives a list of all addons installed.
     * @return String List. A list of all addons of which the configuration states that they are for server packs.
     */
    private List<String> setListOfServerPackAddons(List<String> listOfInstalledAddons) {

        List<String> serverPackAddons = new ArrayList<>();

        LOG.debug(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.debug.setlistofserverpackaddons"));

        URL urlToJar;

        for (String addon : listOfInstalledAddons) {

            urlToJar = null;

            LOG.debug(String.format(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.debug.setlistofserverpackaddons.analyzing"), addon));

            try {
                LOG.debug(String.format(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.debug.setlistofserverpackaddons.assigning"), addon));
                urlToJar = new URL(String.format("jar:file:%s!/addon.conf", addon));
                LOG.debug(String.format(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.debug.setlistofserverpackaddons.url"), urlToJar));
            } catch (MalformedURLException ex) {
                LOG.error(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.error.setlistofserverpackaddons.url"), ex);
            }

            assert urlToJar != null;
            Config addonConfig = ConfigFactory.parseURL(urlToJar);

            if (addonConfig.getString("addontype").equals("serverpack")) {
                serverPackAddons.add(addon);
            }
        }

        if (!serverPackAddons.isEmpty()) {
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.info.setlistofserverpackaddons"));
            for (String addon : serverPackAddons) {
                LOG.info(String.format("    %s", addon));
            }
        } else {
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.info.setlistofserverpackaddons.notfound"));
        }

        return serverPackAddons;
    }

    /**
     * Runs every server pack addon with the passed configuration gathered by {@link Configuration#getConfigurationAsList()},
     * allowing for further customization and actions after a server pack has been generated. The base path in which ServerPackCreator
     * itself resides in is passed as the last argument, making it easier for you to work with server pack files and the like.<br>
     * For an example of a Server Pack Addon, see the example addon <a href="https://github.com/Griefed/ServerPackCreatorExampleAddon">ServerPackCreator-ServerPack-Addon-Example</a>
     * <p>
     * <strong>NOTE: All addons are run in the <code>work/temp/addon_name</code>-directory. Be aware of that when creating your addons!</strong>
     * @author Griefed
     * @param serverPackConfiguration The instance of {@link Configuration} with which a server pack was generated.
     */
    void runServerPackAddons(Configuration serverPackConfiguration) {

        List<String> commandArguments = new ArrayList<>();
        List<String> addonsToExecute = getListOfServerPackAddons();

        ProcessBuilder processBuilder;
        Process process;

        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader;
        String processLog;

        String tempDirectory = "work/temp/";
        String serverPackCreatorBaseDirectory = new File("").getAbsolutePath().replace("\\","/");
        String addonName;
        File workDirectory;

        if (!addonsToExecute.isEmpty()) {

            try {

                for (String addon : addonsToExecute) {

                    addonName = (addon.substring(addon.lastIndexOf("/") + 1)).substring(0, (addon.substring(addon.lastIndexOf("/") + 1)).length() - 4);

                    workDirectory = new File(tempDirectory + addonName);
                    Files.createDirectories(workDirectory.toPath());

                    LOG_ADDONS.debug("Addon work-directory is: " + workDirectory);

                    commandArguments.clear();

                    commandArguments.add(serverPackConfiguration.getJavaPath());
                    commandArguments.add("-jar");
                    commandArguments.add(addon);

                    commandArguments.addAll(serverPackConfiguration.getConfigurationAsList());

                    commandArguments.add(serverPackCreatorBaseDirectory);

                    processBuilder = new ProcessBuilder(commandArguments).directory(workDirectory);

                    LOG_ADDONS.debug(String.format(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.debug.runserverpackaddons"), processBuilder.command()));

                    processBuilder.redirectErrorStream(true);

                    process = processBuilder.start();

                    inputStreamReader = new InputStreamReader(process.getInputStream());

                    bufferedReader = new BufferedReader(inputStreamReader);

                    while (true) {

                        processLog = bufferedReader.readLine();

                        if (processLog == null) {
                            break;
                        }

                        LOG_ADDONS.info(processLog);
                    }

                    bufferedReader.close();
                    inputStreamReader.close();
                    process.destroy();
                }

            } catch (IOException ex) {
                LOG_ADDONS.error(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.error.runserverpackaddons"), ex);
            }

            LOG_ADDONS.info(LOCALIZATIONMANAGER.getLocalizedString("addonshandler.log.info.runserverpackaddons"));
        }
    }
}