package com.github.lipinskipawel.network

import io.github.lipinskipawel.board.engine.Direction
import io.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class EnvelopeTest {

    @Test
    fun shouldCreateValidEnvelopeObjectFromMoveOneDirection() {
        val message = Move(listOf(Direction.E))

        val result = Envelope.of(message)

        Assertions.assertThat(result.move().move.size).isEqualTo(1)
        Assertions.assertThat(result.move().move[0]).isEqualTo(Direction.E)
    }

    @Test
    fun shouldCreateValidEnvelopeObjectFromMoveTwoDirections() {
        val message = Move(listOf(Direction.E, Direction.N))

        val result = Envelope.of(message)

        Assertions.assertThat(result.move().move.size).isEqualTo(2)
        Assertions.assertThat(result.move().move[0]).isEqualTo(Direction.E)
        Assertions.assertThat(result.move().move[1]).isEqualTo(Direction.N)
    }

    @Test
    fun shouldCreateValidEnvelopeObjectFromMoveThreeDirections() {
        val message = Move(listOf(Direction.E, Direction.NE, Direction.SW))

        val result = Envelope.of(message)

        Assertions.assertThat(result.move().move.size).isEqualTo(3)
        Assertions.assertThat(result.move().move[0]).isEqualTo(Direction.E)
        Assertions.assertThat(result.move().move[1]).isEqualTo(Direction.NE)
        Assertions.assertThat(result.move().move[2]).isEqualTo(Direction.SW)
    }

    @Test
    fun shouldConvertToMoveWithOneDirectionFromByteArray() {
        val preparedNAndSW = "E".toByteArray()

        val move = Envelope.toMove(preparedNAndSW)

        Assertions.assertThat(move.move.size).isEqualTo(1)
        Assertions.assertThat(move.move[0]).isEqualTo(Direction.E)
    }

    @Test
    fun shouldConvertToMoveWithTwoDirectionsFromByteArray() {
        val preparedNAndSW = "N,SW".toByteArray()

        val move = Envelope.toMove(preparedNAndSW)

        Assertions.assertThat(move.move.size).isEqualTo(2)
        Assertions.assertThat(move.move[0]).isEqualTo(Direction.N)
        Assertions.assertThat(move.move[1]).isEqualTo(Direction.SW)
    }
}
