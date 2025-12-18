package com.github.lipinskipawel.game

import io.github.lipinskipawel.board.engine.Direction
import io.github.lipinskipawel.board.engine.Direction.E
import io.github.lipinskipawel.board.engine.Direction.N
import io.github.lipinskipawel.board.engine.Direction.NE
import io.github.lipinskipawel.board.engine.Direction.NW
import io.github.lipinskipawel.board.engine.Direction.S
import io.github.lipinskipawel.board.engine.Direction.SE
import io.github.lipinskipawel.board.engine.Direction.SW
import io.github.lipinskipawel.board.engine.Direction.W
import io.github.lipinskipawel.board.engine.Move
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class GameParser {

    companion object {
        private const val DASH_BYTE = 45.toByte()      // '-'.code.toByte()
        private const val DOT_BYTE = 46.toByte()      // '.'.code.toByte()
        private const val ZERO_BYTE = 48.toByte()     // '0'.code.toByte()
        private const val NINE_BYTE = 57.toByte()     // '9'.code.toByte()
    }

    fun parse(kurnikGame: String): List<Move> {
        val parts = kurnikGame.split("\n\n")
        val kurnikGameMoves = parts[1]

        val dataInputStream = DataInputStream(ByteArrayInputStream(kurnikGameMoves.toByteArray()))

        val moves = mutableListOf<Move>()
        while (dataInputStream.available() > 0) {
            val byte = dataInputStream.readByte()

            if (byte == DOT_BYTE) {
                skipUntilDigit(dataInputStream)
                val whiteMove = parseNumber(dataInputStream).toMove()
                moves.add(whiteMove)

                skipUntilDigit(dataInputStream)
                val blackMove = parseNumber(dataInputStream).toMove()
                if (blackMove.move.isNotEmpty()) {
                    moves.add(blackMove)
                }
            }
        }
        return moves
    }

    private fun skipUntilDigit(dataInputStream: DataInputStream) {
        while (dataInputStream.available() > 0) {
            dataInputStream.mark(1)
            val nextByte = dataInputStream.readByte()
            if (isDigit(nextByte)) {
                dataInputStream.reset()
                break
            }
        }
    }

    private fun parseNumber(dataInputStream: DataInputStream): List<Int> {
        val numbers = mutableListOf<Int>()
        while (dataInputStream.available() > 0) {
            dataInputStream.mark(1)
            val byte = dataInputStream.readByte()
            if (isDigit(byte)) {
                numbers.add(byte - ZERO_BYTE)
                // when creating number instead of list
                // num = (num * 10) + (byte - ZERO_BYTE)
            } else {
                dataInputStream.reset()
                break
            }
        }
        if (isNextDash(dataInputStream)) {
            return emptyList()
        }
        return numbers
    }

    private fun isNextDash(dataInputStream: DataInputStream): Boolean {
        dataInputStream.mark(1)
        val byte = dataInputStream.readByte()
        if (byte.compareTo(DASH_BYTE) == 0) {
            dataInputStream.reset()
            return true
        } else {
            return false
        }
    }

    private fun List<Int>.toMove(): Move {
        return this
            .map { it.toDirection() }
            .let { Move(it) }
    }

    private fun Int.toDirection(): Direction {
        return when (this) {
            0 -> N
            1 -> NE
            2 -> E
            3 -> SE
            4 -> S
            5 -> SW
            6 -> W
            7 -> NW
            else -> throw IllegalArgumentException("Can not map number [$this] to Direction")
        }
    }

    private fun isDigit(byte: Byte): Boolean {
        return byte in ZERO_BYTE..NINE_BYTE
    }
}
