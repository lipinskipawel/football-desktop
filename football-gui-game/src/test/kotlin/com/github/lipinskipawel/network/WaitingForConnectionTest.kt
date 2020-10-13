package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import java.util.function.Consumer

/**
 * This test is responsible for testing the [WaitingForConnection] class.
 * The tests here are focus on the API methods of [Connection].
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WaitingForConnectionTest {
    companion object {
        private const val PORT = 5432
        private val waitingForConnection: WaitingForConnection = WaitingForConnection.waitOnPort(PORT)
        private val connection: Connection = ConnectionManager.connectTo(InetAddress.getLocalHost(), PORT)

        @AfterAll
        @JvmStatic
        internal fun closeConnections() {
            waitingForConnection.close()
            connection.close()
        }
    }

    @Test
    fun `should use Move as API`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())
        val message = Move(listOf(Direction.E))

        waitingForConnection.onReceivedData(Consumer {
            holder = it
            startAssertions.countDown()
        })
        connection.send(message)

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.E)
    }

    @Test
    fun `should received Move with two Directions`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())
        val message = Move(listOf(Direction.E, Direction.NW))

        waitingForConnection.onReceivedData(Consumer {
            holder = it
            startAssertions.countDown()
        })
        connection.send(message)

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(2)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.E)
        Assertions.assertThat(holder.move[1]).isEqualTo(Direction.NW)
    }

    @Test
    fun `should wait for connection and send data`() {
        val startAssertion = CountDownLatch(1)
        var holder = Move(emptyList())

        connection.onReceivedData(Consumer {
            holder = it
            startAssertion.countDown()
        })
        waitingForConnection.send(Move(listOf(Direction.W)))

        startAssertion.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.W)
    }
}
