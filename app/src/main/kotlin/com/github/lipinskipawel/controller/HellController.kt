package com.github.lipinskipawel.controller

import com.github.lipinskipawel.board.engine.Board
import com.github.lipinskipawel.board.engine.Boards
import com.github.lipinskipawel.board.engine.Player
import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import javax.swing.JOptionPane

internal class HellController(private val drawableFootballPitch: DrawableFootballPitch) : PitchController {
    private val tokenForPlayer = mutableMapOf<Player, Int>()
    private var gameFlowController = GameFlowController(Boards.immutableBoard(), false)

    init {
        tokenForPlayer[Player.FIRST] = 2
        tokenForPlayer[Player.SECOND] = 2
    }

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
        if (playerAllowedToUndo(gameFlowController.board())) {
            gameFlowController = gameFlowController.undoPlayerMove { }
            tokenForPlayer.compute(gameFlowController.player()) { _: Player?, points -> points!! - 1 }
            tokenForPlayer.compute(gameFlowController.player().opposite()) { _: Player?, points -> points!! + 1 }
            val message = """
                Player ${gameFlowController.player()} tokens : ${tokenForPlayer[gameFlowController.player()]}
                Player ${gameFlowController.player().opposite()} tokens : ${tokenForPlayer[gameFlowController.player().opposite()]}
                """
            JOptionPane.showMessageDialog(null, message)
        }
        drawableFootballPitch.drawPitch(gameFlowController.board(), Player.FIRST)
    }

    private fun playerAllowedToUndo(board: Board<Player>): Boolean {
        return tokenForPlayer[board.player]!! > 0
    }

    private fun winningMessage(winner: Player) {
        JOptionPane.showMessageDialog(null, "Player $winner won he game.")
    }

    override fun tearDown() {
        gameFlowController = GameFlowController(Boards.immutableBoard(), false)
        drawableFootballPitch.drawPitch(gameFlowController.board(), gameFlowController.player())
        tokenForPlayer[Player.FIRST] = 2
        tokenForPlayer[Player.SECOND] = 2
    }
}
