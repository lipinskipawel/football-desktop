package com.github.lipinskipawel.web

import com.github.lipinskipawel.board.engine.Boards
import com.github.lipinskipawel.board.engine.Player
import com.github.lipinskipawel.controller.PitchController
import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OneVsWebController(
        private val drawableFootballPitch: DrawableFootballPitch,
        pair: Pair<FootballGameClient, Boolean>
) : PitchController {
    private val footballClient: FootballGameClient = pair.first

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

    override fun leftClick(renderablePoint: RenderablePoint) {
        logger.info("left click")
        if (gameFlowController.isGameOver()) {
            return
        }
        if (canMove()) {
            gameFlowController = gameFlowController.makeAMove(renderablePoint.position)
            footballClient.send(sendLastMove())
            drawableFootballPitch.drawPitch(gameFlowController.board(), Player.FIRST)
        }
    }

    private fun canMove() = gameFlowController.player() == currentPlayer

    private fun sendLastMove(): List<String> {
        val moves = gameFlowController.board().moveHistory()
        val lastMove = moves[moves.size - 1]
        return lastMove
                .move
                .map { it.toString() }
                .toList()
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
    }

    override fun tearDown() {
        footballClient.close()
    }
}
