package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move
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
    fun shouldCreateValidEnvelopeObjectFromMoveTwoDirection() {
        val message = Move(listOf(Direction.E, Direction.N))

        val result = Envelope.of(message)

        Assertions.assertThat(result.move().move.size).isEqualTo(2)
        Assertions.assertThat(result.move().move[0]).isEqualTo(Direction.E)
        Assertions.assertThat(result.move().move[1]).isEqualTo(Direction.N)
    }
}
