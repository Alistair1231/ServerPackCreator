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
import de.griefed.serverpackcreator.api.ApiWrapper
import de.griefed.serverpackcreator.api.PackConfig
import de.griefed.serverpackcreator.api.utilities.common.InvalidFileTypeException
import de.griefed.serverpackcreator.gui.GuiProps
import de.griefed.serverpackcreator.gui.components.TabPanel
import de.griefed.serverpackcreator.gui.utilities.DialogUtilities
import de.griefed.serverpackcreator.gui.window.MainFrame
import de.griefed.serverpackcreator.gui.window.configs.components.ComponentResizer
import de.griefed.serverpackcreator.gui.window.menu.file.ConfigChooser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.monitor.FileAlterationListener
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.apache.logging.log4j.kotlin.cachedLoggerOf
import java.io.File
import java.util.concurrent.Executors
import javax.swing.DefaultComboBoxModel
import javax.swing.JFileChooser
import javax.swing.JOptionPane

/**
 * Tabbed pane housing every server pack config tab.
 *
 * @author Griefed
 */
@OptIn(DelicateCoroutinesApi::class)
class TabbedConfigsTab(
    private val guiProps: GuiProps,
    private val apiWrapper: ApiWrapper,
    private val mainFrame: MainFrame
) : TabPanel() {
    private val log = cachedLoggerOf(this.javaClass)
    private val choose = arrayOf(Gui.createserverpack_gui_quickselect_choose.toString())
    private val noVersions = DefaultComboBoxModel(
        arrayOf(Gui.createserverpack_gui_createserverpack_forge_none.toString())
    )
    private val componentResizer = ComponentResizer()
    val selectedEditor: ConfigEditor?
        get() {
            return if (activeTab != null) {
                activeTab as ConfigEditor
            } else {
                null
            }
        }

    init {
        iconsDirectoryWatcher()
        propertiesDirectoryWatcher()
        tabs.addChangeListener {
            GlobalScope.launch(guiProps.configDispatcher) {
                if (tabs.tabCount != 0) {
                    for (tab in 0 until tabs.tabCount) {
                        (tabs.getComponentAt(tab) as ConfigEditor).editorTitle.closeButton.isVisible = false
                    }
                    if (activeTab != null) {
                        (activeTab as ConfigEditor).editorTitle.closeButton.isVisible = true
                    }
                }
            }

        }

        val lastLoadedConfigs = guiProps.getGuiProperty("lastloaded")
        if (!lastLoadedConfigs.isNullOrBlank()) {
            val configs = if (lastLoadedConfigs.contains(",")) {
                lastLoadedConfigs.split(",").map { File(it) }
            } else {
                listOf(File(lastLoadedConfigs))
            }
            for (configFile in configs) {
                loadConfig(configFile)
            }
        } else {
            addTab()
        }

        tabs.selectedIndex = 0
        (activeTab!! as ConfigEditor).editorTitle.closeButton.isVisible = true
    }

    /**
     * @author Griefed
     */
    fun addTab(): ConfigEditor {
        val editor = ConfigEditor(
            guiProps,
            this,
            apiWrapper,
            noVersions,
            componentResizer
        )
        tabs.add(editor)
        tabs.setTabComponentAt(tabs.tabCount - 1, editor.editorTitle)
        tabs.selectedIndex = tabs.tabCount - 1
        return editor
    }

    fun saveAll() {
        for (tab in allTabs) {
            (tab as ConfigEditor).saveCurrentConfiguration()
        }
    }

    /**
     * When the GUI has finished loading, try and load the existing serverpackcreator.conf-file into
     * ServerPackCreator.
     *
     * @param configFile The configuration file to parse and load into the GUI.
     *
     * @author Griefed
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun loadConfig(configFile: File, tab: ConfigEditor = addTab()) {
        tab.loadConfiguration(PackConfig(apiWrapper.utilities!!, configFile), configFile)
    }

    /**
     * @author Griefed
     */
    fun loadConfigFile() {
        val configChooser = ConfigChooser(apiWrapper.apiProperties, Gui.createserverpack_gui_buttonloadconfig_title.toString())
        configChooser.isMultiSelectionEnabled = true
        if (configChooser.showOpenDialog(mainFrame.frame) == JFileChooser.APPROVE_OPTION) {
            val files = configChooser.selectedFiles.map { file ->
                try {
                    File(apiWrapper.fileUtilities.resolveLink(file)).absoluteFile
                } catch (ex: InvalidFileTypeException) {
                    log.error("Could not resolve link/symlink. Using entry from user input for checks.", ex)
                    file.absoluteFile
                }
            }
            for (file in files) {
                if (tabs.tabCount > 0 &&
                    DialogUtilities.createShowGet(
                        Gui.menubar_gui_config_load_message(file.absolutePath),
                        Gui.menubar_gui_config_load_title.toString(),
                        mainFrame.frame,
                        JOptionPane.QUESTION_MESSAGE,
                        JOptionPane.YES_NO_OPTION,
                        guiProps.warningIcon,
                        false,
                        arrayOf(Gui.menubar_gui_config_load_current, Gui.menubar_gui_config_load_new)
                    ) == 0
                ) {
                    loadConfig(file, selectedEditor!!)
                } else {
                    loadConfig(file)
                }
            }
        }
    }

    /**
     * @author Griefed
     */
    @Suppress("DuplicatedCode")
    private fun iconsDirectoryWatcher() {
        Executors.newSingleThreadExecutor().execute {
            val observer = FileAlterationObserver(apiWrapper.apiProperties.iconsDirectory)
            val alterations = object : FileAlterationListener {
                override fun onStart(observer: FileAlterationObserver?) {}
                override fun onDirectoryCreate(directory: File?) {}
                override fun onDirectoryChange(directory: File?) {}
                override fun onDirectoryDelete(directory: File?) {}
                override fun onStop(observer: FileAlterationObserver?) {}

                override fun onFileCreate(file: File?) {
                    update()
                }

                override fun onFileChange(file: File?) {
                    update()
                }

                override fun onFileDelete(file: File?) {
                    update()
                }

                private fun update() {
                    if (tabs.tabCount == 0) {
                        return
                    }
                    for (tab in allTabs) {
                        val configTab = tab as ConfigEditor
                        val model = DefaultComboBoxModel(choose)
                        model.addAll(iconQuickSelections())
                        configTab.iconQuickSelect.model = model
                    }
                }
            }
            observer.addListener(alterations)
            val monitor = FileAlterationMonitor(2000)
            monitor.addObserver(observer)
            try {
                monitor.start()
            } catch (ex: Exception) {
                log.error("Error starting the FileWatcher Monitor.", ex)
            }
        }
    }

    /**
     * @author Griefed
     */
    @Suppress("DuplicatedCode")
    private fun propertiesDirectoryWatcher() {
        Executors.newSingleThreadExecutor().execute {
            val observer = FileAlterationObserver(apiWrapper.apiProperties.propertiesDirectory)
            val alterations = object : FileAlterationListener {
                override fun onStart(observer: FileAlterationObserver?) {}
                override fun onDirectoryCreate(directory: File?) {}
                override fun onDirectoryChange(directory: File?) {}
                override fun onDirectoryDelete(directory: File?) {}
                override fun onStop(observer: FileAlterationObserver?) {}

                override fun onFileCreate(file: File?) {
                    update()
                }

                override fun onFileChange(file: File?) {
                    update()
                }

                override fun onFileDelete(file: File?) {
                    update()
                }

                private fun update() {
                    if (tabs.tabCount == 0) {
                        return
                    }
                    for (tab in allTabs) {
                        val configTab = tab as ConfigEditor
                        val model = DefaultComboBoxModel(choose)
                        model.addAll(propertiesQuickSelections())
                        configTab.propertiesQuickSelect.model = model
                    }
                }
            }
            observer.addListener(alterations)
            val monitor = FileAlterationMonitor(2000)
            monitor.addObserver(observer)
            try {
                monitor.start()
            } catch (ex: Exception) {
                log.error("Error starting the FileWatcher Monitor.", ex)
            }
        }
    }

    /**
     * List of server icons for quick selection in a given config tab.
     *
     * @author Griefed
     */
    fun iconQuickSelections(): List<String> {
        return getNames(apiWrapper.apiProperties.iconsDirectory, guiProps.imageRegex)
    }

    /**
     * Acquire all files-names for the passed [directory], filtered using the passed [matcher].
     *
     * @author Griefed
     */
    private fun getNames(directory: File, matcher: Regex): List<String> {
        val files = directory.listFiles()!!
        val filtered = files.filter { file -> file.name.matches(matcher) }
        return filtered.map { file -> file.name }
    }

    /**
     * List of server properties for quick selection in a given config tab.
     *
     * @author Griefed
     */
    fun propertiesQuickSelections(): List<String> {
        return getNames(apiWrapper.apiProperties.propertiesDirectory, guiProps.propertiesRegex)
    }
}