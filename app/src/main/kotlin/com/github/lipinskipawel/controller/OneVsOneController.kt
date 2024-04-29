package com.github.lipinskipawel.controller

import com.github.lipinskipawel.board.engine.Boards
import com.github.lipinskipawel.board.engine.Player
import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import javax.swing.JOptionPane

internal class OneVsOneController(private val drawableFootballPitch: DrawableFootballPitch) : PitchController {
    private var gameFlowController = GameFlowController(Boards.immutableBoard(), false)

    override fun leftClick(renderablePoint: RenderablePoint) {
        if (gameFlowController.isGameOver()) {
            return
        }
        gameFlowController = gameFlowController.makeAMove(renderablePoint.position)
        drawableFootballPitch.drawPitch(gameFlowController.board(), Player.FIRST)
        gameFlowController.onWinner { winningMessage(it) }
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
        if (gameFlowController.isGameOver()) {
            return
        }
        gameFlowController = gameFlowController.undoPlayerMove { }
        drawableFootballPitch.drawPitch(gameFlowController.board(), Player.FIRST)
    }

    private fun winningMessage(winner: Player) {
        JOptionPane.showMessageDialog(null, "Player $winner won he game.")
    }

    override fun tearDown() {
        gameFlowController = GameFlowController(Boards.immutableBoard(), false)
        drawableFootballPitch.drawPitch(gameFlowController.board(), gameFlowController.player())
    }
}
