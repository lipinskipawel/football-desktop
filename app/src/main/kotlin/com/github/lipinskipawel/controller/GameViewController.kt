package com.github.lipinskipawel.controller

import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import io.github.lipinskipawel.board.engine.Boards
import io.github.lipinskipawel.board.engine.Move
import io.github.lipinskipawel.board.engine.Player
import io.github.lipinskipawel.board.engine.Player.FIRST
import javax.swing.JOptionPane

class GameViewController(
    private val drawableFootballPitch: DrawableFootballPitch,
    private val moves: List<Move>
) : PitchController {
    private var gameFlowController = GameFlowController(Boards.immutableBoard(), false)
    private var currentMove: Int = 0

    override fun leftClick(renderablePoint: RenderablePoint) {
        try {
            if (currentMove == moves.size) {
                return
            }
            gameFlowController = gameFlowController.makeAMove(moves[currentMove])
            drawableFootballPitch.drawPitch(gameFlowController.board(), FIRST)
            gameFlowController.onWinner { winningMessage(it) }
            currentMove++
        } catch (ee: RuntimeException) {
            JOptionPane.showMessageDialog(null, "You can not move like that.")
        }
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
        if (currentMove == 0) {
            return
        }
        var gameFlowController = GameFlowController(Boards.immutableBoard(), false)
        for (i in 0..<currentMove - 1) {
            gameFlowController = gameFlowController.makeAMove(moves[i])
        }
        this.gameFlowController = gameFlowController
        drawableFootballPitch.drawPitch(this.gameFlowController.board(), FIRST)
        currentMove--
    }

    private fun winningMessage(winner: Player) {
        JOptionPane.showMessageDialog(null, "Player $winner won he game.")
    }

    override fun tearDown() {
        gameFlowController = GameFlowController(Boards.immutableBoard(), false)
        drawableFootballPitch.drawPitch(gameFlowController.board(), gameFlowController.player())
    }
}