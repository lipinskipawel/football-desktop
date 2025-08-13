package com.github.lipinskipawel.controller

import com.github.lipinskipawel.game.GameFlowController
import io.github.lipinskipawel.board.engine.Boards
import io.github.lipinskipawel.board.engine.Direction
import io.github.lipinskipawel.board.engine.Player
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class GameFlowControllerTest {

    @Test
    fun `make a move`() {
        val game = GameFlowController()
        val board = Boards.immutableBoard()

        val afterMove = game.makeAMove(48)
        val followUpBoard = board.executeMove(Direction.NW)

        assertAll("after move",
            { Assertions.assertThat(afterMove.isGameOver()).isFalse() },
            { Assertions.assertThat(afterMove.player()).isEqualTo(Player.SECOND) },
            { Assertions.assertThat(afterMove.board()).isEqualToComparingFieldByFieldRecursively(followUpBoard) }
        )
    }

    @Test
    fun `make a move and undo`() {
        val game = GameFlowController()
        val board = Boards.immutableBoard()

        val afterMove = game.makeAMove(48).undo()
        val followUpBoard = board.executeMove(Direction.NW).undo()

        assertAll("after move and undo",
            { Assertions.assertThat(afterMove.isGameOver()).isFalse() },
            { Assertions.assertThat(afterMove.player()).isEqualTo(Player.FIRST) },
            { Assertions.assertThat(afterMove.board()).isEqualToComparingFieldByFieldRecursively(followUpBoard) }
        )
    }

    @Test
    fun `should be game over`() {
        val game = GameFlowController()
        val board = Boards.immutableBoard()

        val gameOver = game.gameOver(board, true)
        val gameOver2 = game.gameOver(board)

        assertAll("game over with/without overloading argument",
            { Assertions.assertThat(gameOver.isGameOver()).isTrue() },
            { Assertions.assertThat(gameOver.player()).isEqualTo(Player.FIRST) },
            { Assertions.assertThat(gameOver.board()).isEqualToComparingFieldByFieldRecursively(board) },
            { Assertions.assertThat(gameOver2.isGameOver()).isTrue() },
            { Assertions.assertThat(gameOver2.player()).isEqualTo(Player.FIRST) },
            { Assertions.assertThat(gameOver2).isEqualToComparingFieldByFieldRecursively(gameOver) }
        )
    }

    @Test
    fun `two moves, undoPlayerMove`() {
        val game = GameFlowController()

        val afterMoves = game
            .makeAMove(48)
            .makeAMove(39)
            .undoPlayerMove { }

        assertAll("after two moves and ",
            { Assertions.assertThat(afterMoves.player()).isEqualTo(Player.FIRST) }
        )
    }

    @Test
    fun `three moves, undoPlayerMove`() {
        val game = GameFlowController()

        val afterMoves = game
            .makeAMove(48)
            .makeAMove(39)
            .makeAMove(40)
            .undoPlayerMove { }

        assertAll("after two moves and ",
            { Assertions.assertThat(afterMoves.player()).isEqualTo(Player.SECOND) },
            { Assertions.assertThat(afterMoves.board().ballPosition).isEqualTo(48) }
        )
    }
}
