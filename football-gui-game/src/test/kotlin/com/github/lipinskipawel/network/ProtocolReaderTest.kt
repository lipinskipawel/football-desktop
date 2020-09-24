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

        Assertions.assertThat(header).isEqualTo(ByteArray(1) { 0x01 } + ByteArray(1) { 1 })
        Assertions.assertThat(data).isEqualTo(message.toByteArray())
    }
}
