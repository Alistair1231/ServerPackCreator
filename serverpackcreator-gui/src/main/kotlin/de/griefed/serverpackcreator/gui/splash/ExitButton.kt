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
 * full license can be found at https:github.com/Griefed/ServerPackCreator/blob/main/LICENSE
 */
package de.griefed.serverpackcreator.gui.splash

import de.griefed.serverpackcreator.gui.utilities.ImageUtilities
import javax.swing.JButton
import kotlin.system.exitProcess

/**
 * Display an exit button in the splash screen to quit SPC before it is fully started.
 *
 * @author Griefed
 */
class ExitButton(width: Int) : JButton() {
    init {
        val size = 16
        @Suppress("SpellCheckingInspection")
        icon = ImageUtilities.fromBase64(
            "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",
            size, size
        )
        setBounds(width - size - 5, 5, size, size)
        isContentAreaFilled = false
        isOpaque = false
        border = null
        isBorderPainted = false
        addActionListener { exitProcess(0) }
    }
}