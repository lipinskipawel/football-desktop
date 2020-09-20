package com.github.lipinskipawel.network

/**
 * This class is responsible for converting data into proper Protocol format.
 * This class is Thread safe.
 * Protocol format:
 * | version of protocol | number of segments | data |
 *
 * Size of each field:
 * version of protocol - 1 byte
 * number of segments  - 1 byte
 * data                - 2048 bytes
 */
internal class ProtocolClient {

    companion object {
        private val PROTOCOL_VERSION: ByteArray = ByteArray(1) { 0x01 }
        private val SEGMENT_SIZE: Int = 2048
        private val NUMBER_OF_SEGMENT: Int = 100

        fun createClient(): ProtocolClient {
            return ProtocolClient()
        }
    }

    fun convert(data: String): ByteArray {
        return createHeader(data) + data.toByteArray()
    }

    private fun createHeader(data: String): ByteArray {
        val sizeOfData = data.toByteArray().size
        val divided = sizeOfData / SEGMENT_SIZE
        val isTheNumberIsNatural = sizeOfData % SEGMENT_SIZE == 0
        return if (isTheNumberIsNatural) {
            PROTOCOL_VERSION + computeNumberOfSegmentsNeeded(divided)
        } else {
            PROTOCOL_VERSION + computeNumberOfSegmentsNeeded(divided + 1)
        }
    }

    private fun computeNumberOfSegmentsNeeded(divided: Int): ByteArray {
        return if (isMoreThatAllowed(divided))
            ByteArray(1) { NUMBER_OF_SEGMENT.toByte() }
        else
            ByteArray(1) { divided.toByte() }
    }

    private fun isMoreThatAllowed(divided: Int): Boolean {
        return divided > NUMBER_OF_SEGMENT
    }
}
