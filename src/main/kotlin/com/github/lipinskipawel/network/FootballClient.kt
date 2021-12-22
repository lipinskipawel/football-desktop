package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Move
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.function.Consumer

class FootballClient(serverUri: URI?) : Connection {
    private val webSocketClient: WebSocketClientWrapper = WebSocketClientWrapper(serverUri)

    fun connectBlocking() {
        this.webSocketClient.connectBlocking()
    }

    override fun close() {
        webSocketClient.closeBlocking()
    }

    override fun isOpen(): Boolean = this.webSocketClient.isOpen

    override fun send(move: Move) {
        webSocketClient.send(move.toString())
    }

    override fun onReceivedData(onReceivedData: Consumer<Move>) {
        TODO("Not yet implemented")
    }
}

private class WebSocketClientWrapper(
        serverUri: URI?
) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("Open: $handshakedata")
    }

    override fun onMessage(message: String) {
        println("Message: $message")
//        onMessageCallback.accept(message)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("Close connection with code: $code, reason: $reason, remote: $remote")
    }

    override fun onError(ex: Exception?) {
        println("Error $ex")
        val stacktrace = ex?.stackTrace
        stacktrace?.forEach { println(it) }
        println(ex?.cause)
    }
}
