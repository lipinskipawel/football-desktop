package com.github.lipinskipawel.network

/**
 * This class is responsible for converting data into proper Protocol format.
 * This class is Thread safe.
 * Protocol format:
 * | version of protocol | number of segments | data |
 *
 * Size of each field:
 * version of protocol - 1 byte
 * length of data      - 1 byte
 * data                - 255 bytes
 */
internal class ProtocolClient {

    companion object {
        private const val PROTOCOL_VERSION: Byte = 0x01

        fun createClient(): ProtocolClient {
            return ProtocolClient()
        }
    }

    fun convert(data: String): ByteArray {
        return createHeader(data) + data.toByteArray()
    }

    private fun createHeader(data: String): ByteArray {
        val sizeOfData = data.toByteArray().size
        if (sizeOfData > 255) {
            throw Exception("The size of data [$sizeOfData] is more that 255")
        }
        return byteArrayOf(PROTOCOL_VERSION, sizeOfData.toByte())
    }
}
