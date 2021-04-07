package de.griefed.ServerPackCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ServerSetup {
    private static final Logger appLogger = LogManager.getLogger(FilesSetup.class);
    private static final Logger installerLogger = LogManager.getLogger("InstallerLogger");
    /** Installs the files for a Forge/Fabric server.
     * @param modLoader String. The modloader for which to install the server.
     * @param modpackDir String. /server_pack The directory where the modloader server will be installed in.
     * @param minecraftVersion String. The Minecraft version for which to install the modloader and Minecraft server.
     * @param modLoaderVersion String. The modloader version for which to install the modloader and Minecraft server.
     * @param javaPath String. Path to Java installation needed to execute the Fabric and Forge installers.
     */
    static void installServer(String modLoader, String modpackDir, String minecraftVersion, String modLoaderVersion, String javaPath) {
        File fabricInstaller = new File(String.format("%s/server_pack/fabric-installer.jar", modpackDir));
        File forgeInstaller = new File(String.format("%s/server_pack/forge-installer.jar", modpackDir));
        if (modLoader.equalsIgnoreCase("Fabric")) {
            try {
                appLogger.info("Starting Fabric installation.");
                if (ServerUtilities.downloadFabricJar(modpackDir)) {
                    appLogger.info("Fabric installer successfully downloaded. Installing Fabric. This may take a while...");
                    ProcessBuilder processBuilder = new ProcessBuilder(
                            javaPath,
                            "-jar",
                            "fabric-installer.jar",
                            "server",
                            String.format("-mcversion %s", minecraftVersion),
                            String.format("-loader %s", modLoaderVersion),
                            "-downloadMinecraft").directory(new File(String.format("%s/server_pack", modpackDir)));
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while (true) {
                        line = reader.readLine();
                        if (line == null) { break; }
                        installerLogger.info(line);
                    }
                    appLogger.info("For details regarding the installation of this modloader server, see logs/modloader_installer.log");
                    appLogger.info("Returning to ServerPackCreator.");
                } else {
                    appLogger.error("Something went wrong during the installation of Fabric. Maybe the Fabric server are down or unreachable? Skipping...");
                }
            } catch (IOException ex) {
                appLogger.error("An error occurred during Fabric installation.", ex);
            }
        } else if (modLoader.equalsIgnoreCase("Forge")) {
            try {
                appLogger.info("Starting Forge installation.");
                if (ServerUtilities.downloadForgeJar(minecraftVersion, modLoaderVersion, modpackDir)) {
                    appLogger.info("Forge installer successfully downloaded. Installing Forge. This may take a while...");
                    ProcessBuilder processBuilder = new ProcessBuilder(
                            javaPath,
                            "-jar",
                            "forge-installer.jar",
                            "--installServer")
                            .directory(new File(String.format("%s/server_pack", modpackDir)));
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while (true) {
                        line = reader.readLine();
                        if (line == null) { break; }
                        installerLogger.info(line);
                    }
                    appLogger.info("For details regarding the installation of this modloader server, see logs/modloader_installer.log");
                    appLogger.info("Returning to ServerPackCreator.");
                } else {
                    appLogger.error("Something went wrong during the installation of Forge. Maybe the Forge servers are down or unreachable? Skipping...");
                }
            } catch (IOException ex) {
                appLogger.error("An error occurred during Forge installation.", ex);
            }
        } else {
            appLogger.error(String.format("Specified invalid modloader: %s", modLoader));
        }
        ServerUtilities.generateDownloadScripts(modLoader, modpackDir, minecraftVersion);
        ServerUtilities.cleanUpServerPack(
                fabricInstaller,
                forgeInstaller,
                modLoader,
                modpackDir,
                minecraftVersion, modLoaderVersion);
    }
    /** Create a zip-archive of the serverpack, excluding Mojang's minecraft_server.jar.
     * With help from https://stackoverflow.com/questions/1091788/how-to-create-a-zip-file-in-java
     * @param modpackDir String. The directory where the zip-archive will be created and saved in.
     * @param modLoader String. Determines the name of Minecraft#s server jar which will be deleted from the zip-archive.
     * @param includeServerInstallation Boolean. Determines whether the Minecraft server jar needs to be deleted from the zip-archive.
     */
    static void zipBuilder(String modpackDir, String modLoader, Boolean includeServerInstallation) {
        final Path sourceDir = Paths.get(String.format("%s/server_pack", modpackDir));
        String zipFileName = sourceDir.toString().concat(".zip");
        appLogger.info("Creating zip archive of serverpack...");
        try {
            final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    try {
                        Path targetFile = sourceDir.relativize(file);
                        outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
                        byte[] bytes = Files.readAllBytes(file);
                        outputStream.write(bytes, 0, bytes.length);
                        outputStream.closeEntry();
                    } catch (IOException ex) {
                        appLogger.error("There was an error during zip creation", ex);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            outputStream.close();
        } catch (IOException ex) {
            appLogger.error("There was an error during zip creation", ex);
        }
        if (includeServerInstallation) {
            ServerUtilities.deleteMinecraftJar(modLoader, modpackDir);
            appLogger.warn("!!!       NOTE: The minecraft_server.jar will not be included in the zip-archive.       !!!");
            appLogger.warn("!!! Mojang strictly prohibits the distribution of their software through third parties. !!!");
            appLogger.warn("!!!   Tell your users to execute the download scripts to get the Minecraft server jar.  !!!");
        }
        appLogger.info("Finished creation of zip archive.");
    }
    /** Deletes client-side-only mods in server_pack, if specified.
     * @param modpackDir String. /server_pack/mods The directory where the files will be deleted.
     * @param clientMods String List. Client mods to delete.
     */
    @SuppressWarnings("unused")
    @Deprecated
    static void deleteClientMods(String modpackDir, List<String> clientMods) {
        appLogger.info("Deleting client-side mods from serverpack:");
        File serverMods = new File(String.format("%s/server_pack/mods", modpackDir));
        Set<String> fileList = new HashSet<>();
        try {
            Files.walkFileTree(Paths.get(String.format("%s/server_pack/mods", modpackDir)), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!Files.isDirectory(file)) {
                        fileList.add(file.getFileName().toString());
                        for (int i = 0; i < clientMods.toArray().length; i++) {
                            if (file.getFileName().toString().contains(clientMods.get(i))) {
                                boolean isDeleted = file.toFile().delete();
                                if (isDeleted) {
                                    appLogger.info(String.format("    %s", file.getFileName().toString()));
                                } else {
                                    appLogger.error(String.format("Could not delete: %s", file.getFileName().toString()));
                                }
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            appLogger.error("Error: There was an error during deletion of clientside mods.", ex);
        }
        for (int i = 0; i < fileList.toArray().length; i++) {
            appLogger.debug(String.format("DEBUG: Remaining: %s", fileList.toArray()[i]));
        }
    }
}