package com.github.lipinskipawel.controller

import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import com.github.lipinskipawel.gui.UserDialogPresenter
import com.github.lipinskipawel.network.Connection
import io.github.lipinskipawel.board.engine.Boards
import io.github.lipinskipawel.board.engine.Move
import io.github.lipinskipawel.board.engine.Player
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors.joining
import javax.swing.SwingUtilities

internal class OneVsLanController(
    private val drawableFootballPitch: DrawableFootballPitch,
    private val dialogPresenter: UserDialogPresenter
) : PitchController {
    companion object {
        private val logger = LoggerFactory.getLogger(OneVsLanController::class.java)
    }

    private val gameFlowController: AtomicReference<GameFlowController> =
        AtomicReference(GameFlowController(Boards.immutableBoard(), false))

    /**
     * This player is set based on the chosen policy.
     * Player can chose either waiting policy (server) or
     * active one (connecting).
     */
    private lateinit var currentPlayer: Player
    private var connection: Connection? = null

    fun injectConnection(connection: Connection?, client: Boolean) {
        this.connection = connection
        this.connection!!.onReceivedData { consumeTheMoveFromConnection(it) }
        currentPlayer = if (client) Player.FIRST else Player.SECOND
        drawableFootballPitch.activePlayer(currentPlayer)
    }

    override fun leftClick(renderablePoint: RenderablePoint) {
        logger.info("left click")
        if (gameFlowController.get().isGameOver()) {
            return
        }
        if (canMove(gameFlowController.get().player())) {
            gameFlowController.updateAndGet { it.makeAMove(renderablePoint.position) }
            if (!canMove(gameFlowController.get().player())) {
                val moves = gameFlowController.get().board().moveHistory()
                val lastMove = moves[moves.size - 1]
                val lastMoveInString = lastMove
                    .move
                    .stream()
                    .map { it.toString() }
                    .collect(joining(", "))
                logger.info("sending move $lastMoveInString")
                gameFlowController.get().onWinner { winningMessage(it) }
                connection!!.send(lastMove)
            }
            logger.info("Drawing the move")
            drawableFootballPitch.drawPitch(gameFlowController.get().board(), currentPlayer)
        } else {
            dialogPresenter.showMessage(null, "You can not make move right now.\nWait for you enemy move")
        }
    }

    private fun canMove(player: Player): Boolean {
        return currentPlayer === player
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
        if (gameFlowController.get().isGameOver()) {
            return
        }
        if (canMove(gameFlowController.get().player())) {
            gameFlowController.updateAndGet { it.undoOnlyCurrentPlayerMove() }
            drawableFootballPitch.drawPitch(gameFlowController.get().board(), currentPlayer)
        } else {
            dialogPresenter.showMessage(null, "You can not undo move.\nYour opponent is making a move.")
        }
    }

    private fun consumeTheMoveFromConnection(move: Move) {
        if (!gameFlowController.get().isGameOver() &&
            !canMove(gameFlowController.get().player())
        ) {
            val movesInString = move
                .move
                .stream()
                .map { it.toString() }
                .collect(joining(", "))
            logger.info("consuming move from socket: $movesInString")
            gameFlowController.updateAndGet { it.makeAMove(move) }
            if (SwingUtilities.isEventDispatchThread()) {
                logger.info("consuming move - drawing")
                drawableFootballPitch.drawPitch(gameFlowController.get().board(), gameFlowController.get().player())
                gameFlowController.get().onWinner { winningMessage(it) }
            } else {
                logger.info("consuming move - drawing")
                SwingUtilities.invokeLater {
                    drawableFootballPitch.drawPitch(gameFlowController.get().board(), gameFlowController.get().player())
                    gameFlowController.get().onWinner { winningMessage(it) }
                }
            }
        }
    }

    private fun winningMessage(winner: Player) {
        dialogPresenter.showMessage(null, "Player $winner won he game.")
    }

    override fun tearDown() {
        gameFlowController.updateAndGet { GameFlowController(Boards.immutableBoard(), false) }
        drawableFootballPitch.drawPitch(gameFlowController.get().board(), gameFlowController.get().player())
        connection!!.close()
    }
}
