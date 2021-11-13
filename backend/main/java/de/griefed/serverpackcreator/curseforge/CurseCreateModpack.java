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
package de.griefed.serverpackcreator.curseforge;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import de.griefed.serverpackcreator.ConfigurationModel;
import de.griefed.serverpackcreator.i18n.LocalizationManager;
import de.griefed.serverpackcreator.ApplicationProperties;
import de.griefed.serverpackcreator.utilities.ReticulatingSplines;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Download a modpack from CurseForge and create it by unzipping the ZIP-archive, copy all folders and files from the
 * override directory to the parent directory, download all mods in said modpack, and delete no longer needed files.
 * Modpacks are create in a ProjectName/FileDisplayName structure. Before a modpack is created, the FileDisplayName folder
 * is checked for existence and deleted should it already exist. The reason for this is the order in which ServerPackCreator
 * checks and uses the configuration file.<p>
 * First: the configuration is checked for a valid CurseForge projectID,fileID combination.<br>
 * Second: The folder structure is checked for an already existing folder with FileDisplayName and if it exists it is deleted.<br>
 * Third: The modpack is created and all mods are downloaded etc., files are copied, etc.<br>
 * Fourth: Information about the modpack is acquired from the modpack's manifest.json and written to a new configuration file
 * with said information. The configuration for modpackDir, which previously contained a projectID,fileID is replaced
 * with the path to the new modpack at ProjectName/FileDisplayName.<br>
 * If modpackDir holds a projectID,fileID combination we have to assume the modpack has yet to be created, thus
 * we need to make sure the target directory is empty in order to create a clean and fresh modpack.<br>
 * If modpackDir does not hold a projectID,fileID we have to assume it is a path pointing at a directory which already
 * contains a modpack we can work with and create a server pack from. Thus, we clean up the environment if modpackDir holds
 * a projectID,fileID.
 * @author Griefed
 */
@Component
public class CurseCreateModpack {
    private static final Logger LOG = LogManager.getLogger(CurseCreateModpack.class);

    private final LocalizationManager LOCALIZATIONMANAGER;

    private final ReticulatingSplines reticulatingSplines = new ReticulatingSplines();

    private final Random randInt = new Random();

    private ApplicationProperties serverPackCreatorProperties;

    /**
     * <strong>Constructor</strong><p>
     * Used for Dependency Injection.<p>
     * Receives an instance of {@link LocalizationManager} or creates one if the received
     * one is null. Required for use of localization.
     * @author Griefed
     * @param injectedLocalizationManager Instance of {@link LocalizationManager} required for localized log messages.
     * @param injectedServerPackCreatorProperties Instance of {@link Properties} required for various different things.
     */
    @Autowired
    public CurseCreateModpack(LocalizationManager injectedLocalizationManager, ApplicationProperties injectedServerPackCreatorProperties) {
        if (injectedServerPackCreatorProperties == null) {
            this.serverPackCreatorProperties = new ApplicationProperties();
        } else {
            this.serverPackCreatorProperties = injectedServerPackCreatorProperties;
        }

        if (injectedLocalizationManager == null) {
            this.LOCALIZATIONMANAGER = new LocalizationManager(serverPackCreatorProperties);
        } else {
            this.LOCALIZATIONMANAGER = injectedLocalizationManager;
        }
    }

    /**
     * Retrieve the name of a given CurseForge project.
     * @author Griefed
     * @param newProjectID The ID of the new CurseForge project.
     */
    public String retrieveProjectName(int newProjectID) {
        try {

            if (CurseAPI.project(newProjectID).isPresent()) {

                return CurseAPI.project(newProjectID).get().name();

            } else {

                return String.valueOf(newProjectID);
            }

        } catch (CurseException cex) {

            // TODO: Improve error message
            LOG.error(cex);
            return String.valueOf(newProjectID);
        }
    }

