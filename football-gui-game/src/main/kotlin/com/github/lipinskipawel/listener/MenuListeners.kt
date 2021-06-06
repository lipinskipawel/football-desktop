package com.github.lipinskipawel.listener

import com.github.lipinskipawel.HeapDumper
import com.github.lipinskipawel.OptionsMenu
import com.github.lipinskipawel.ThreadDumper
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JOptionPane

class OptionListener(private val optionMenu: OptionsMenu) : ActionListener {
    private val heapDumper: HeapDumper = HeapDumper()
    private val threadDumper: ThreadDumper = ThreadDumper()

    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            optionMenu.getHeapDumpItem() -> this.saveHeapDumps()
            optionMenu.getThreadDumpItem() -> this.saveThreadDumps()
        }
    }

    private fun saveHeapDumps() {
        val filename = "default-heap-dump.hprof"
        val isHeapDumpCreated = heapDumper.dumpHeap(filename)
        if (isHeapDumpCreated) {
            JOptionPane.showMessageDialog(null, "Heap dump was created at: $filename")
        } else {
            JOptionPane.showMessageDialog(null, "Error when creating heap dump.")
        }
    }

    private fun saveThreadDumps() {
        val filename = "default-thread-dump.hprof"
        threadDumper.dumpThreads(filename)
        JOptionPane.showMessageDialog(null, "Thread dump was created at: $filename")
    }
}
