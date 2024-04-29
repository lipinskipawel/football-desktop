package com.github.lipinskipawel.network

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ProtocolReaderTest {

    @Test
    fun shouldReadDirection() {
        val message = "E"
        val preparedMessage = ProtocolClient.createClient().convert(message);

        val reader = ProtocolReader(preparedMessage)
        val header = reader.header()
        val data = reader.data()

        Assertions.assertThat(header).isEqualTo(byteArrayOf(0x01) + byteArrayOf(message.toByteArray().size.toByte()))
        Assertions.assertThat(data).isEqualTo(message.toByteArray())
    }

    @Test
    fun shouldReadManyDirections() {
        val message = "E, NS"
        val preparedMessage = ProtocolClient.createClient().convert(message);

        val reader = ProtocolReader(preparedMessage)
        val header = reader.header()
        val data = reader.data()

        Assertions.assertThat(header).isEqualTo(byteArrayOf(0x01) + byteArrayOf(message.toByteArray().size.toByte()))
        Assertions.assertThat(data).isEqualTo(message.toByteArray())
    }
}
