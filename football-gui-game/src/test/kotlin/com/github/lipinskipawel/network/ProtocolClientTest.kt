package com.github.lipinskipawel.network

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ProtocolClientTest {

    @Test
    @ExperimentalStdlibApi
    fun shouldConvertStringToProperFormat() {
        val message = "Hello world!"
        val client = ProtocolClient.createClient()

        val bytes: ByteArray = client.convert(message)

        Assertions.assertThat(bytes[0]).isEqualTo(0x02)
        Assertions.assertThat(bytes[1]).isEqualTo(1)
        Assertions.assertThat(bytes.decodeToString(2, bytes.size)).isEqualTo(message)
    }
}
