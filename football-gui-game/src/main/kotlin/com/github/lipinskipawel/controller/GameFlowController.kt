package com.github.lipinskipawel.controller

import com.github.lipinskipawel.board.engine.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.Throws

class GameFlowController(
        private val board: BoardInterface = Boards.immutableBoard(),
        private var endGame: Boolean = false
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GameFlowController::class.java)
    }

    /**
     * This method will thrown RuntimeException whenever ball stuck on something.
     * Ball can stuck in the goal area, or in the point where no more moves are allowed.
     */
    @Throws(CantMakeAMove::class)
    fun makeAMove(point: Int, block: () -> Unit = {}): GameFlowController {
        val move: Direction
        try {
            move = this.board.ballAPI.kickBallTo(point)
        } catch (ee: RuntimeException) {
            throw CantMakeAMove()
        }
        if (this.board.isMoveAllowed(move)) {
            val afterMove = this.board.executeMove(move)
            if (afterMove.player != this.board.player) {
                block()
            }
            val end = afterMove.isGoal
            return GameFlowController(afterMove, end)
        }
        return this
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
        logger.info("AI kotlin | end game : $end")
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

    fun board(): BoardInterface = this.board

    fun player(): Player = this.board.player

    fun isGameOver(): Boolean = this.endGame

    fun gameOver(board: BoardInterface, gameOver: Boolean = true) = GameFlowController(board, gameOver)


    private fun undoMoves(board: BoardInterface): GameFlowController {
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

    private fun isSmallMoveUndo(afterUndo: BoardInterface): Boolean = afterUndo.player == this.board.player

    private fun isHitTheCorner() = this.board.allLegalMoves().isEmpty()
}
