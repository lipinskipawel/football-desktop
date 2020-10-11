package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Move
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

    fun send(move: Move) {
        sendingStream.write(Envelope.of(move).toByteArray())
        sendingStream.flush()
    }

    override fun close() {
        socket.close()
    }
}
