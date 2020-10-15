package com.github.lipinskipawel.network

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.time.Duration

class ConnectionManager {

    companion object {
        fun connectTo(ipAddress: InetAddress, port: Int): Connection {
            return connectTo(ipAddress, port, Duration.ZERO)
        }

        fun connectTo(ipAddress: InetAddress, port: Int, timeout: Duration): Connection {
            val socket = Socket()
            val socketAddress = InetSocketAddress(ipAddress, port)
            socket.connect(socketAddress, timeout.toMillis().toInt())
            return connect(socket)
        }

        fun waitForConnection(port: Int): Connection {
            val serverSocket = ServerSocket(port, 1, InetAddress.getLocalHost())
            val socket = serverSocket.accept()
            return connect(socket)
        }

        private fun connect(socket: Socket): Connection {
            return DirectConnection.of(socket)
        }
    }
}
