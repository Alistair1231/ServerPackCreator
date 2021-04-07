package de.griefed.ServerPackCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Main {
    private static final Logger appLogger = LogManager.getLogger(Main.class);
    /** Main class makes the calls to every other class where the actual magic is happening. The main class of ServerPackCreator should never contain code which does work on the server pack itself.
     * @param args Command Line Argument determines whether ServerPackCreator will start into normal operation mode or with a step-by-step generation of a configuration file.
     */
    public static void main(String[] args) {
        appLogger.warn("################################################################");
        appLogger.warn("#                      WORK IN PROGRESS!                       #");
        appLogger.warn("#  USE AT YOUR OWN RISK! BE AWARE THAT DATA LOSS IS POSSIBLE!  #");
        appLogger.warn("#        I WILL NOT BE HELD RESPONSIBLE FOR DATA LOSS!         #");
        appLogger.warn("#                    YOU HAVE BEEN WARNED!                     #");
        appLogger.warn("################################################################");

        try {
            String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
            String javaVersion = System.getProperty("java.version");
            String osArch = System.getProperty("os.arch");
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            appLogger.info("SYSTEM INFORMATION:");
            appLogger.info(String.format("JAR Path: %s", jarPath));
            appLogger.info(String.format("JAR Name: %s", jarName));
            appLogger.info(String.format("Java version: %s", javaVersion));
            appLogger.info(String.format("OS architecture: %s", osArch));
            appLogger.info(String.format("OS name: %s", osName));
            appLogger.info(String.format("OS version: %s", osVersion));
            appLogger.info("Include this information when reporting an issue on GitHub.");

        } catch (URISyntaxException ex) {
            appLogger.error("Error getting system properties.", ex);
        }
        if (Arrays.asList(args).contains(Reference.CONFIG_GEN_ARGUMENT) || (!Reference.oldConfigFile.exists() && !Reference.configFile.exists())){
            CLISetup.setup();
        }
        if (!FilesSetup.filesSetup()) {
            appLogger.warn("################################################################");
            appLogger.warn("#             ONE OR MORE DEFAULT FILE(S) GENERATED.           #");
            appLogger.warn("# CHECK THE LOGS TO FIND OUT WHICH FILE(S) WAS/WERE GENERATED. #");
            appLogger.warn("#         CUSTOMIZE, THEN RUN SERVERPACKCREATOR AGAIN!         #");
            appLogger.warn("################################################################");
            System.exit(0);
        }
        if (!ConfigCheck.checkConfig()) {
            CopyFiles.cleanupEnvironment(Reference.modpackDir);
            try {
                CopyFiles.copyFiles(Reference.modpackDir, Reference.copyDirs, Reference.clientMods);
            } catch (IOException ex) {
                appLogger.error("There was an error calling the copyFiles method.", ex);
            }
            CopyFiles.copyStartScripts(Reference.modpackDir, Reference.modLoader, Reference.includeStartScripts);
            if (Reference.includeServerInstallation) {
                ServerSetup.installServer(Reference.modLoader, Reference.modpackDir, Reference.minecraftVersion, Reference.modLoaderVersion, Reference.javaPath);
            } else {
                appLogger.info("Not installing modded server.");
            }
            if (Reference.includeServerIcon) {
                CopyFiles.copyIcon(Reference.modpackDir);
            } else {
                appLogger.info("Not including servericon.");
            }
            if (Reference.includeServerProperties) {
                CopyFiles.copyProperties(Reference.modpackDir);
            } else {
                appLogger.info("Not including server.properties.");
            }
            if (Reference.includeZipCreation) {
                ServerSetup.zipBuilder(Reference.modpackDir, Reference.modLoader, Reference.includeServerInstallation);
            } else {
                appLogger.info("Not creating zip archive of serverpack.");
            }
            appLogger.info(String.format("Server pack available at: %s/server_pack", Reference.modpackDir));
            appLogger.info(String.format("Server pack archive available at : %s/server_pack.zip", Reference.modpackDir));
            appLogger.info("Done!");
            System.exit(0);
        } else {
            appLogger.error("ERROR: Please check your serverpackcreator.conf for any incorrect settings. This message is also displayed if ServerPackCreator downloaded and setup a modpack from a projectID,fileID for modpackDir.");
            System.exit(1);
        }
    }
}
