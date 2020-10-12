package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress
import java.util.concurrent.CountDownLatch

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest {
    companion object {
        private const val PORT = 5432
        private val server: Server = Server.createServer(PORT)

        @AfterAll
        @JvmStatic
        internal fun closeServer() {
            server.close()
        }
    }

    @Test
    fun `should use Move as API`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())
        val message = Move(listOf(Direction.E))

        server.onReceived {
            holder = it
            startAssertions.countDown()
        }
        val connection = ConnectionManager.connectTo(InetAddress.getByName("127.0.0.1"), PORT)
        connection.send(message)
        connection.close()

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.E)
    }

    @Test
    fun `should received Move with two Directions`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())
        val message = Move(listOf(Direction.E, Direction.NW))

        server.onReceived {
            holder = it
            startAssertions.countDown()
        }
        val connection = ConnectionManager.connectTo(InetAddress.getByName("127.0.0.1"), PORT)
        connection.send(message)
        connection.close()

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(2)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.E)
        Assertions.assertThat(holder.move[1]).isEqualTo(Direction.NW)
    }
}
