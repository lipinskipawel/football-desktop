package com.github.lipinskipawel.network

import io.github.lipinskipawel.board.engine.Move
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Consumer

internal class DirectConnection private constructor(private val socket: Socket) : Connection {

    private val sendingStream = socket.getOutputStream()
    private val readingStream = socket.getInputStream()

    private var onData: Consumer<Move> = Consumer { }
    private lateinit var readerTask: Future<*>

    companion object {
        fun of(socket: Socket): Connection {
            val pool = Executors.newSingleThreadExecutor()
            val connection = DirectConnection(socket)
            connection.readerTask = pool.submit(connection.ReadingDataThroughSocket())
            pool.shutdown()
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
        readerTask.cancel(true)
        socket.close()
    }

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
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
