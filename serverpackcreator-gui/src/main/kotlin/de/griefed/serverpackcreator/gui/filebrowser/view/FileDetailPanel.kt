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
package de.griefed.serverpackcreator.gui.filebrowser.view

import Gui
import de.griefed.serverpackcreator.gui.filebrowser.model.FileBrowserModel
import de.griefed.serverpackcreator.gui.filebrowser.model.FileNode
import de.griefed.serverpackcreator.gui.window.configs.components.ElementLabel
import net.miginfocom.swing.MigLayout
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Panel containing details about the currently selected file in the table.
 *
 * @author Griefed (Kotlin Conversion and minor changes)
 * @author Andrew Thompson
 * @see <a href="https://codereview.stackexchange.com/questions/4446/file-browser-gui">File Browser GUI</a>
 * @license LGPL
 */
class FileDetailPanel : JPanel(
    MigLayout(
        "fillx",
        "10[150!]0[grow]20"
    )
) {
    private var fileNameLabel = JLabel(" ")
    private var lastModifiedLabel = JLabel(" ")
    private var fileSizeLabel = JLabel(" ")
    private var filePathField = JTextField()
    private var fileNameTextLabel = ElementLabel(" ")
    private val filePathTextLabel = ElementLabel(Gui.filebrowser_detail_path.toString())
    private val lastModifiedTextLabel = ElementLabel(Gui.filebrowser_detail_last.toString())
    private val fileSizeTextLabel = ElementLabel(Gui.filebrowser_detail_size.toString())
    var fileNode: FileNode? = null

    init {
        filePathField.isEditable = false
        add(fileNameTextLabel, "cell 0 0")
        add(fileNameLabel, "cell 1 0,grow")

        add(filePathTextLabel, "cell 0 1")
        add(filePathField, "cell 1 1,grow")

        add(lastModifiedTextLabel, "cell 0 2")
        add(lastModifiedLabel, "cell 1 2,grow")

        add(fileSizeTextLabel, "cell 0 3")
        add(fileSizeLabel, "cell 1 3,grow")
    }

    /**
     * Update details about the selected file.
     *
     * @author Griefed (Kotlin Conversion and minor changes)
     * @author Andrew Thompson
     */
    private fun updatePartControl(browserModel: FileBrowserModel) {
        if (fileNode != null) {
            val file: File = fileNode!!.file
            if (file.isDirectory) {
                fileNameTextLabel.text = Gui.filebrowser_detail_directory.toString()
            } else {
                fileNameTextLabel.text = Gui.filebrowser_detail_file.toString()
            }
            fileNameLabel.text = file.name
            fileNameLabel.icon = browserModel.getFileIcon(file)
            filePathField.text = file.absolutePath
            filePathField.caretPosition = 0
            lastModifiedLabel.text = generateLastModified(file)
            fileSizeLabel.text = generateFileSize(file)
        }
    }

    /**
     * Get the date and time of when the file was last modified,
     *
     * @author Griefed (Kotlin Conversion and minor changes)
     * @author Andrew Thompson
     */
    private fun generateLastModified(file: File): String {
        val timestamp = file.lastModified()
        val date = Date(timestamp)
        val pattern = "EEE, d MMM yyyy zzzz  h:mm:ss aa"
        val sdf = SimpleDateFormat(pattern)
        return sdf.format(date)
    }

    /**
     * Get the filesize of the selected file and, depending on the size, display it with `bytes`, `KB`, `GB`, `TB`.
     *
     * @author Griefed (Kotlin Conversion and minor changes)
     * @author Andrew Thompson
     */
    private fun generateFileSize(file: File): String {
        val label = arrayOf("bytes", "KB", "MB", "GB", "TB")
        var bytes = file.length().toDouble()
        var count = 0
        while (bytes > 1000.0) {
            bytes /= 1024.0
            count++
        }
        return String.format("%.3f ", bytes) + label[count]
    }

    /**
     * Update the file-node.
     *
     * @author Griefed (Kotlin Conversion and minor changes)
     * @author Andrew Thompson
     */
    fun setFileNode(fileNode: FileNode?, browserModel: FileBrowserModel) {
        this.fileNode = fileNode
        updatePartControl(browserModel)
    }
}