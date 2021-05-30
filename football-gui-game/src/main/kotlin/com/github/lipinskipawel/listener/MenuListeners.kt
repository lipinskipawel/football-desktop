package com.github.lipinskipawel.listener

import com.github.lipinskipawel.HeapDumper
import com.github.lipinskipawel.OptionsMenu
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JOptionPane

class OptionListener(private val optionMenu: OptionsMenu) : ActionListener {
    private val heapDumper: HeapDumper = HeapDumper()

    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            optionMenu.getHeapDumpItem() -> this.saveHeapDumps()
        }
    }

    private fun saveHeapDumps() {
        val filename = "default.hprof"
        val isHeapDumpCreated = heapDumper.dumpHeap(filename)
        if (isHeapDumpCreated) {
            JOptionPane.showMessageDialog(null, "Heap dump was created at: $filename")
        } else {
            JOptionPane.showMessageDialog(null, "Error when creating heap dump.")
        }
    }
}
