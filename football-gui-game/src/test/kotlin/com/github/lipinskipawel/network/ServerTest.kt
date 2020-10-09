package com.github.lipinskipawel.network

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.net.InetAddress

internal class ServerTest {
    companion object {
        private const val PORT = 5432
    }

    @Test
    fun shouldUseByteArrayAsAPI() {
        var holder = ByteArray(1)
        val message = "Hello world!"
        val converted = ProtocolClient.createClient().convert(message)

        val server = Server.createServer(PORT)
                .onReceived { holder = it }
        val connection = ConnectionManager.connectTo(InetAddress.getByName("127.0.0.1"), PORT)
        connection.send(Envelope(message))
        server.close()
        connection.close()

        Assertions.assertThat(holder).isEqualTo(converted)
    }
}
