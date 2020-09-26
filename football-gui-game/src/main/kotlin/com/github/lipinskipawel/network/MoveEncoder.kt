package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Direction.*
import com.github.lipinskipawel.board.engine.Move

/**
 * This class is able to encode Move class. This class is Thread safe.
 * Each Move object contains list of Directions. Direction is mapped as
 * followed:
 *
 * N  - 0000 0001
 * NE - 0000 0010
 * E  - 0000 0011
 * SE - 0000 0101
 * S  - 0000 0110
 * SW - 0000 0111
 * W  - 0000 1000
 * NW - 0000 1001
 */
class MoveEncoder {

    companion object {
        fun encode(move: Move): ByteArray {
            return move
                    .move
                    .map { encode(it) }
                    .toByteArray()
        }

        private fun encode(direction: Direction): Byte {
            return when (direction) {
                N -> 0x01
                NE -> 0x02
                E -> 0x03
                SE -> 0x04
                S -> 0x05
                SW -> 0x06
                W -> 0x07
                NW -> 0x08
                else -> 0x15
            }
        }
    }
}
