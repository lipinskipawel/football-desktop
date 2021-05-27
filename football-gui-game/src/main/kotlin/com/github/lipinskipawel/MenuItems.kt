package com.github.lipinskipawel

import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem

class OptionsMenu : JMenu("Options") {
    private val heapDump: JMenuItem = JMenuItem("Heap Dump")

    init {
        add(this.heapDump)
    }

    fun addActionClassForAllMenuItems(actionListener: ActionListener) {
        this.heapDump.addActionListener(actionListener)
    }

    fun getHeapDumpItem(): JMenuItem = this.heapDump
}
