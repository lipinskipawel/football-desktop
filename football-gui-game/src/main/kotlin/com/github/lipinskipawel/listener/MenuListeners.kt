package com.github.lipinskipawel.listener

import com.github.lipinskipawel.HeapDumper
import com.github.lipinskipawel.OptionsMenu
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class OptionListener(private val optionMenu: OptionsMenu) : ActionListener {
    private val heapDumper: HeapDumper = HeapDumper()

    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            optionMenu.getHeapDumpItem() -> this.saveHeapDumps()
        }
    }

    private fun saveHeapDumps() {
        try {
            heapDumper.dumpHeap("default.hprof")
        } catch (ex: Exception) {
            println("log exception to file and inform user to contact administrator")
        }
    }
}
