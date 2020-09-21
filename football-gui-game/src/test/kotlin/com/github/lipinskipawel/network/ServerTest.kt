package com.github.lipinskipawel.network

import com.github.lipinskipawel.network.support.SendSupport
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ServerTest {
    companion object {
        private const val PORT = 5432
    }

    @Test
    fun shouldUseByteArrayAsAPI() {
        var holder = ByteArray(1)
        val message = "Hello world!"
        val protocolClient = ProtocolClient.createClient()
        val convertedMessage = protocolClient.convert(message)

        val server = Server.createServer(PORT)
                .onReceived { data -> holder = data }

        val sender = SendSupport("127.0.0.1", PORT)
        sender.send(convertedMessage)
        server.close()

        Assertions.assertThat(holder).isEqualTo(convertedMessage)
    }
}
