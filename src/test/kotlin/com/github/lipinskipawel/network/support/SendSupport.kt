package com.github.lipinskipawel.network.support

import java.net.Socket

internal class SendSupport(host: String, port: Int) {

    private val socket: Socket = Socket(host, port)

    fun send(data: ByteArray) {
        socket.getOutputStream().write(data)
        socket.getOutputStream().flush()
        socket.close()
    }
}
