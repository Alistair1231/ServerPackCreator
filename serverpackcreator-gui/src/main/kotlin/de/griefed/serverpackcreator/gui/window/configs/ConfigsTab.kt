/* Copyright (C) 2023  Griefed
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
package de.griefed.serverpackcreator.gui.window.configs

import Gui
import de.griefed.serverpackcreator.api.*
import de.griefed.serverpackcreator.api.utilities.common.Utilities
import de.griefed.serverpackcreator.api.versionmeta.VersionMeta
import de.griefed.serverpackcreator.gui.FileBrowser
import de.griefed.serverpackcreator.gui.GuiProps
import de.griefed.serverpackcreator.gui.components.TabPanel
import kotlinx.coroutines.*
import org.apache.logging.log4j.kotlin.cachedLoggerOf
import java.io.File
import javax.swing.JOptionPane

@OptIn(DelicateCoroutinesApi::class)
class ConfigsTab(
    private val guiProps: GuiProps,
    private val configurationHandler: ConfigurationHandler,
    private val apiProperties: ApiProperties,
    private val versionMeta: VersionMeta,
    private val utilities: Utilities,
    private val serverPackHandler: ServerPackHandler,
    private val apiPlugins: ApiPlugins,
) : TabPanel() {
    private val log = cachedLoggerOf(this.javaClass)
    private val fileBrowser = FileBrowser(this, guiProps, utilities)
    val selectedEditor: ConfigEditorPanel?
        get() {
            return if (activeTab != null) {
                activeTab as ConfigEditorPanel
            } else {
                null
            }
        }

    init {
        tabs.addChangeListener {
            GlobalScope.launch(guiProps.configDispatcher) {
                if (tabs.tabCount != 0) {
                    for (tab in 0 until tabs.tabCount) {
                        (tabs.getComponentAt(tab) as ConfigEditorPanel).title.closeButton.isVisible = false
                    }
                    (activeTab!! as ConfigEditorPanel).title.closeButton.isVisible = true
                }
            }

        }
        for (i in 0..5) {
            addTab()
        }
        tabs.selectedIndex = 0
        (activeTab!! as ConfigEditorPanel).title.closeButton.isVisible = true
    }

    fun addTab(): ConfigEditorPanel {
        val editor = ConfigEditorPanel(
            guiProps, this,
            configurationHandler,
            apiProperties,
            versionMeta,
            utilities,
            serverPackHandler,
            apiPlugins
        ) { fileBrowser.show() }
        tabs.add(editor)
        tabs.setTabComponentAt(tabs.tabCount - 1, editor.title)
        tabs.selectedIndex = tabs.tabCount - 1
        return editor
    }

    /**
     * When the GUI has finished loading, try and load the existing serverpackcreator.conf-file into
     * ServerPackCreator.
     *
     * @param configFile The configuration file to parse and load into the GUI.
     * @author Griefed
     */
    fun loadConfig(configFile: File, tab: ConfigEditorPanel = addTab()) {
        GlobalScope.launch(guiProps.configDispatcher) {
            try {
                val packConfig = PackConfig(utilities, configFile)
                tab.lastLoadedConfiguration = packConfig
                tab.setModpackDirectory(packConfig.modpackDir)
                if (packConfig.clientMods.isEmpty()) {
                    tab.setClientSideMods(apiProperties.clientSideMods())
                    log.debug("Set clientMods with fallback list.")
                } else {
                    tab.setClientSideMods(packConfig.clientMods)
                }
                if (packConfig.copyDirs.isEmpty()) {
                    tab.setCopyDirectories(mutableListOf("mods", "config"))
                } else {
                    tab.setCopyDirectories(packConfig.copyDirs)
                }
                tab.setScriptVariables(packConfig.scriptSettings)
                tab.setServerIconPath(packConfig.serverIconPath)
                tab.setServerPropertiesPath(packConfig.serverPropertiesPath)
                if (packConfig.minecraftVersion.isEmpty()) {
                    packConfig.minecraftVersion = versionMeta.minecraft.latestRelease().version
                }
                tab.setMinecraftVersion(packConfig.minecraftVersion)
                tab.setModloader(packConfig.modloader)
                tab.setModloaderVersion(packConfig.modloaderVersion)
                tab.setServerInstallationTicked(packConfig.isServerInstallationDesired)
                tab.setIconInclusionTicked(packConfig.isServerIconInclusionDesired)
                tab.setPropertiesInclusionTicked(packConfig.isServerPropertiesInclusionDesired)
                tab.setZipArchiveCreationTicked(packConfig.isZipCreationDesired)
                tab.setJavaArguments(packConfig.javaArgs)
                tab.setServerPackSuffix(packConfig.serverPackSuffix)
                for (panel in tab.pluginPanels) {
                    panel.setServerPackExtensionConfig(packConfig.getPluginConfigs(panel.pluginID))
                }
            } catch (ex: Exception) {
                log.error("Couldn't load configuration file.", ex)
                JOptionPane.showMessageDialog(
                    panel,
                    Gui.createserverpack_gui_config_load_error_message.toString() + " " + ex.cause + "   ",
                    Gui.createserverpack_gui_config_load_error.toString(),
                    JOptionPane.ERROR_MESSAGE,
                    guiProps.errorIcon
                )
            }
        }
    }
}