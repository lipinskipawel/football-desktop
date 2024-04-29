package com.github.lipinskipawel.network

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.*
import java.time.Duration

class ConnectionManager {

    companion object {
        private const val SERVER_PORT: Int = 9679
        private val LOGGER: Logger = LoggerFactory.getLogger(ConnectionManager::class.java)

        fun connectTo(ipAddress: InetAddress): Connection {
            return connectTo(ipAddress, Duration.ZERO)
        }

        fun connectTo(ipAddress: InetAddress, timeout: Duration): Connection {
            val socket = Socket()
            val socketAddress = InetSocketAddress(ipAddress, SERVER_PORT)
            socket.connect(socketAddress, timeout.toMillis().toInt())
            return connect(socket)
        }

        fun waitForConnection(interAddress: InetAddress): Connection {
            val serverSocket = ServerSocket(SERVER_PORT, 1, interAddress)
            val socket = serverSocket.accept()
            LOGGER.info("Connection accepted.")
            serverSocket.close()
            return connect(socket)
        }

        fun getInetAddress(): InetAddress {
            val socket = Socket()
            return socket.use {
                it.connect(InetSocketAddress("google.com", 80))
                it.localAddress
            }
        }

        private fun connect(socket: Socket): Connection {
            return DirectConnection.of(socket)
        }
    }
}
