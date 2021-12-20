package com.github.lipinskipawel.controller

import com.github.lipinskipawel.board.engine.Boards
import com.github.lipinskipawel.board.engine.Player
import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import javax.swing.JOptionPane

internal class WarmupController(private val drawableFootballPitch: DrawableFootballPitch) : PitchController {
    private var gameFlowController: GameFlowController

    init {
        gameFlowController = GameFlowController(Boards.immutableBoard(), false)
    }

    override fun leftClick(renderablePoint: RenderablePoint) {
        try {
            gameFlowController = gameFlowController.makeAMove(renderablePoint.position)
            drawableFootballPitch.drawPitch(gameFlowController.board(), Player.FIRST)
            gameFlowController.onWinner { winner: Player -> winningMessage(winner) }
        } catch (ee: RuntimeException) {
            JOptionPane.showMessageDialog(null, "You cannot move like that.")
        }
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
        gameFlowController = gameFlowController.undo()
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
