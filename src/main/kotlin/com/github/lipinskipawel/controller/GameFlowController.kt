package com.github.lipinskipawel.controller

import com.github.lipinskipawel.board.engine.Board
import com.github.lipinskipawel.board.engine.Boards
import com.github.lipinskipawel.board.engine.Move
import com.github.lipinskipawel.board.engine.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GameFlowController(
        private val board: Board = Boards.immutableBoard(),
        private var endGame: Boolean = false
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GameFlowController::class.java)
    }

    fun makeAMove(point: Int): GameFlowController {
        val move = this.board.ballAPI.kickBallTo(point)
        if (this.board.isMoveAllowed(move)) {
            val afterMove = this.board.executeMove(move)
            val end = afterMove.isGoal
            return GameFlowController(afterMove, end)
        }
        return this
    }

    fun makeAMove(move: Move): GameFlowController {
        val afterMove = this.board.executeMove(move)
        val end = afterMove.isGoal
        return GameFlowController(afterMove, end)
    }

    /**
     * @param callback this is a hack. The code inside the block should be move to this class.
     */
    fun undoPlayerMove(callback: () -> Unit = {}): GameFlowController {
        val afterUndo = this.board.undo()
        if (isSmallMoveUndo(afterUndo)) {
            return GameFlowController(afterUndo)
        }
        val two = try {
            val one = undoMoves(this.board)
            undoMoves(one.board).board
        } catch (ee: Exception) {
            Boards.immutableBoard()
        }
        try {
            callback()
        } catch (ex: Exception) {
            logger.error("Given callback exception:\n$ex}")
        } finally {
            return GameFlowController(two)
        }
    }

    fun undoOnlyCurrentPlayerMove(): GameFlowController {
        return GameFlowController(this.board.undoPlayerMove())
    }

    fun onWinner(callback: (Player) -> Unit) {
        this.board
                .takeTheWinner()
                .ifPresent { callback(it) }
    }

    fun onPlayerHitTheCorner(runOnHitCorner: () -> Unit) {
        if (isHitTheCorner())
            runOnHitCorner()
    }

    fun undo(): GameFlowController = GameFlowController(board.undo())

    fun board(): Board = this.board

    fun player(): Player = this.board.player

    fun isGameOver(): Boolean = this.endGame

    fun gameOver(board: Board, gameOver: Boolean = true) = GameFlowController(board, gameOver)


    private fun undoMoves(board: Board): GameFlowController {
        var beforeUndoBoard = board
        do {
            beforeUndoBoard = beforeUndoBoard.undo()
        } while (beforeUndoBoard.player == board.player)
        val after = beforeUndoBoard
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
        return GameFlowController(after)
    }

    private fun isSmallMoveUndo(afterUndo: Board): Boolean = afterUndo.player == this.board.player

    private fun isHitTheCorner() = this.board.allLegalMoves().isEmpty()
}
