package com.github.lipinskipawel.network.support

import java.net.Socket

internal class SendSupport(host: String, port: Int) {

    private val socket: Socket = Socket(host, port)

    fun send(data: String) {
        socket.getOutputStream().write(data.toByteArray())
        socket.getOutputStream().flush()
        socket.close()
    }
}
