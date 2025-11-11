package com.github.lipinskipawel

import com.github.lipinskipawel.gui.DrawableFacade
import com.github.lipinskipawel.gui.GameDrawer
import com.github.lipinskipawel.gui.GamePanel
import com.github.lipinskipawel.gui.Table
import com.github.lipinskipawel.listener.OptionListener
import com.github.lipinskipawel.listener.PitchListener
import com.github.lipinskipawel.listener.PlayLanListener
import com.github.lipinskipawel.listener.PlayListener
import javax.swing.JMenuBar

fun main(args: Array<String>) {
    val tableMenuBar = JMenuBar()
    val playMenu = PlayMenu()
    val optionsMenu = OptionsMenu()
    tableMenuBar.add(playMenu)
    tableMenuBar.add(optionsMenu)

    val gamePanel = GamePanel()
    gamePanel.setWarmUP()
    val gameDrawer = GameDrawer()
    val table = Table(tableMenuBar, gamePanel, gameDrawer)

    val actionGameController = PitchListener(DrawableFacade(gameDrawer, gamePanel))
    actionGameController.setGameMode("warm-up")
    gameDrawer.addMouse(actionGameController)

    val playListener = PlayListener(playMenu, gamePanel, actionGameController)
    playMenu.addActionClassForAllMenuItems(playListener)

    val playLanListener = PlayLanListener(playMenu, gamePanel, actionGameController)
    playMenu.addActionClassForAllMenuItems(playLanListener)
    gamePanel.addButtonConnectListener(playLanListener)

    val optionListener = OptionListener(optionsMenu, gamePanel, actionGameController)
    optionsMenu.addActionClassForAllMenuItems(optionListener)

    table.show()
}
