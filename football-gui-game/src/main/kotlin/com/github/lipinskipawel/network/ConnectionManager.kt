package com.github.lipinskipawel.network

import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.CompletableFuture

class ConnectionManager {

    companion object {
        fun connectTo(ipAddress: InetAddress, port: Int): Connection {
            return DirectConnection.of(Socket(ipAddress.hostAddress, port))
        }

        fun waitForConnection(port: Int): CompletableFuture<Connection> {
            val server = WaitingForConnection.waitOnPort(port)
            return CompletableFuture.completedFuture(server)
        }
    }
}
