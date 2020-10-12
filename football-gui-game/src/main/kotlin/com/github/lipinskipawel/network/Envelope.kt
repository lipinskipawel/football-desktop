package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move

class Envelope private constructor(private val encodedData: ByteArray) {

    companion object {
        /**
         * This method take Move and converts it into Envelope.
         */
        fun of(move: Move): Envelope {
            val data = move
                    .move
                    .map { java.lang.String.valueOf(it) }
                    .stream()
                    .collect(java.util.stream.Collectors.joining(","))
            return Envelope(ProtocolClient.createClient().convert(data))
        }

        /**
         * This method converts ByteArray to Move.
         */
        fun toMove(bytes: ByteArray): Move {
            val directions = bytes
                    .map { it.toChar() }
                    .map { java.lang.String.valueOf(it) }
                    .reduceRight { s, acc -> s.plus(acc) }
                    .split(",")
                    .map { Direction.valueOf(it) }
                    .toMutableList()
            return Move(directions)
        }
    }

    fun move(): Move {
        val directions = ProtocolReader(encodedData)
                .data()
                .map { it.toChar() }
                .map { java.lang.String.valueOf(it) }
                .reduceRight { s, acc -> s.plus(acc) }
                .split(",")
                .map { Direction.valueOf(it) }
        return Move(directions)
    }

    fun toByteArray(): ByteArray {
        return encodedData
    }
}
