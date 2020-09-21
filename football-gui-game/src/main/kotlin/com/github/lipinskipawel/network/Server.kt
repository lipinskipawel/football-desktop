package com.github.lipinskipawel.network

import java.io.Closeable
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Server private constructor(port: Int) : Closeable {

    private val server: ServerSocket = ServerSocket(port, 1, InetAddress.getLocalHost())
    private val pool: ExecutorService = Executors.newFixedThreadPool(1)

    private var callback: (ByteArray) -> Unit = { }

    companion object {
        fun createServer(port: Int): Server {
            val x = Server(port)
            x.pool.submit(x.BackgroundListener())
            return x
        }
    }

    fun onReceived(block: (ByteArray) -> Unit): Server {
        callback = block
        return this
    }

    override fun close() {
        server.close()
        pool.shutdown()
    }


    inner class BackgroundListener : Runnable {
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                val bytes = server.accept().getInputStream().readNBytes(1024)
                callback(bytes)
            }
        }
    }
}

