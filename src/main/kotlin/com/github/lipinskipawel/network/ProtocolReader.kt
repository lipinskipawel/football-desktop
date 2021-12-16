package com.github.lipinskipawel.network

internal class ProtocolReader(private val message: ByteArray) {
    private val header = header()

    fun header(): ByteArray {
        val version = message[0]
        val numberOfSegments = message[1]
        return byteArrayOf(version, numberOfSegments)
    }

    fun data(): ByteArray {
        val startingByteOfData = header.size
        return message.sliceArray(IntRange(startingByteOfData, message.size - 1))
    }
}
