package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction.N
import com.github.lipinskipawel.board.engine.Direction.S
import com.github.lipinskipawel.board.engine.Direction.E
import com.github.lipinskipawel.board.engine.Direction.W
import com.github.lipinskipawel.board.engine.Direction.SW
import com.github.lipinskipawel.board.engine.Direction.SE
import com.github.lipinskipawel.board.engine.Direction.NE
import com.github.lipinskipawel.board.engine.Direction.NW
import com.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class MoveEncoderTest {

    @Test
    fun shouldEncodeMoveWithOneDirection() {
        val move = Move(listOf(N))

        val encoded = MoveEncoder.encode(move)

        Assertions.assertThat(encoded.size).isEqualTo(1)
        Assertions.assertThat(encoded).containsExactly(0x01)
    }

    @Test
    fun shouldEncodeMoveWithAllDirections() {
        val move = Move(listOf(N, S, W, E, NW, NE, SW, SE))

        val encoded = MoveEncoder.encode(move)

        Assertions.assertThat(encoded.size).isEqualTo(8)
        Assertions.assertThat(encoded).containsExactly(0x01, 0x05, 0x07, 0x03, 0x08, 0x02, 0x06, 0x04)
    }
}
