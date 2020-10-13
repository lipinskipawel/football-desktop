package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Move
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

internal class DirectConnection private constructor(private val socket: Socket) : Connection {

    private val pool: ExecutorService = Executors.newSingleThreadExecutor()
    private val sendingStream = socket.getOutputStream()
    private val readingStream = socket.getInputStream()

    private var onData: Consumer<Move> = Consumer { }

    companion object {
        fun of(socket: Socket): Connection {
            val connection = DirectConnection(socket)
            connection.pool.submit(connection.ReadingDataThroughSocket())
            return connection
        }
    }

    override fun send(move: Move) {
        sendingStream.write(Envelope.of(move).toByteArray())
        sendingStream.flush()
    }

    override fun onReceivedData(onReceivedData: Consumer<Move>) {
        this.onData = onReceivedData
    }

    override fun close() {
        socket.close()
    }

    private inner class ReadingDataThroughSocket : Runnable {
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                val headData = ByteArray(2)
                val bytesRead = readingStream.read(headData)
                if (bytesRead != -1) {
                    val length = headData[1]
                    val dataBuffer = ByteArray(length.toInt())
                    readingStream.read(dataBuffer)
                    onData.accept(Envelope.toMove(dataBuffer))
                }
            }
        }
    }
}
