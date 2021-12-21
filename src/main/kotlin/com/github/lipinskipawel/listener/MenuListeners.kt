package com.github.lipinskipawel.listener

import com.github.lipinskipawel.HeapDumper
import com.github.lipinskipawel.OptionsMenu
import com.github.lipinskipawel.ThreadDumper
import com.github.lipinskipawel.gui.Table
import com.github.lipinskipawel.network.ConnectionManager.Companion.getInetAddress
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.net.InetAddress
import java.net.UnknownHostException
import javax.swing.JOptionPane

/**
 * This file contains all the listeners that are registered to the top Menu bar. Each JMenu item has its own class that
 * will receive an ActionEvent. Currently, this file defines:
 * - [PlayListener]
 * - [PlayLanListener]
 * - [OptionListener]
 * as classes that are capable of dispatch particular ActionEvent.
 */

class PlayListener(private val table: Table,
                   private val actionGameController: PitchListener
) : ActionListener {

    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            table.playMenu.getWarmupItem() -> warmup()
            table.playMenu.get1vs1Item() -> oneVsOne()
            table.playMenu.getHellModeItem() -> hellMode()
            table.playMenu.getAiItem() -> oneVsAi()
        }
    }

    private fun warmup() {
        table.setWarmUp()
        actionGameController.setGameMode("warm-up")
    }

    private fun oneVsOne() {
        table.setOneVsOne()
        actionGameController.setGameMode("1vs1")
    }

    private fun hellMode() {
        table.setHellMode()
        actionGameController.setGameMode("hell mode")
    }

    private fun oneVsAi() {
        table.setOneVsAI()
        actionGameController.setGameMode("1vsAI")
    }
}

/**
 * This class is responsible for setting a connection over the LAN with the opponent. It knows about more than just top
 * menu bar items. It must also understand whether the user clicked on [connectionButton] in order to connect to a game.
 */
class PlayLanListener(private val table: Table,
                      private val actionGameController: PitchListener
) : ActionListener {

    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            table.playMenu.getLanItem() -> lanItem()
            table.connectButton -> connectionButton()
        }
    }

    private fun lanItem() {
        val waitingToConnect = JOptionPane.showConfirmDialog(
                null, "Do you want to wait to connection?")

        if (waitingToConnect == JOptionPane.YES_OPTION) {
            table.setButtonEnabled(false)
            actionGameController.setGameMode("1vsLAN-server")
        } else if (waitingToConnect == JOptionPane.NO_OPTION) {
            table.setButtonEnabled(true)
            actionGameController.setGameMode("1vsLAN-client")
        }
        table.setOneVsLAN(getInetAddress().hostAddress)
    }

    private fun connectionButton() {
        try {
            table.setButtonEnabled(false)
            val address = InetAddress.getByName(table.IPEnemy())
            actionGameController.connectTo(address)
        } catch (unknownHostException: UnknownHostException) {
            JOptionPane.showMessageDialog(null, "You have written wrong ip address!")
            unknownHostException.printStackTrace()
        }
    }
}

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
