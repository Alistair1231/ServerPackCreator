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
package de.griefed.serverpackcreator.gui.window.control

import Gui
import de.griefed.serverpackcreator.api.ApiWrapper
import de.griefed.serverpackcreator.api.PackConfig
import de.griefed.serverpackcreator.gui.GuiProps
import de.griefed.serverpackcreator.gui.window.configs.TabbedConfigsTab
import de.griefed.serverpackcreator.gui.window.control.components.GenerationButton
import de.griefed.serverpackcreator.gui.window.control.components.LarsonScanner
import de.griefed.serverpackcreator.gui.window.control.components.ServerPacksButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.miginfocom.swing.MigLayout
import org.apache.logging.log4j.kotlin.cachedLoggerOf
import java.awt.Desktop
import java.io.File
import java.io.IOException
import javax.swing.JOptionPane
import javax.swing.JPanel

/**
 * TODO docs
 */
class ControlPanel(
    private val guiProps: GuiProps,
    private val tabbedConfigsTab: TabbedConfigsTab,
    private val larsonScanner: LarsonScanner,
    private val apiWrapper: ApiWrapper
) {
    private val log = cachedLoggerOf(this.javaClass)
    private val statusPanel = StatusPanel(guiProps)

    @OptIn(DelicateCoroutinesApi::class)
    private val runGeneration = GenerationButton(guiProps) {
        GlobalScope.launch(guiProps.generationDispatcher) {
            launchGeneration()
            readyForGeneration()
        }
    }
    private val serverPacks = ServerPacksButton(guiProps) {
        apiWrapper.utilities!!.fileUtilities.openFolder(apiWrapper.apiProperties.serverPacksDirectory)
    }
    val panel = JPanel()

    init {
        panel.layout = MigLayout(
            "",
            "0[200!]0[grow]0",
            "0[75!,bottom]10[75!,top]0"
        )
        panel.add(runGeneration, "cell 0 0 1 1,grow,height 50!,width 150!,align center")
        panel.add(serverPacks, "cell 0 1 1 1,grow,height 50!,width 150!,align center")
        panel.add(statusPanel.panel, "cell 1 0 1 2,grow,push, h 160!")
    }

    /**
     * Upon button-press, check the entered configuration and if successfull, generate a server pack.
     *
     * @author Griefed
     */
    private fun launchGeneration() {
        runGeneration.isEnabled = false
        larsonScanner.loadConfig(guiProps.busyConfig)
        var decision = 0
        if (tabbedConfigsTab.selectedEditor == null) {
            JOptionPane.showMessageDialog(
                panel,
                Gui.createserverpack_log_error_configuration_none_message.toString(),
                Gui.createserverpack_log_error_configuration_none_title.toString(),
                JOptionPane.INFORMATION_MESSAGE, guiProps.largeInfoIcon
            )
            statusPanel.updateStatus(Gui.createserverpack_log_error_configuration_none_message.toString())
            return
        }
        if (tabbedConfigsTab.selectedEditor!!.getServerFiles().any { inclusion -> inclusion.source == "lazy_mode" }) {
            val message = Gui.configuration_log_warn_checkconfig_copydirs_lazymode0.toString() + "\n" +
                    Gui.configuration_log_warn_checkconfig_copydirs_lazymode1.toString() + "\n" +
                    Gui.configuration_log_warn_checkconfig_copydirs_lazymode2.toString() + "\n" +
                    Gui.configuration_log_warn_checkconfig_copydirs_lazymode3.toString()
            decision = JOptionPane.showConfirmDialog(
                panel,
                message,
                Gui.createserverpack_gui_createserverpack_lazymode.toString(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                guiProps.largeWarningIcon
            )
        }
        log.debug("Case $decision")
        when (decision) {
            0 -> runGenerationTasks()
        }
    }

    /**
     * Set the GUI ready for the next generation.
     *
     * @author Griefed
     */
    private fun readyForGeneration() {
        runGeneration.isEnabled = true
        larsonScanner.loadConfig(guiProps.idleConfig)
    }

    /**
     * Generate a server pack from the current configuration in the GUI.
     *
     * @author Griefed
     */
    private fun runGenerationTasks() {
        val activeTab = tabbedConfigsTab.selectedEditor!!
        val packConfig: PackConfig = activeTab.getCurrentConfiguration()
        val encounteredErrors: MutableList<String> = ArrayList(100)

        log.info("Checking entered configuration.")
        statusPanel.updateStatus(Gui.createserverpack_log_info_buttoncreateserverpack_start.toString())
        if (!activeTab.checkServer()) {
            packConfig.isServerInstallationDesired = false
        }

        if (!apiWrapper.configurationHandler!!.checkConfiguration(packConfig, encounteredErrors, true)) {
            log.info("Config check passed.")
            statusPanel.updateStatus(Gui.createserverpack_log_info_buttoncreateserverpack_checked.toString())
            generateServerPack(packConfig)
        } else {
            generationFailed(encounteredErrors)
        }
    }

    /**
     * TODO docs
     */
    private fun generateServerPack(packConfig: PackConfig) {
        log.info("Starting ServerPackCreator run.")
        statusPanel.updateStatus(Gui.createserverpack_log_info_buttoncreateserverpack_generating.toString())
        try {
            apiWrapper.serverPackHandler!!.run(packConfig)
            statusPanel.updateStatus(Gui.createserverpack_log_info_buttoncreateserverpack_ready.toString())
            if (JOptionPane.showConfirmDialog(
                    panel.parent,
                    Gui.createserverpack_gui_createserverpack_openfolder_browse.toString(),
                    Gui.createserverpack_gui_createserverpack_openfolder_title.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    guiProps.infoIcon
                ) == 0
            ) {
                try {
                    Desktop.getDesktop().open(File(apiWrapper.serverPackHandler!!.getServerPackDestination(packConfig)))

                } catch (ex: IOException) {
                    log.error("Error opening file explorer for server pack.", ex)
                }
            }
        } catch (ex: Exception) {
            log.error("An error occurred when generating the server pack.", ex)
        }
    }

    /**
     * TODO docs
     */
    private fun generationFailed(encounteredErrors: List<String>) {
        statusPanel.updateStatus(Gui.createserverpack_gui_buttongenerateserverpack_fail.toString())
        if (encounteredErrors.isNotEmpty()) {
            val errors = StringBuilder()
            for (i in encounteredErrors.indices) {
                errors.append(i + 1).append(": ").append(encounteredErrors[i]).append("    ").append("\n")
            }
            JOptionPane.showMessageDialog(
                panel.parent,
                errors,
                Gui.createserverpack_gui_createserverpack_errors_encountered(encounteredErrors.size),
                JOptionPane.ERROR_MESSAGE,
                guiProps.errorIcon
            )
        }
    }
}