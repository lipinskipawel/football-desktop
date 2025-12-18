package com.github.lipinskipawel.game

import io.github.lipinskipawel.board.engine.Direction.E
import io.github.lipinskipawel.board.engine.Direction.N
import io.github.lipinskipawel.board.engine.Direction.NE
import io.github.lipinskipawel.board.engine.Direction.NW
import io.github.lipinskipawel.board.engine.Direction.S
import io.github.lipinskipawel.board.engine.Direction.SE
import io.github.lipinskipawel.board.engine.Direction.SW
import io.github.lipinskipawel.board.engine.Direction.W
import io.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class GameParserTest : WithAssertions {

    private val gameParser = GameParser()

    companion object {
        @JvmStatic
        private fun kurnikGames(): Stream<Arguments> {
            return Stream.of(
                // Named.of() in future junit version
                Arguments.of("win short", "kurnik/win-short.txt", winShort()),
                Arguments.of("win by goal", "kurnik/win-by-goal.txt", winByGoal()),
                Arguments.of("win by time", "kurnik/win-by-time.txt", winByTime()),
                Arguments.of("win by trap", "kurnik/win-by-trap.txt", winByTrap())
            )
        }

        private fun winShort(): List<Move> {
            return listOf(
                Move(listOf(SW)),
                Move(listOf(S)),
                Move(listOf(SE)),
                Move(listOf(S)),
                Move(listOf(SW, NW)),
                Move(listOf(NW)),
                Move(listOf(SW, SE, NE, S, NW)),
                Move(listOf(SW))
            )
        }

        private fun winByGoal(): List<Move> {
            return listOf(
                Move(listOf(SW)),
                Move(listOf(N)),
                Move(listOf(SE)),
                Move(listOf(N, W, NE)),
                Move(listOf(S, SE)),
                Move(listOf(NE)),
                Move(listOf(S)),
                Move(listOf(W, W, W, W)),
                Move(listOf(SE)),
                Move(listOf(NE, NE)),
                Move(listOf(S, S)),
                Move(listOf(NE, E)),
                Move(listOf(NW, NE)),
                Move(listOf(NE, NW)),
                Move(listOf(SW)),
                Move(listOf(NW)),
                Move(listOf(S)),
                Move(listOf(E, SE, SE, SW, E, NW)),
                Move(listOf(NE, W, N)),
                Move(listOf(N, E, NW)),
                Move(listOf(SW)),
                Move(listOf(S, E, SW)),
                Move(listOf(NW, SW, NW)),
                Move(listOf(E)),
                Move(listOf(NE, E, N)),
                Move(listOf(NW, S)),
                Move(listOf(W)),
                Move(listOf(NW, SW)),
                Move(listOf(N, SW)),
                Move(listOf(W, NE, SE, W, SW, E)),
                Move(listOf(N, SE)),
                Move(listOf(N, SW, E, SW)),
                Move(listOf(S)),
                Move(listOf(SW, SE)),
                Move(listOf(E, NE, NW)),
                Move(listOf(W, W, NE, W, NE, S, SE, SW)),
                Move(listOf(S, SW, SE)),
                Move(listOf(SW, E)),
                Move(listOf(NW, E, E)),
                Move(listOf(NE, S)),
                Move(listOf(W, SW, N, NE)),
                Move(listOf(SE, SE)),
                Move(listOf(N)),
                Move(listOf(NW, N, SW, N, NW, SW, E, SE, S, NW)),
                Move(listOf(E, E, E)),
                Move(listOf(E, SE)),
                Move(listOf(SE)),
                Move(listOf(E, SW, N, W)),
                Move(listOf(SW, NW, NE)),
                Move(listOf(W, W, SW)),
                Move(listOf(SW, N, E, NW, N, NE, N)),
                Move(listOf(SE, SE, SE, N, SW, N, SW, S)),
                Move(listOf(E, SW, N, NE, SE)),
                Move(listOf(S, SW))
            )
        }

        private fun winByTime(): List<Move> {
            return listOf(
                Move(listOf(SE)),
                Move(listOf(W)),
                Move(listOf(SE)),
                Move(listOf(N, NE)),
                Move(listOf(S)),
                Move(listOf(NE)),
                Move(listOf(SE, SW)),
                Move(listOf(NW, W, N)),
                Move(listOf(SE, SW, SE)),
                Move(listOf(NE, SE, SW)),
                Move(listOf(S, NE, NW)),
                Move(listOf(S, NW, W)),
                Move(listOf(W)),
                Move(listOf(NE, W)),
                Move(listOf(N, NE, W, S, NW)),
                Move(listOf(NW)),
                Move(listOf(SW)),
                Move(listOf(NW, NE)),
                Move(listOf(SE, NE)),
                Move(listOf(W)),
                Move(listOf(NE)),
                Move(listOf(SE)),
                Move(listOf(E)),
                Move(listOf(NE)),
                Move(listOf(S)),
                Move(listOf(E)),
                Move(listOf(NW, W)),
                Move(listOf(SE, W, SE)),
                Move(listOf(NE, NE, NW)),
                Move(listOf(N, SE, W, S)),
                Move(listOf(SW, S, SW, E, N, SE, NE, NW, E, NW, W, NE, NW, SW)),
                Move(listOf(S, SW, SE)),
                Move(listOf(SW, W, SW)),
                Move(listOf(SE)),
                Move(listOf(SE, N, SE, N, E)),
                Move(listOf(SE, W, N, NW, SW, W, NE, W)),
                Move(listOf(SE, SW)),
                Move(listOf(N, W)),
                Move(listOf(N, NW, E)),
                Move(listOf(S, E, SW, W)),
                Move(listOf(W, SE)),
                Move(listOf(N, SW, E, NE, SE, E, SW)),
                Move(listOf(SW, NW)),
                Move(listOf(S, NE)),
                Move(listOf(SE, N, W, NE, S, NW)),
                Move(listOf(NE, N, NW, SW)),
                Move(listOf(S, NE, W, N, SW, E, NW, NE)),
                Move(listOf(NE, NW)),
                Move(listOf(S, E, S, W, SE, E, S, NE, NW)),
                Move(listOf(E)),
                Move(listOf(NW, E, SW, S, NE, NE, S, S, NW, N, N)),
                Move(listOf(W))
            )
        }

        private fun winByTrap(): List<Move> {
            return listOf(
                Move(listOf(N)),
                Move(listOf(NW)),
                Move(listOf(NE)),
                Move(listOf(W)),
                Move(listOf(N)),
                Move(listOf(SW)),
                Move(listOf(SE, N, NE)),
                Move(listOf(W, SE, N, SE)),
                Move(listOf(N)),
                Move(listOf(SW, E, SE)),
                Move(listOf(N)),
                Move(listOf(NW, NE, SE)),
                Move(listOf(N, SW)),
                Move(listOf(SW, E, SW)),
                Move(listOf(SW, N)),
                Move(listOf(N, SE, N, SW, NW, SW)),
                Move(listOf(NW)),
                Move(listOf(E, S, W)),
                Move(listOf(N, NW, NE, SE)),
                Move(listOf(S, SW, NW, E, SW, E, S)),
                Move(listOf(NE, E, E, E, E, NE)),
                Move(listOf(NW, W, W, NW, SW, N, SE, W, SE, W, NW)),
                Move(listOf(N))
            )
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("kurnikGames")
    fun `load game play moves`(testName: String, fileName: String, expected: List<Move>) {
        // given
        val fileContent = readFile(fileName)

        // when
        val moves = gameParser.parse(fileContent)

        // then
        assertThat(moves).isEqualTo(expected)
    }

    private fun readFile(fileName: String): String {
        return this::class.java.classLoader
            .getResource(fileName)
            ?.readText()
            ?: throw IllegalStateException("File not found $fileName")
    }
}
