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

import de.griefed.serverpackcreator.i18n.LocalizationManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * <strong>Table of tests</strong>
 * <p>
 * 1. {@link #DefaultFilesTest()}<br>
 * 2. {@link #getConfigFileTest()}<br>
 * 3. {@link #getOldConfigFileTest()}<br>
 * 4. {@link #getPropertiesFileTest()}<br>
 * 5. {@link #getIconFileTest()}<br>
 * 6. {@link #getForgeWindowsFileTest()}<br>
 * 7. {@link #getForgeLinuxFileTest()}<br>
 * 8. {@link #getFabricWindowsFileTest()}<br>
 * 9. {@link #getFabricLinuxFileTest()}<br>
 * 10.{@link #getMinecraftManifestUrlTest()}<br>
 * 11.{@link #getForgeManifestUrlTest()}<br>
 * 12.{@link #getFabricManifestUrlTest()}<br>
 * 13.{@link #getFabricInstallerManifestUrlTest()}<br>
 * 14.{@link #getManifestMinecraftTest()}<br>
 * 15.{@link #getManifestForgeTest()}<br>
 * 16.{@link #getManifestFabricTest()}<br>
 * 17.{@link #getManifestFabricInstallerTest()}<br>
 * 18.{@link #getManifestMinecraftTestEquals()}<br>
 * 19.{@link #getManifestForgeTestEquals()}<br>
 * 20.{@link #getManifestFabricTestEquals()}<br>
 * 21.{@link #getManifestFabricInstallerTestEquals()}<br>
 * 22.{@link #checkForConfigTestOld}<br>
 * 23.{@link #checkForConfigTest}<br>
 * 24.{@link #checkForConfigTestNew}<br>
 * 25.{@link #checkForFabricLinuxTest}<br>
 * 26.{@link #checkForFabricLinuxTestNew}<br>
 * 27.{@link #checkForFabricWindowsTest}<br>
 * 28.{@link #checkForFabricWindowsTestNew}<br>
 * 29.{@link #checkForForgeLinuxTest}<br>
 * 30.{@link #checkForForgeLinuxTestNew}<br>
 * 31.{@link #checkForForgeWindowsTest}<br>
 * 32.{@link #checkForForgeWindowsTestNew}<br>
 * 33.{@link #checkForPropertiesTest}<br>
 * 34.{@link #checkForPropertiesTestNew}<br>
 * 35.{@link #checkForIconTest}<br>
 * 36.{@link #checkForIconTestNew}<br>
 * 37.{@link #filesSetupTest}<br>
 * 38.{@link #downloadMinecraftManifestTest}<br>
 * 39.{@link #downloadFabricManifestTest}<br>
 * 40.{@link #downloadForgeManifestTest}<br>
 * 41.{@link #downloadFabricInstallerManifestTest}
 */
class DefaultFilesTest {

    private final DefaultFiles DEFAULTFILES;
    private final LocalizationManager LOCALIZATIONMANAGER;

    DefaultFilesTest() {
        try {
            FileUtils.copyFile(new File("backend/main/resources/serverpackcreator.properties"),new File("serverpackcreator.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOCALIZATIONMANAGER = new LocalizationManager();
        LOCALIZATIONMANAGER.init();
        DEFAULTFILES = new DefaultFiles(LOCALIZATIONMANAGER);
    }

    @Test
    void getConfigFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getConfigFile());
    }

    @Test
    void getOldConfigFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getOldConfigFile());
    }

    @Test
    void getPropertiesFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getPropertiesFile());
    }

    @Test
    void getIconFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getIconFile());
    }

    @Test
    void getForgeWindowsFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getForgeWindowsFile());
    }

    @Test
    void getForgeLinuxFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getForgeLinuxFile());
    }

    @Test
    void getFabricWindowsFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getFabricWindowsFile());
    }

    @Test
    void getFabricLinuxFileTest() {
        Assertions.assertNotNull(DEFAULTFILES.getFabricLinuxFile());
    }

    @Test
    void getMinecraftManifestUrlTest() {
        Assertions.assertNotNull(DEFAULTFILES.getMinecraftManifestUrl());
    }

    @Test
    void getForgeManifestUrlTest() {
        Assertions.assertNotNull(DEFAULTFILES.getForgeManifestUrl());
    }

    @Test
    void getFabricManifestUrlTest() {
        Assertions.assertNotNull(DEFAULTFILES.getFabricManifestUrl());
    }

    @Test
    void getFabricInstallerManifestUrlTest() {
        Assertions.assertNotNull(DEFAULTFILES.getFabricInstallerManifestUrl());
    }

    @Test
    void getManifestMinecraftTest() {
        Assertions.assertNotNull(DEFAULTFILES.getMANIFEST_MINECRAFT());
    }

    @Test
    void getManifestForgeTest() {
        Assertions.assertNotNull(DEFAULTFILES.getMANIFEST_FORGE());
    }

    @Test
    void getManifestFabricTest() {
        Assertions.assertNotNull(DEFAULTFILES.getMANIFEST_FABRIC());
    }

    @Test
    void getManifestFabricInstallerTest() {
        Assertions.assertNotNull(DEFAULTFILES.getMANIFEST_FABRIC_INSTALLER());
    }

    @Test
    void getManifestMinecraftTestEquals() {
        Assertions.assertEquals("minecraft-manifest.json", DEFAULTFILES.getMANIFEST_MINECRAFT().toString());
    }

    @Test
    void getManifestForgeTestEquals() {
        Assertions.assertEquals("forge-manifest.json", DEFAULTFILES.getMANIFEST_FORGE().toString());
    }

    @Test
    void getManifestFabricTestEquals() {
        Assertions.assertEquals("fabric-manifest.xml", DEFAULTFILES.getMANIFEST_FABRIC().toString());
    }

    @Test
    void getManifestFabricInstallerTestEquals() {
        Assertions.assertEquals("fabric-installer-manifest.xml", DEFAULTFILES.getMANIFEST_FABRIC_INSTALLER().toString());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForConfigTestOld() throws IOException {
        File oldConfigFile = new File("creator.conf");
        oldConfigFile.createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForConfig());
        Assertions.assertTrue(new File("serverpackcreator.conf").exists());
        new File("serverpackcreator.conf").delete();
        new File("creator.conf").delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForConfigTest() throws IOException {
        File configFile = new File("serverpackcreator.conf");
        configFile.createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForConfig());
        configFile.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForConfigTestNew() {
        File configFile = new File("./serverpackcreator.conf");
        configFile.delete();
        Assertions.assertTrue(DEFAULTFILES.checkForConfig());
        configFile.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForFabricLinuxTest() throws IOException {
        FileUtils.createParentDirectories(new File(String.format("server_files/%s", DEFAULTFILES.getFabricLinuxFile().toString())));
        new File(String.format("server_files/%s", DEFAULTFILES.getFabricLinuxFile())).createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForFile(DEFAULTFILES.getFabricLinuxFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getFabricLinuxFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForFabricLinuxTestNew() {
        new File(String.format("./server_files/%s", DEFAULTFILES.getFabricLinuxFile())).delete();
        Assertions.assertTrue(DEFAULTFILES.checkForFile(DEFAULTFILES.getFabricLinuxFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getFabricLinuxFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForFabricWindowsTest() throws IOException {
        FileUtils.createParentDirectories(new File(String.format("./server_files/%s", DEFAULTFILES.getFabricWindowsFile().toString())));
        new File(String.format("server_files/%s", DEFAULTFILES.getFabricWindowsFile())).createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForFile(DEFAULTFILES.getFabricWindowsFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getFabricWindowsFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForFabricWindowsTestNew() {
        new File(String.format("./server_files/%s", DEFAULTFILES.getFabricWindowsFile())).delete();
        Assertions.assertTrue(DEFAULTFILES.checkForFile(DEFAULTFILES.getFabricWindowsFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getFabricWindowsFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForForgeLinuxTest() throws IOException {
        FileUtils.createParentDirectories(new File(String.format("./server_files/%s", DEFAULTFILES.getForgeLinuxFile().toString())));
        new File(String.format("server_files/%s", DEFAULTFILES.getForgeLinuxFile())).createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForFile(DEFAULTFILES.getForgeLinuxFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getForgeLinuxFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForForgeLinuxTestNew() {
        new File(String.format("./server_files/%s", DEFAULTFILES.getForgeLinuxFile().toString())).delete();
        Assertions.assertTrue(DEFAULTFILES.checkForFile(DEFAULTFILES.getForgeLinuxFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getForgeLinuxFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForForgeWindowsTest() throws IOException {
        FileUtils.createParentDirectories(new File(String.format("./server_files/%s", DEFAULTFILES.getForgeWindowsFile().toString())));
        new File(String.format("server_files/%s", DEFAULTFILES.getForgeWindowsFile())).createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForFile(DEFAULTFILES.getForgeWindowsFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getForgeWindowsFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForForgeWindowsTestNew() {
        new File(String.format("./server_files/%s", DEFAULTFILES.getForgeWindowsFile())).delete();
        Assertions.assertTrue(DEFAULTFILES.checkForFile(DEFAULTFILES.getForgeWindowsFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getForgeWindowsFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForPropertiesTest() throws IOException {
        FileUtils.createParentDirectories(new File(String.format("./server_files/%s", DEFAULTFILES.getPropertiesFile().toString())));
        new File(String.format("server_files/%s", DEFAULTFILES.getPropertiesFile())).createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForFile(DEFAULTFILES.getPropertiesFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getPropertiesFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForPropertiesTestNew() {
        new File(String.format("./server_files/%s", DEFAULTFILES.getPropertiesFile())).delete();
        Assertions.assertTrue(DEFAULTFILES.checkForFile(DEFAULTFILES.getPropertiesFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getPropertiesFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForIconTest() throws IOException {
        FileUtils.createParentDirectories(new File(String.format("./server_files/%s", DEFAULTFILES.getIconFile().toString())));
        new File(String.format("server_files/%s", DEFAULTFILES.getIconFile())).createNewFile();
        Assertions.assertFalse(DEFAULTFILES.checkForFile(DEFAULTFILES.getIconFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getIconFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void checkForIconTestNew() {
        new File(String.format("./server_files/%s", DEFAULTFILES.getIconFile())).delete();
        Assertions.assertTrue(DEFAULTFILES.checkForFile(DEFAULTFILES.getIconFile()));
        new File(String.format("./server_files/%s", DEFAULTFILES.getIconFile())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void filesSetupTest() throws IOException {
        DEFAULTFILES.filesSetup();
        Assertions.assertTrue(new File("./server_files").isDirectory());
        Assertions.assertTrue(new File("./work").isDirectory());
        Assertions.assertTrue(new File("./work/temp").isDirectory());
        Assertions.assertTrue(new File("./server-packs").isDirectory());
        Assertions.assertTrue(new File("./server_files/server.properties").exists());
        Assertions.assertTrue(new File("./server_files/server-icon.png").exists());
        Assertions.assertTrue(new File("./server_files/start-fabric.bat").exists());
        Assertions.assertTrue(new File("./server_files/start-fabric.sh").exists());
        Assertions.assertTrue(new File("./server_files/start-forge.bat").exists());
        Assertions.assertTrue(new File("./server_files/start-forge.sh").exists());
        Assertions.assertTrue(new File("./serverpackcreator.conf").exists());
        Assertions.assertTrue(new File("./work/minecraft-manifest.json").exists());
        Assertions.assertTrue(new File("./work/fabric-manifest.xml").exists());
        Assertions.assertTrue(new File("./work/forge-manifest.json").exists());
        String delete = "./server_files";
        if (new File(delete).isDirectory()) {
            Path pathToBeDeleted = Paths.get(delete);
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        String deleteWork = "./work";
        if (new File(deleteWork).isDirectory()) {
            Path pathToBeDeleted = Paths.get(deleteWork);
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        String deleteServerPacks = "./server-packs";
        if (new File(deleteServerPacks).isDirectory()) {
            Path pathToBeDeleted = Paths.get(deleteServerPacks);
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        new File("./serverpackcreator.conf").delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void downloadMinecraftManifestTest() throws IOException {
        //Files.createDirectories(Paths.get("./work"));
        FileUtils.createParentDirectories(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_MINECRAFT())));
        DEFAULTFILES.refreshManifestFile(DEFAULTFILES.getMinecraftManifestUrl(), DEFAULTFILES.getMANIFEST_MINECRAFT());
        Assertions.assertTrue(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_MINECRAFT())).exists());
        new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_MINECRAFT())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void downloadFabricManifestTest() throws IOException {
        //Files.createDirectories(Paths.get("./work"));
        FileUtils.createParentDirectories(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FABRIC())));
        DEFAULTFILES.refreshManifestFile(DEFAULTFILES.getFabricManifestUrl(), DEFAULTFILES.getMANIFEST_FABRIC());
        Assertions.assertTrue(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FABRIC())).exists());
        new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FABRIC())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void downloadForgeManifestTest() throws IOException {
        //Files.createDirectories(Paths.get("./work"));
        FileUtils.createParentDirectories(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FORGE())));
        DEFAULTFILES.refreshManifestFile(DEFAULTFILES.getForgeManifestUrl(), DEFAULTFILES.getMANIFEST_FORGE());
        Assertions.assertTrue(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FORGE())).exists());
        new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FORGE())).delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void downloadFabricInstallerManifestTest() throws IOException {
        //Files.createDirectories(Paths.get("./work"));
        FileUtils.createParentDirectories(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FABRIC_INSTALLER())));
        DEFAULTFILES.refreshManifestFile(DEFAULTFILES.getFabricInstallerManifestUrl(), DEFAULTFILES.getMANIFEST_FABRIC_INSTALLER());
        Assertions.assertTrue(new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FABRIC_INSTALLER())).exists());
        new File(String.format("./work/%s", DEFAULTFILES.getMANIFEST_FABRIC_INSTALLER())).delete();
    }
}