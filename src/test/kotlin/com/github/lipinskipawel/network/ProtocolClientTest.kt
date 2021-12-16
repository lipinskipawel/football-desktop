package com.github.lipinskipawel.network

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ProtocolClientTest {

    @Test
    @ExperimentalStdlibApi
    fun shouldConvertDirectionToProtocolFormat() {
        val message = "E"
        val client = ProtocolClient.createClient()

        val bytes: ByteArray = client.convert(message)

        Assertions.assertThat(bytes[0]).isEqualTo(0x01)
        Assertions.assertThat(bytes[1]).isEqualTo(message.toByteArray().size.toByte())
        Assertions.assertThat(bytes.decodeToString(2, bytes.size)).isEqualTo(message)
    }

    @Test
    @ExperimentalStdlibApi
    fun shouldConvertManyDirectionsToProtocolFormat() {
        val message = "E,NW"
        val client = ProtocolClient.createClient()

        val bytes: ByteArray = client.convert(message)

        Assertions.assertThat(bytes[0]).isEqualTo(0x01)
        Assertions.assertThat(bytes[1]).isEqualTo(message.toByteArray().size.toByte())
        Assertions.assertThat(bytes.decodeToString(2, bytes.size)).isEqualTo(message)
    }

    @Test
    @ExperimentalStdlibApi
    fun shouldThrowException() {
        val message = "Should thr".multiplyBy(100)
        val client = ProtocolClient.createClient()

        Assertions.assertThatThrownBy { client.convert(message) }
                .isInstanceOf(Exception::class.java)
                .hasMessageContaining("is more that")
    }
}

/**
 * Multiply given string n times.
 */
private fun String.multiplyBy(multiply: Int): String {
    val builder = StringBuilder()
    for (number in 1..multiply) {
        builder.append(this)
    }
    return builder.toString()
}
