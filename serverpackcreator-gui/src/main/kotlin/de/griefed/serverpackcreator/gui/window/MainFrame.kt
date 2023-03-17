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
package de.griefed.serverpackcreator.gui.window

import Gui
import de.griefed.larsonscanner.LarsonScanner
import de.griefed.serverpackcreator.api.ApiWrapper
import de.griefed.serverpackcreator.gui.GuiProps
import de.griefed.serverpackcreator.gui.splash.SplashScreen
import de.griefed.serverpackcreator.gui.window.menu.MainMenuBar
import de.griefed.serverpackcreator.updater.MigrationManager
import de.griefed.serverpackcreator.updater.UpdateChecker
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * TODO docs
 */
class MainFrame(
    apiWrapper: ApiWrapper,
    updateChecker: UpdateChecker,
    splashScreen: SplashScreen,
    migrationManager: MigrationManager
) {
    private val guiProps = GuiProps()
    val frame: JFrame = JFrame(Gui.createserverpack_gui_createandshowgui.toString())
    private val updateDialogs =
        UpdateDialogs(guiProps, apiWrapper.utilities!!.webUtilities, apiWrapper.apiProperties, updateChecker, frame)
    private val larsonScanner = LarsonScanner()
    val mainPanel = MainPanel(
        guiProps,
        apiWrapper,
        larsonScanner
    )
    private val mainMenuBar = MainMenuBar(
        guiProps,
        apiWrapper,
        updateDialogs,
        larsonScanner,
        this,
        migrationManager
    )

    init {
        frame.iconImage = guiProps.appIcon
        frame.jMenuBar = mainMenuBar.menuBar
        frame.contentPane.add(mainPanel.panel)
        frame.pack()
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.isLocationByPlatform = true
        frame.setSize(1200, 800)
        frame.isVisible = true
        frame.isAutoRequestFocus = true
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                mainPanel.closeAndExit()
            }
        })
        splashScreen.close()
    }
}