    /**
     * Retrieve the file disk-name of a given CurseForge project.
     * @author Griefed
     * @param newProjectID The ID of the CurseForge project.
     * @param newFileID The ID of the CurseForge file.
     */
    public String retrieveFileDiskName(int newProjectID, int newFileID) {
        try {

            return CurseAPI.project(newProjectID).get().files().fileWithID(newFileID).nameOnDisk();

        } catch (NullPointerException | CurseException ex) {

            LOG.error("Filediskname for file " + newFileID + " not found. Using fileID.", ex);
            return String.valueOf(newFileID);
        }
    }

    /**
     * Get the display-name of the project's file. If this fails, the disk-name is returned. If THAT fails, too, the fileID
     * is returned.
     * @author Griefed
     * @param projectID int. The id of the project.
     * @param fileID int. The id of the file.
     * @return String. The name of the file.
     */
    public String retrieveFileName(int projectID, int fileID) {
        try {

            return CurseAPI.project(projectID).orElseThrow(NullPointerException::new).files().fileWithID(fileID).displayName();

        } catch (CurseException | NullPointerException ex) {

            // TODO: Replace with lang key
            LOG.warn("Display name for file " + fileID + " not found. Checking filename.");

            try {

                return CurseAPI.project(projectID).orElseThrow(NullPointerException::new).files().fileWithID(fileID).nameOnDisk();

            } catch (CurseException | NullPointerException ex2) {

                // TODO: Replace with lang key
                LOG.warn("Filediskname for file " + fileID + " not found. Using fileID.");

                return String.valueOf(fileID);

            }

        }
    }

    /**
     * Ensures the modloader is normalized to first letter upper case and rest lower case. Basically allows the user to
     * input Forge or Fabric in any combination of upper- and lowercase and ServerPackCreator will still be able to
     * work with the users input.
     * @author Griefed
     * @param modloader String. The String to check for case-insensitive cases of either Forge or Fabric.
     * @return String. Returns a normalized String of the specified modloader.
     */
    String setModloaderCase(String modloader) {

        if (modloader.equalsIgnoreCase("Forge")) {
            return "Forge";

        } else if (modloader.equalsIgnoreCase("Fabric")) {

            return "Fabric";
        } else {
            // TODO: Replace with lang key
            LOG.warn("Couldn't determine modloader. Returning \"Forge\".");
            return "Forge";
        }
    }

