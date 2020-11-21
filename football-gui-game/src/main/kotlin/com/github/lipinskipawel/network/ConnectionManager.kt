package com.github.lipinskipawel.network

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.time.Duration

class ConnectionManager {

    companion object {
        private const val SERVER_PORT: Int = 9679

        fun connectTo(ipAddress: InetAddress): Connection {
            return connectTo(ipAddress, Duration.ZERO)
        }

        fun connectTo(ipAddress: InetAddress, timeout: Duration): Connection {
            val socket = Socket()
            val socketAddress = InetSocketAddress(ipAddress, SERVER_PORT)
            socket.connect(socketAddress, timeout.toMillis().toInt())
            return connect(socket)
        }

        fun waitForConnection(): Connection {
            val serverSocket = ServerSocket(SERVER_PORT, 1, InetAddress.getLocalHost())
            val socket = serverSocket.accept()
            return connect(socket)
        }

        private fun connect(socket: Socket): Connection {
            return DirectConnection.of(socket)
        }
    }
}
