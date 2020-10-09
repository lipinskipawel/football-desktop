package com.github.lipinskipawel.network

open class Envelope(data: String) {

    private val reader = ProtocolReader(ProtocolClient.createClient().convert(data))

    fun toByteArray(): ByteArray {
        return reader.header() + reader.data()
    }
}
