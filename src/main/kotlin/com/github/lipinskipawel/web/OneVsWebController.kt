package com.github.lipinskipawel.web

import com.github.lipinskipawel.board.engine.Boards
import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move
import com.github.lipinskipawel.board.engine.Player
import com.github.lipinskipawel.controller.PitchController
import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import com.github.lipinskipawel.network.Connection
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Collectors
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

internal class OneVsWebController(
        private val drawableFootballPitch: DrawableFootballPitch,
        pair: Pair<Connection, Boolean>
) : PitchController {
    private val footballClient: Connection = pair.first

    /**
     * This variable holds information whether current player is the First one to move or Second.
     */
    private val currentPlayer: Player = if (pair.second) {
        Player.FIRST
    } else {
        Player.SECOND
    }

    private var gameFlowController = GameFlowController(Boards.immutableBoard(), false)

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OneVsWebController::class.java)
    }

    init {
        footballClient.onReceivedData { consumeMove(it) }
    }

    private fun consumeMove(move: Move) {
        if (!gameFlowController.isGameOver() && !canMove()) {
            val movesInString = move
                    .move
                    .stream()
                    .map { obj: Direction -> obj.toString() }
                    .collect(Collectors.joining(", "))
            logger.info("consuming move from socket: $movesInString")
            gameFlowController = gameFlowController.makeAMove(move)
            if (SwingUtilities.isEventDispatchThread()) {
                logger.info("consuming move - drawing")
                drawableFootballPitch.drawPitch(gameFlowController.board(), gameFlowController.player())
                gameFlowController.onWinner { winner: Player -> winningMessage(winner) }
            } else {
                logger.info("consuming move - drawing")
                SwingUtilities.invokeLater {
                    drawableFootballPitch.drawPitch(gameFlowController.board(), gameFlowController.player())
                    gameFlowController.onWinner { winner: Player -> winningMessage(winner) }
                }
            }
        }
    }

    override fun leftClick(renderablePoint: RenderablePoint) {
        logger.info("left click")
        if (gameFlowController.isGameOver()) {
            return
        }
        if (canMove()) {
            gameFlowController = gameFlowController.makeAMove(renderablePoint.position)
            if (!canMove()) {
                val move = sendLastMove()
                footballClient.send(move)
            }
            drawableFootballPitch.drawPitch(gameFlowController.board(), Player.FIRST)
        }
    }

    private fun canMove() = gameFlowController.player() == currentPlayer

    private fun sendLastMove(): Move {
        val moves = gameFlowController.board().moveHistory()
        val lastMove = moves[moves.size - 1]
        return Move(lastMove.move)
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
    }

    override fun tearDown() {
        footballClient.close()
    }

    private fun winningMessage(winner: Player) {
        JOptionPane.showMessageDialog(null, "Player $winner won he game.")
    }
}
