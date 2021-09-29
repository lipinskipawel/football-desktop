package com.github.lipinskipawel.network

import com.github.lipinskipawel.board.engine.Move
import java.util.function.Consumer

/**
 * This class is a common abstraction representing the Connection between two clients.
 * Any instances of this class has to be Thread safe.
 * Instances of this class can be obtain by the methods in [ConnectionManager].
 */
interface Connection : AutoCloseable {

    /**
     * This method is responsible for sending the [Move] through the connection.
     */
    fun send(move: Move)

    /**
     * This method will register a callback [onReceivedData] function which will be then
     * invoked whether Move will be received on the Connection.
     */
    fun onReceivedData(onReceivedData: Consumer<Move>)

    /**
     * This method is responsible for closing [Connection].
     */
    override fun close()

    /**
     * Checks whether the connection between clients is open.
     */
    fun isOpen(): Boolean
}
