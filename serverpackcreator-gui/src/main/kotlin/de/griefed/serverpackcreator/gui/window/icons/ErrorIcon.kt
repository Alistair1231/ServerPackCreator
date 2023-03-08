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
package de.griefed.serverpackcreator.gui.window.icons

import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import kotlin.math.roundToInt

/**
 * TODO docs
 */
class ErrorIcon(private val zoom: Float = 0.7f) : FlatOptionPaneErrorIcon() {
    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g.create() as Graphics2D
        try {
            g2.translate(x, y)
            g2.scale(zoom.toDouble(), zoom.toDouble())
            super.paintIcon(c, g2, 0, 0)
        } finally {
            g2.dispose()
        }
    }

    override fun getIconWidth(): Int {
        return (super.getIconWidth() * zoom).roundToInt()
    }

    override fun getIconHeight(): Int {
        return (super.getIconHeight() * zoom).roundToInt()
    }
}