package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Move
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

internal class WaitingForConnection private constructor(port: Int) : Connection {

    private var server: ServerSocket = ServerSocket(port, 1, InetAddress.getLocalHost())
    private var socket: Socket = Socket()
    private val pool: ExecutorService = Executors.newFixedThreadPool(1)

    private var callback: Consumer<Move> = Consumer { }

    companion object {
        fun waitOnPort(port: Int): WaitingForConnection {
            val x = WaitingForConnection(port)
            x.pool.submit(x.ReadingDataThroughSocket())
            return x
        }
    }

    override fun send(move: Move) {
        this.socket.getOutputStream().write(Envelope.of(move).toByteArray())
        this.socket.getOutputStream().flush()
    }

    override fun onReceivedData(onReceivedData: Consumer<Move>) {
        callback = onReceivedData
    }

    override fun close() {
        server.close()
        pool.shutdown()
    }


    private inner class ReadingDataThroughSocket : Runnable {
        override fun run() {
            socket = server.accept()
            while (!Thread.currentThread().isInterrupted) {
                val headData = ByteArray(2)
                val bytesRead = socket.getInputStream().read(headData)
                if (bytesRead != -1) {
                    val length = headData[1]
                    val dataBuffer = ByteArray(length.toInt())
                    socket.getInputStream().read(dataBuffer)
                    callback.accept(Envelope.toMove(dataBuffer))
                }
            }
        }
    }
}

