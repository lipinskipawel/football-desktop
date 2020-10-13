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
 * This test is responsible for testing the [DirectConnection] class.
 * The tests here are focus on the API methods of [Connection].
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DirectConnectionTest {
    companion object {
        private const val PORT = 6767
        private val address: InetAddress = InetAddress.getByName("127.0.0.1")
        private val waitingForConnection: Connection = ConnectionManager.waitForConnection(PORT).get()
        private val connection: Connection = ConnectionManager.connectTo(address, PORT)

        @AfterAll
        @JvmStatic
        internal fun closeConnections() {
            waitingForConnection.close()
        }
    }

    @Test
    fun `should execute an callback onReceivedData`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())

        connection.onReceivedData(Consumer {
            holder = it
            startAssertions.countDown()
        })
        waitingForConnection.send(Move(listOf(Direction.W)))

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.W)
    }

    @Test
    fun `should connect and send the data`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())

        waitingForConnection
                .onReceivedData(Consumer {
                    holder = it
                    startAssertions.countDown()
                })
        connection.send(Move(listOf(Direction.W)))

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.W)
    }
}
