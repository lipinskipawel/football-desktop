package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.gui.Table;
import com.github.lipinskipawel.network.Connection;
import com.github.lipinskipawel.network.ConnectionManager;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

final class OneVsLanController implements PitchController {

    private static final int SERVER_PORT = 6090;
    private final Table table;
    private GameFlowController gameFlowController;
    private Connection connection;
    private Socket socket;

    public OneVsLanController(final Table table) {
        this.table = table;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
    }

    /**
     * Starting the server and starting to listen on localhost port {@link OneVsLanController#SERVER_PORT}.
     * This method deals with the received data and mutates the {@link GameFlowController} and
     * invokes the {@link Table#drawBoard(BoardInterface, Player)}.
     */
    void startServer() {
        final var executor = Executors.newSingleThreadExecutor();
        executor
                .submit(() -> {
                    try {
                        connection = ConnectionManager.Companion
                                .waitForConnection(SERVER_PORT)
                                .get();
                        connection.onReceivedData(this::consumeTheMoveFromConnection);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
        executor.shutdown();
    }

    private void consumeTheMoveFromConnection(final Move move) {
        move.getMove().forEach(System.out::println);
        this.gameFlowController = this.gameFlowController.makeAMove(move);
        this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player());
    }

    void connectToServer(final String ipEnemy) {
        try {
            this.socket = new Socket(ipEnemy, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void reset() {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player());
        this.table.activePlayer(this.gameFlowController.player());
        if (this.connection != null) {
            this.connection.close();
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
