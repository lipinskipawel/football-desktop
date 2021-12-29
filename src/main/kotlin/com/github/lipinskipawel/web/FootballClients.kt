package com.github.lipinskipawel.web

import com.github.lipinskipawel.board.engine.Move
import com.github.lipinskipawel.network.Connection
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.*
import java.util.function.Consumer
import javax.swing.DefaultListModel
import javax.swing.SwingUtilities

/**
 * This variable is used by API methods of this file. It holds a reference to connection to Football Server.
 */
private var lobbyConnection: Optional<FootballLobbyClient> = Optional.empty()
private lateinit var callbackOnRedirect: (RedirectEndpoint) -> Unit
private lateinit var redirect: RedirectEndpoint
private const val username = "football-desktop"

/**
 * This method is an API of this file.
 * This method will connect asynchronously to the Football Server.
 */
fun connectToLobby(
        listModel: DefaultListModel<String>,
        givenUsername: String = username,
        connectBlocking: Boolean = false,
        callback: (RedirectEndpoint) -> Unit = {}
) {
    lobbyConnection = Optional.of(FootballLobbyClient(URI.create("ws://localhost:8080/lobby"), listModel))
    lobbyConnection.get().addHeader("cookie", givenUsername)
    if (connectBlocking) {
        lobbyConnection.get().connectBlocking()
    } else {
        lobbyConnection.get().connect()
    }
    callbackOnRedirect = callback
}

/**
 * This method is an API of this file.
 * This method will try to send an [Player] object only when a connection has been established, and it's to the /lobby
 * endpoint.
 * Connection can be established only by calling [connectToLobby] method.
 *
 * @param username is the username of the opponent which current user will be playing with
 */
fun sendRequestToPlay(username: String) {
    lobbyConnection.ifPresent { it.send(Player(username)) }
}

/**
 * This method is an API of this file.
 * This method will establish a connection with the Football Server to play a game. This method must be called after the
 * successful connection to the lobby and after choosing opponent to play. See [connectToLobby] and [sendRequestToPlay].
 *
 * @param givenUsername is a string representing current player username
 * @param connectBlocking indicates whether establishing a connection to the Football Server should block
 * @param onData is a callback that will be triggered on every received message
 *
 * @return Pair<Connection, Boolean> Connection is a connection to the Football Server. Boolean is a
 * marker whether client should move first
 */
fun connectToGame(
        givenUsername: String = username,
        connectBlocking: Boolean = false,
        onData: (Move) -> Unit = {}
): Pair<Connection, Boolean> {
    val footballClient = FootballGameClient(URI.create("ws://localhost:8080${redirect.redirectEndpoint}"))
    footballClient.addHeader("cookie", givenUsername)
    if (connectBlocking) {
        footballClient.connectBlocking()
    } else {
        footballClient.connect()
    }
    footballClient.onReceivedData(onData)
    return Pair(footballClient, redirect.first.username == givenUsername)
}

/**
 * Private helper class that will connect to /lobby endpoint of Football Server instance.
 */
private class FootballLobbyClient(
        serverUri: URI,
        private val listModel: DefaultListModel<String>
) : WebSocketClient(serverUri) {
    private val parser: Gson = Gson()

    fun send(playerToPlay: Player) {
        send(parser.toJson(RequestToPlay(playerToPlay)))
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("onOpen")
    }

    override fun onMessage(message: String?) {
        val response = parseResponse(message)
        when (response::class.java) {
            ListOfPlayers::class.java -> handleListOfPlayers(response as ListOfPlayers)
            RedirectEndpoint::class.java -> handleRedirectEndpoint(response as RedirectEndpoint)
            else -> println("not implemented $response")
        }
    }

    private fun parseResponse(json: String?): Any {
        val parsed = parser.fromJson<ListOfPlayers>(json, ListOfPlayers::class.java)
        return try {
            val validationOfParser = parsed.players.size
            parsed
        } catch (ee: NullPointerException) {
            parser.fromJson<RedirectEndpoint>(json, RedirectEndpoint::class.java)
        }
    }

    private fun handleListOfPlayers(players: ListOfPlayers) {
        println("ListOfPlayers: $players")
        SwingUtilities.invokeLater {
            listModel.removeAllElements()
            listModel.addAll(players.players.map { it.username })
        }
    }

    private fun handleRedirectEndpoint(redirectEndpoint: RedirectEndpoint) {
        println("RedirectEndpoint: $redirectEndpoint")
        redirect = redirectEndpoint
        callbackOnRedirect(redirectEndpoint)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("onClose: ${this::class.java}")
    }

    override fun onError(ex: java.lang.Exception?) {
        println("onError")
    }
}

private class FootballGameClient(
        serverUri: URI,
) : WebSocketClient(serverUri), Connection {
    private val parser: Gson = Gson()
    private var onData: Consumer<Move> = Consumer { }

    /**
     * Sends given [move] to the Football Server.
     * Before sending, it converts given list of strings [move] to [GameMove] object that Football Server understands.
     *
     * @param move should contain [N, NE, E, SE, S, SW, W, NW]
     */
    fun send(move: List<String>) {
        val json = parser.toJson(GameMove(move))
        send(json)
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        println("onOpen ${this::class.java}")
    }

    override fun onMessage(message: String) {
        println("onMessage ${this::class.java}")
        val move = parser.fromJson<Move>(message, Move::class.java)
        if (isThisAMove(move)) {
            this.onData.accept(move)
        }
    }

    /**
     * This method tries to ensure that parsing of the message to the [Move] was successful.
     */
    private fun isThisAMove(move: Move): Boolean {
        return try {
            val size = move.move.size
            true
        } catch (error: RuntimeException) {
            println("Move has not been given.")
            false
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println("onClose ${this::class.java}")
    }

    override fun onError(ex: Exception?) {
        println("onError ${this::class.java}")
    }

    override fun send(move: Move) {
        val json = parser.toJson(GameMove(move.move.map { it.toString() }))
        send(json)
    }

    override fun onReceivedData(onReceivedData: Consumer<Move>) {
        onData = onReceivedData
    }
}

/**
 * Those are private data classes used to interact with the Football Server.
 */
data class Player(val username: String)
private data class ListOfPlayers(val players: List<Player>)
private data class RequestToPlay(val opponent: Player)
data class RedirectEndpoint(val redirectEndpoint: String, val first: Player, val second: Player)
private data class GameMove(private val move: List<String>)
