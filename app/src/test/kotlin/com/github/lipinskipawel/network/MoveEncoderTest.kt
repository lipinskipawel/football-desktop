package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction.*
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

    @Test
    fun shouldDecodeOneDirection() {
        val preparedNorthDirection = byteArrayOf(0x01)

        val decodedDirection = MoveEncoder.decode(preparedNorthDirection)

        Assertions.assertThat(decodedDirection.move.size).isEqualTo(1)
        Assertions.assertThat(decodedDirection.move[0]).isEqualTo(N)
    }

    @Test
    fun shouldDecodeMultipleDirections() {
        val preparedNorthDirection = byteArrayOf(0x01, 0x05, 0x08)

        val decodedDirection = MoveEncoder.decode(preparedNorthDirection)

        Assertions.assertThat(decodedDirection.move.size).isEqualTo(3)
        Assertions.assertThat(decodedDirection.move[0]).isEqualTo(N)
        Assertions.assertThat(decodedDirection.move[1]).isEqualTo(S)
        Assertions.assertThat(decodedDirection.move[2]).isEqualTo(NW)
    }
}
