package com.github.lipinskipawel

import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem

class OptionsMenu : JMenu("Options") {
    private val heapDump: JMenuItem = JMenuItem("Heap Dump")
    private val threadDump: JMenuItem = JMenuItem("Thread Dump")

    init {
        add(this.heapDump)
        add(this.threadDump)
    }

    fun addActionClassForAllMenuItems(actionListener: ActionListener) {
        this.heapDump.addActionListener(actionListener)
        this.threadDump.addActionListener(actionListener)
    }

    fun getHeapDumpItem(): JMenuItem = this.heapDump
    fun getThreadDumpItem(): JMenuItem = this.threadDump
}
