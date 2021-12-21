package com.github.lipinskipawel

import com.github.lipinskipawel.gui.Table
import com.github.lipinskipawel.listener.OptionListener
import com.github.lipinskipawel.listener.PitchListener
import com.github.lipinskipawel.listener.PlayLanListener
import com.github.lipinskipawel.listener.PlayListener

fun main(args: Array<String>) {
    val table = Table()

    val actionGameController = PitchListener(table.drawableFootballPitch)
    actionGameController.setGameMode("warm-up")

    table.addActionClassForPlayMenu(PlayListener(table, actionGameController))

    val playLanListener = PlayLanListener(table, actionGameController)
    table.addActionClassForPlayMenu(playLanListener)
    table.addConnectListener(playLanListener)

    table.addActionClassForOptionMenu(OptionListener(table.optionsMenu))

    table.addMouseClassToGameDrawer(actionGameController)
}
