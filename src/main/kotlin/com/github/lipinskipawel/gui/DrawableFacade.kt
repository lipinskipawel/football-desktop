package com.github.lipinskipawel.gui

import com.github.lipinskipawel.board.engine.Board
import com.github.lipinskipawel.board.engine.Player

internal class DrawableFacade(private val gameDrawer: GameDrawer, private val gamePanel: GamePanel) : DrawableFootballPitch {
    override fun drawPitch(board: Board, playerView: Player) {
        gameDrawer.drawMove(board, playerView)
        gamePanel.activePlayer(board.player)
    }

    override fun activePlayer(activePlayer: Player) {
        gamePanel.activePlayer(activePlayer)
    }
}
