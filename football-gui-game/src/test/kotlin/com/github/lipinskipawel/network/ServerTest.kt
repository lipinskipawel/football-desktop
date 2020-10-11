package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.net.InetAddress

internal class ServerTest {
    companion object {
        private const val PORT = 5432
    }

    @Test
    fun shouldUseMoveAsAPI() {
        var holder = Move(emptyList())
        val message = Move(listOf(Direction.E))

        val server = Server.createServer(PORT)
                .onReceived { holder = it }
        val connection = ConnectionManager.connectTo(InetAddress.getByName("127.0.0.1"), PORT)
        connection.send(message)
        server.close()
        connection.close()

        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.E)
    }
}
