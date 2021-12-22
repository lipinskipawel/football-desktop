package com.github.lipinskipawel

import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.swing.DefaultListModel
import javax.swing.SwingUtilities

/**
 * This method will connect asynchronous to the Football Server.
 */
fun connectToLobby(listModel: DefaultListModel<String>) {
    val lobbyClient = FootballLobbyClient(URI.create("ws://localhost:8080/lobby"), listModel)
    lobbyClient.addHeader("cookie", "football-desktop")
    lobbyClient.connect()
}

private class FootballLobbyClient(
        serverUri: URI,
        private val listModel: DefaultListModel<String>
) : WebSocketClient(serverUri) {
    private val parser: Gson = Gson()

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("onOpen")
    }

    override fun onMessage(message: String?) {
        println("Got message: $message")
        val parsedResponse = parser.fromJson<ListOfPlayers>(message, ListOfPlayers::class.java)
        SwingUtilities.invokeLater {
            listModel.removeAllElements()
            listModel.addAll(parsedResponse.players.map { it.username })
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("onClose")
    }

    override fun onError(ex: java.lang.Exception?) {
        println("onError")
    }
}

private data class Player(val username: String)
private data class ListOfPlayers(val players: List<Player>)