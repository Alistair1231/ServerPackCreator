package de.griefed.serverpackcreator.gui.window.components

import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.event.DocumentListener

class ScrollTextArea(
    text: String,
    private val textArea: JTextArea = JTextArea(text),
    verticalScrollbarVisibility: Int = VERTICAL_SCROLLBAR_AS_NEEDED,
    horizontalScrollbarVisibility: Int = HORIZONTAL_SCROLLBAR_NEVER
) : JScrollPane(verticalScrollbarVisibility, horizontalScrollbarVisibility) {
    init {
        textArea.lineWrap = true
        viewport.view = textArea
    }

    var text: String
        get() {return textArea.text}
        set(value) {textArea.text = value}

    fun addDocumentListener(listener: DocumentListener) {
        textArea.document.addDocumentListener(listener)
    }
}