package de.griefed.serverpackcreator.gui.filebrowser.controller.action

import Gui
import de.griefed.serverpackcreator.api.utilities.common.Utilities
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.AbstractAction

class OpenAction(private val utilities: Utilities) : AbstractAction() {
    private var file: File? = null

    init {
        putValue(NAME, Gui.filebrowser_action_open.toString())
    }

    override fun actionPerformed(e: ActionEvent) {
        if (file != null) {
            utilities.fileUtilities.openFile(file!!)
        }
    }

    fun setFile(file: File?) {
        this.file = file
    }
}