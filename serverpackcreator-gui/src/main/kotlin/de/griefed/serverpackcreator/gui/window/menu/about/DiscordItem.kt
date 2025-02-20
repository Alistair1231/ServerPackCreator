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
package de.griefed.serverpackcreator.gui.window.menu.about

import Gui
import de.griefed.serverpackcreator.api.utilities.common.WebUtilities
import java.net.URI
import javax.swing.JMenuItem

/**
 * Menu item in the about menu which opens the Discord-invitation to Griefed's Discord server in the users' browser.
 *
 * @author Griefed
 */
class DiscordItem(private val webUtilities: WebUtilities) : JMenuItem(Gui.menubar_gui_menuitem_discord.toString()) {
    private val discord = URI.create("https://discord.griefed.de")

    init {
        this.addActionListener { openDiscord() }
    }

    /**
     * @author Griefed
     */
    private fun openDiscord() {
        webUtilities.openLinkInBrowser(discord)
    }
}