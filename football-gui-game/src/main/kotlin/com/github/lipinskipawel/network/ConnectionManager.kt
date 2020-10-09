package com.github.lipinskipawel.network

import java.net.InetAddress
import java.net.Socket

class ConnectionManager {

    companion object {
        fun connectTo(ipAddress: InetAddress, port: Int): Connection {
            return Connection(Socket(ipAddress.hostAddress, port))
        }
    }
}

class Connection(private val socket: Socket) : AutoCloseable {

    private val sendingStream = socket.getOutputStream()

    fun <T : Envelope> send(data: T) {
        sendingStream.write(data.toByteArray())
        sendingStream.flush()
    }

    override fun close() {
        socket.close()
    }
}
