package com.github.lipinskipawel

import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem

class PlayMenu : JMenu("Play") {
    private val menuItemWarmup: JMenuItem = JMenuItem("Warm-up")
    private val menuItemOneVsOne: JMenuItem = JMenuItem("1 vs 1")
    private val menuItemHellMove: JMenuItem = JMenuItem("Hell mode")
    private val menuItemLAN: JMenuItem = JMenuItem("1 vs LAN")
    private val menuItemAI: JMenuItem = JMenuItem("1 vs AI")

    init {
        add(this.menuItemWarmup)
        add(this.menuItemOneVsOne)
        add(this.menuItemHellMove)
        add(this.menuItemLAN)
        add(this.menuItemAI)
    }

    fun addActionClassForAllMenuItems(actionListener: ActionListener) {
        this.menuItemWarmup.addActionListener(actionListener)
        this.menuItemOneVsOne.addActionListener(actionListener)
        this.menuItemHellMove.addActionListener(actionListener)
        this.menuItemLAN.addActionListener(actionListener)
        this.menuItemAI.addActionListener(actionListener)
    }

    fun getWarmupItem(): JMenuItem = this.menuItemWarmup
    fun get1vs1Item(): JMenuItem = this.menuItemOneVsOne
    fun getHellModeItem(): JMenuItem = this.menuItemHellMove
    fun getLanItem(): JMenuItem = this.menuItemLAN
    fun getAiItem(): JMenuItem = this.menuItemAI
}

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
