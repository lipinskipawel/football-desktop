package com.github.lipinskipawel.network

import io.github.lipinskipawel.board.engine.Direction
import io.github.lipinskipawel.board.engine.Move
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.function.Executable
import java.net.InetAddress
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

/**
 * This test is responsible for testing the [DirectConnection] class.
 * The tests here are focus on the API methods of [Connection].
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DirectConnectionTest {
    companion object {
        private lateinit var waitingForConnection: Connection
        private lateinit var connection: Connection

        @BeforeAll
        @JvmStatic
        internal fun createServerAndConnection() {
            val pool = Executors.newSingleThreadExecutor()
            val futureServer = pool.submit(Callable {
                ConnectionManager.waitForConnection(InetAddress.getLocalHost())
            })
            connection = ConnectionManager.connectTo(InetAddress.getLocalHost(), Duration.ofMillis(30))
            waitingForConnection = futureServer.get()
            pool.shutdown()
        }

        @AfterAll
        @JvmStatic
        internal fun closeConnections() {
            waitingForConnection.close()
            connection.close()
        }
    }

    @Test
    fun `should execute an callback onReceivedData`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())

        connection.onReceivedData {
            holder = it
            startAssertions.countDown()
        }
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
            .onReceivedData {
                holder = it
                startAssertions.countDown()
            }
        connection.send(Move(listOf(Direction.W)))

        startAssertions.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.W)
    }

    @Test
    fun `should use Move as API`() {
        val startAssertions = CountDownLatch(1)
        var holder = Move(emptyList())
        val message = Move(listOf(Direction.E))

        waitingForConnection.onReceivedData {
            holder = it
            startAssertions.countDown()
        }
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

        waitingForConnection.onReceivedData {
            holder = it
            startAssertions.countDown()
        }
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

        connection.onReceivedData {
            holder = it
            startAssertion.countDown()
        }
        waitingForConnection.send(Move(listOf(Direction.W)))

        startAssertion.await()
        Assertions.assertThat(holder.move.size).isEqualTo(1)
        Assertions.assertThat(holder.move[0]).isEqualTo(Direction.W)
    }

    @Test
    fun `should received 3 moves`() {
        val startAssertion = CountDownLatch(1)
        val waitForFirstMessage = CountDownLatch(1)
        val waitForSecondMessage = CountDownLatch(1)
        var firstHolder = Move(emptyList())
        var secondHolder = Move(emptyList())
        var thirdHolder = Move(emptyList())

        waitingForConnection.onReceivedData {
            firstHolder = it
            waitForFirstMessage.countDown()
        }
        connection.send(Move(listOf(Direction.W)))
        waitForFirstMessage.await()
        waitingForConnection.onReceivedData {
            secondHolder = it
            waitForSecondMessage.countDown()
        }
        connection.send(Move(listOf(Direction.NW)))
        waitForSecondMessage.await()
        waitingForConnection.onReceivedData {
            thirdHolder = it
            startAssertion.countDown()
        }
        connection.send(Move(listOf(Direction.E, Direction.SE)))

        startAssertion.await()
        org.junit.jupiter.api.Assertions.assertAll(
            Executable { Assertions.assertThat(firstHolder.move.size).isEqualTo(1) },
            Executable { Assertions.assertThat(firstHolder.move[0]).isEqualTo(Direction.W) },

            Executable { Assertions.assertThat(secondHolder.move.size).isEqualTo(1) },
            Executable { Assertions.assertThat(secondHolder.move[0]).isEqualTo(Direction.NW) },

            Executable { Assertions.assertThat(thirdHolder.move.size).isEqualTo(2) },
            Executable { Assertions.assertThat(thirdHolder.move[0]).isEqualTo(Direction.E) },
            Executable { Assertions.assertThat(thirdHolder.move[1]).isEqualTo(Direction.SE) }
        )
    }

    @Test
    fun `should exchange 2 portions of move`() {
        val startAssertions = CountDownLatch(1)
        val waitForFirstMessage = CountDownLatch(1)
        val waitForSecondMessage = CountDownLatch(1)
        var firstHolder = Move(emptyList())
        var firstHolderAssertion = Move(emptyList())
        var secondHolder = Move(emptyList())
        var secondHolderAssertion = Move(emptyList())

        waitingForConnection.onReceivedData {
            firstHolder = it
            waitForFirstMessage.countDown()
        }
        connection.onReceivedData { firstHolderAssertion = it }
        connection.send(Move(listOf(Direction.W)))
        waitForFirstMessage.await()
        waitingForConnection.send(firstHolder)

        waitingForConnection.onReceivedData {
            secondHolder = it
            waitForSecondMessage.countDown()
        }
        connection.onReceivedData {
            secondHolderAssertion = it
            startAssertions.countDown()
        }
        connection.send(Move(listOf(Direction.NW, Direction.S)))
        waitForSecondMessage.await()
        waitingForConnection.send(secondHolder)

        startAssertions.await()
        org.junit.jupiter.api.Assertions.assertAll(
            Executable { Assertions.assertThat(firstHolderAssertion.move.size).isEqualTo(1) },
            Executable { Assertions.assertThat(firstHolderAssertion.move[0]).isEqualTo(Direction.W) },

            Executable { Assertions.assertThat(secondHolderAssertion.move.size).isEqualTo(2) },
            Executable { Assertions.assertThat(secondHolderAssertion.move[0]).isEqualTo(Direction.NW) },
            Executable { Assertions.assertThat(secondHolderAssertion.move[1]).isEqualTo(Direction.S) }
        )
    }
}
