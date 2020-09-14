package com.github.lipinskipawel.network

import java.io.Closeable
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Server private constructor(port: Int) : Closeable {

    private val server: ServerSocket = ServerSocket(port, 1, InetAddress.getLocalHost())
    private val pool: ExecutorService = Executors.newFixedThreadPool(1)

    private val callbacks = mutableListOf<(String) -> Unit>()

    companion object {
        fun createServer(port: Int): Server {
            val x = Server(port)
            x.pool.submit(x.BackgroundListener())
            return x
        }
    }

    fun onReceived(block: (String) -> Unit): Server {
        callbacks.add(block)
        return this
    }

    override fun close() {
        server.close()
        pool.shutdown()
    }


    inner class BackgroundListener : Runnable {
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                val line = server.accept().getInputStream().bufferedReader().readLine()
                if (line != null) {
                    callbacks.forEach { it(line) }
                } else {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }
}

