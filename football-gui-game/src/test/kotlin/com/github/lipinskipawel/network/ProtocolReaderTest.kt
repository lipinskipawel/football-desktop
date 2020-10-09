package com.github.lipinskipawel.network

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ProtocolReaderTest {

    @Test
    fun shouldReadHeader() {
        val message = "hello world"
        val preparedMessage = ProtocolClient.createClient().convert(message);

        val reader = ProtocolReader(preparedMessage)
        val header = reader.header()
        val data = reader.data()

        Assertions.assertThat(header).isEqualTo(byteArrayOf(0x01) + byteArrayOf(message.toByteArray().size.toByte()))
        Assertions.assertThat(data).isEqualTo(message.toByteArray())
    }
}