    /**
     * Getter for the object-mapper used for working with JSON-data.
     * @author Griefed
     * @return ObjectMapper. Returns the object-mapper used for working with JSON-data.
     */
    ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return objectMapper;
    }

    /**
     * Acquires the names of the CurseForge project and file. Should no filename exist, we will use the fileDiskName as
     * fallback to ensure we always have a folder-structure of projectName/FileDisplayName at hand in which the modpack
     * will be created. Calls<br>
     * {@link CurseAPI} and various methods of it in order to acquire information about the modpack.<br>
     * {@link #checkCurseForgeDir(String)}<br>
     * {@link #initializeModpack(String, Integer, Integer, ConfigurationModel)}
     * @author Griefed
     * @param configurationModel Instance of {@link ConfigurationModel}. Required for getting the directory in which we
     *                           will create our modpack.
     * @param projectID Integer. The ID of the project. Used to gather information about the CurseForge project and to
     *                 download the modpack.
     * @param fileID Integer. The ID of the file. Used to gather information about the CurseForge file and to download
     *              the modpack.
     */
    public void curseForgeModpack(ConfigurationModel configurationModel, Integer projectID, Integer fileID) {

        try {
            if (CurseAPI.project(projectID).isPresent()) {

                configurationModel.setProjectName(retrieveProjectName(projectID));
                configurationModel.setFileName(retrieveFileName(projectID, fileID));
                configurationModel.setFileDiskName(retrieveFileDiskName(projectID, fileID));

                configurationModel.setModpackDir(String.format("./work/modpacks/%s/%s", projectID, fileID));
            }
        } catch (CurseException cex) {
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.error(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.error.curseforgemodpack"), projectID, fileID), cex);
        }

        if (    !checkCurseForgeDir(configurationModel.getModpackDir()) &&
                !configurationModel.getProjectName().equals(String.valueOf(projectID)) &&
                !configurationModel.getFileDiskName().equals(String.valueOf(fileID))) {

            initializeModpack(configurationModel.getModpackDir(), projectID, fileID, configurationModel);

        } else {
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.overwrite"));
        }
    }

    /**
     * Downloads the specified file of the specified project to a directory which is the combination of the project
     * name and file display name. Unzips the downloaded modpack ZIP-archive, gathers and displays information about the
     * specified project/file and makes calls to methods which further set up the modpack. Calls<br>
     * {@link CurseAPI} and various methods of it to create the modpack.<br>
     * {@link #unzipArchive(String, String)}<br>
     * {@link #copyOverride(String)}<br>
     * {@link #downloadMods(String, ConfigurationModel)}<br>
     * @author Griefed
     * @param modpackDir String. Combination of project name and file name. Created during download procedure and later
     *                  replaces the modpackDir variable in the configuration file.
     * @param projectID Integer. The ID of the project. Used to gather information and to download the modpack.
     * @param fileID Integer. The ID of the file. Used to gather information and to download the modpack.
     * @param configurationModel {@link ConfigurationModel} instance containing info about our modpack and server pack.
     */
    void initializeModpack(String modpackDir, Integer projectID, Integer fileID, ConfigurationModel configurationModel) {
        try {
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.download"), configurationModel.getProjectName(), configurationModel.getFileName()));

            CurseAPI.downloadFileToDirectory(projectID, fileID, Paths.get(modpackDir));
        } catch (CurseException cex) {
            LOG.error(String.format("Error: Could not download file %s for project %s to directory %s.", configurationModel.getFileName(), configurationModel.getProjectName(), modpackDir));
        }

        unzipArchive(String.format("%s/%s", modpackDir, configurationModel.getFileDiskName()), modpackDir);
        //TODO: Refactor to use apache commons
        boolean isFileDeleted = new File(String.format("%s/%s", modpackDir, configurationModel.getFileDiskName())).delete();
        if (isFileDeleted) {
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.deletezip"));
        }

        if (new File(String.format("%s/manifest.json", modpackDir)).exists()) {
            try {
                byte[] jsonData = Files.readAllBytes(Paths.get(String.format("%s/manifest.json", modpackDir)));
                configurationModel.setCurseModpack(getObjectMapper().readTree(jsonData));

                String[] modloaderAndVersion = configurationModel.getCurseModpack().get("minecraft").get("modLoaders").get(0).get("id").asText().split("-");

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.infoheader"));
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.modpackname"), configurationModel.getCurseModpack().get("name").asText()));
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.modpackversion"), configurationModel.getCurseModpack().get("version").asText()));
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.modpackauthor"), configurationModel.getCurseModpack().get("author").asText()));
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.modpackminecraftversion"), configurationModel.getCurseModpack().get("minecraft").get("version").asText()));
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.modloader"), modloaderAndVersion[0]));
                LOG.info(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.initializemodpack.modloaderversion"), modloaderAndVersion[1]));

            } catch (IOException ex) {
                LOG.error("Error: There was a fault during json parsing.", ex);
            }

            copyOverride(modpackDir);
            if (new File(String.format("%s/overrides", modpackDir)).isDirectory()) {
                try {
                    //TODO: Refactor to use apache commons
                    Path pathToBeDeleted = Paths.get(String.format("%s/overrides", modpackDir));
                    //noinspection ResultOfMethodCallIgnored
                    Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                } catch (IOException ex) {
                    LOG.debug("Directory \"overrides\" not found. Skipping delete action...");
                }
            }

            downloadMods(modpackDir, configurationModel);
        }
    }

    /**
     * Downloads all mods specified in the modpack's manifest.json file. If a download of a mod fails, it will be
     * retried once before treating it as "currently unavailable" and adding the URL to the failed download
     * to a list which will be printed to the console and logs after the method has finished. The user will need to
     * download these failed mods themselves as ServerPackCreator couldn't, for whatever reason, successfully download them.
     * If the acquisition of the download URL fails as well....well we're out of luck, then. The user will have to figure
     * this out on their own. Possible reasons for a failed download and failed URL acquisition might be that the file
     * was taken down, no longer exists, CurseForge is unavailable etc. etc. There's nothing we can do about that.
     * @author Griefed
     * @param modpackDir String. All mods are downloaded to the child-directory "mods" inside the modpack directory.
     * @param configurationModel {@link ConfigurationModel} instance containing info about our modpack and server pack.
     */
    void downloadMods(String modpackDir, ConfigurationModel configurationModel) {
        /* This log is meant to be read by the user, therefore we allow translation. */
        LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.downloadmods.info"));
        List<String> failedDownloads = new ArrayList<>();

        for (int i = 0; i < configurationModel.getCurseModpack().get("files").size(); i++) {

            if (randInt.nextInt(configurationModel.getCurseModpack().get("files").size())==randInt.nextInt(configurationModel.getCurseModpack().get("files").size())) {
                LOG.info(reticulatingSplines.reticulate());
            }

            String modName, modFileName;
            modName = modFileName = "";

            try {

                //noinspection OptionalGetWithoutIsPresent
                modName = CurseAPI.project(configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt()).get().name();

                modFileName = retrieveFileName(
                        configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                        configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt()
                );

            } catch (CurseException cex) {
                LOG.error("Error: Couldn't retrieve CurseForge project name and file name.", cex);
            }

            try {

                LOG.info(String.format(
                        LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.downloadmods.specificmod"),
                        i+1, configurationModel.getCurseModpack().get("files").size(), modName, modFileName)
                );

                //Download mod
                CurseAPI.downloadFileToDirectory(
                        configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                        configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt(),
                        Paths.get(String.format("%s/mods", modpackDir))
                );

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException iex) {
                    LOG.debug("Error during interruption event.", iex);
                }

            } catch (CurseException cex) {
                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.error(String.format(
                        LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.error.downloadmods.errordownload"),
                        modName,
                        configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                        modFileName,
                        configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt())
                );

                try {

                    /* This log is meant to be read by the user, therefore we allow translation. */
                    LOG.info(String.format(
                            LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.downloadmods.tryagain"),
                            modName, configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                            modFileName,
                            configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt())
                    );

                    //Retry download if previous attempt failed
                    CurseAPI.downloadFileToDirectory(
                            configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                            configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt(),
                            Paths.get(String.format("%s/mods", modpackDir))
                    );

                } catch (CurseException cex2) {

                    /* This log is meant to be read by the user, therefore we allow translation. */
                    LOG.error(String.format(
                            LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.error.downloadmods.retryfail"),
                            modName,
                            configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                            modFileName,
                            configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt())
                    );

                    try {
                        //Add URL of failed download to list
                        failedDownloads.add(String.format("Mod: %s, ID: %d. File: %s, ID: %d, URL: %s",
                                modName,
                                configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                                modFileName,
                                configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt(),
                                CurseAPI.fileDownloadURL(configurationModel.getCurseModpack().get("files").get(i).get("projectID").asInt(),
                                configurationModel.getCurseModpack().get("files").get(i).get("fileID").asInt()))
                        );

                    } catch (CurseException cex3) {

                        LOG.error("Error: An error occurred during URL retrieval.", cex3);
                    }
                }
            }
        }

        if (failedDownloads.size() != 0) {
            //Print the URLs of failed downloads, if there are any
            for (int i = 0; i <= failedDownloads.size(); i++) {
                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.error(String.format(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.error.downloadmods.urllist"), failedDownloads.get(i)));
            }
        }
    }

    /**
     * Recursively copy all folders and files from the override directory to the parent directory, our modpack directory.
     * @author Griefed
     * @param modpackDir String. The 'overrides' directory resides in this directory. All folders and files within overrides
     *                  are copied to the parent directory, the modpack directory.
     */
    void copyOverride(String modpackDir) {
        /* This log is meant to be read by the user, therefore we allow translation. */
        LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.copyoverrides.info"));
        try {

            //TODO: Refactor to use apache commons
            Stream<Path> files = Files.walk(Paths.get(String.format("%s/overrides", modpackDir)));
            files.forEach(file -> {
                try {
                    Files.copy(file, Paths.get(modpackDir).resolve(Paths.get(String.format("%s/overrides", modpackDir)).relativize(file)), REPLACE_EXISTING);
                    LOG.debug("Copying: " + file.toAbsolutePath());
                } catch (IOException ex) {
                    if (!ex.toString().startsWith("java.nio.file.DirectoryNotEmptyException")) {
                        LOG.error("An error occurred copying files from overrides to parent directory.", ex);
                    }
                }
            });
            files.close();
        } catch (IOException ex) {
            LOG.error("An error occurred copying files from overrides to parent directory.", ex);
        }
    }

    /**
     * Check whether the folder for the specified CurseForge projectID/fileID exists and if it does exist, delete it
     * recursively to ensure we are working with a clean environment when creating a modpack from CurseForge.<br>
     * Calls {@link #cleanupEnvironment(String)} to ensure a clean environment when we create a new modpack from CurseForge.
     * @author Griefed
     * @param modpackDir String. The path to the modpack directory, a combination of project name and file display name.
     * @return Boolean. Returns true if something went wrong during the cleanup of the modpack directory. If the cleanup
     * procedure finished successfully, and we have a clean environment, false is returned. Returns false if the modpack
     * directory could not be found, indicating a clean environment.
     */
    boolean checkCurseForgeDir(String modpackDir) {
        boolean isModpackPresent = false;
        if (!(new File(modpackDir).isDirectory()) && !(new File(String.format("%s/manifest.json", modpackDir)).exists())) {
            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.checkcurseforgedir.create"));
        } else {
            if (serverPackCreatorProperties.getProperty("de.griefed.serverpackcreator.curseforge.cleanup.enabled").equalsIgnoreCase("true")) {

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.checkcurseforgedir"));
                isModpackPresent = cleanupEnvironment(modpackDir);

            } else {
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.cleanup"));
                isModpackPresent = true;
            }
        }
        return isModpackPresent;
    }

    /**
     * Unzips the downloaded modpack ZIP-archive to the specified directory.
     * @author Griefed
     * @param zipFile String. The path to the ZIP-archive which we want to unzip.
     * @param modpackDir The directory into which the ZIP-archive will be unzipped into.
     */
    void unzipArchive(String zipFile, String modpackDir) {
        /* This log is meant to be read by the user, therefore we allow translation. */
        LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.unziparchive"));
        try {
            new ZipFile(zipFile).extractAll(modpackDir);
        } catch (ZipException ex) {
            LOG.error("Error: There was an error extracting the archive " + zipFile, ex);
        }
    }

    /**
     * Deletes any and all folder and files, recursively, inside the target directory, thus ensuring we are working in a
     * clean environment when creating a new modpack from CurseForge.
     * @author Griefed
     * @param modpackDir String. The directory we want to delete.
     * @return Boolean. Returns false if every file and folder was, recursively and successfully, deleted.
     */
    public boolean cleanupEnvironment(String modpackDir) {
        boolean cleanedUp = false;

        if (new File(modpackDir).exists()) {

            /* This log is meant to be read by the user, therefore we allow translation. */
            LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.cleanupenvironment.enter"));
            Path modpackPath = Paths.get(modpackDir);

            try {
                //TODO: Refactor to use commons io
                Files.walkFileTree(modpackPath,

                        new SimpleFileVisitor<Path>() {

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                Files.delete(dir);
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Files.delete(file);
                                return FileVisitResult.CONTINUE;
                            }

                        });


            } catch (FileSystemException ex) {

                LOG.error("Windows is blocking the deletion of a file. You need to delete \"" + modpackDir + "\" manually.", ex);

            } catch (IOException ex) {

                cleanedUp = true;
                LOG.error("Error deleting a file from the CurseForge directory " + modpackDir, ex);

            } finally {

                /* This log is meant to be read by the user, therefore we allow translation. */
                LOG.info(LOCALIZATIONMANAGER.getLocalizedString("cursecreatemodpack.log.info.cleanupenvironment.complete"));
            }
        }
        return cleanedUp;
    }
}