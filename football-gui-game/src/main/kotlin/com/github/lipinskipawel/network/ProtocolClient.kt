package com.github.lipinskipawel.network

internal class ProtocolClient {

    companion object {
        private val META_HEAD_JAVA_SERIALIZED_OBJECTS: ByteArray = ByteArray(1) { 0x01 }
        private val META_HEAD_STRING: ByteArray = ByteArray(1) { 0x02 }
        private val SEGMENT_SIZE: Int = 127
        private val NUMBER_OF_SEGMENT: Int = 127

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
            META_HEAD_STRING + computeNumberOfSegmentsNeeded(divided)
        } else {
            META_HEAD_STRING + computeNumberOfSegmentsNeeded(divided + 1)
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
