package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Direction;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.DrawableFootballPitch;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.gui.Table;
import com.github.lipinskipawel.gui.UserDialogPresenter;
import com.github.lipinskipawel.network.Connection;
import com.github.lipinskipawel.network.ConnectionManager;
import kotlin.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.joining;

final class OneVsLanController implements PitchController {

    private static final Logger logger = LoggerFactory.getLogger(OneVsLanController.class);
    private static final int SERVER_PORT = 6090;
    private final DrawableFootballPitch drawableFootballPitch;
    private final UserDialogPresenter dialogPresenter;
    private final AtomicReference<GameFlowController> gameFlowController;
    /**
     * This player is set based on the chosen policy.
     * Player can chose either waiting policy (server) or
     * active one (connecting).
     */
    private Player currentPlayer;
    private Connection connection;

    public OneVsLanController(final DrawableFootballPitch drawableFootballPitch,
                              final UserDialogPresenter userDialogPresenter) {
        this.drawableFootballPitch = drawableFootballPitch;
        this.dialogPresenter = userDialogPresenter;
        this.gameFlowController = new AtomicReference<>(new GameFlowController(Boards.immutableBoard(), false));
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
        logger.info("left click");
        if (this.gameFlowController.get().isGameOver()) {
            return;
        }
        if (canMove(this.gameFlowController.get().player())) {
            this.gameFlowController.updateAndGet(game -> game.makeAMove(renderablePoint.getPosition()));
            if (!canMove(this.gameFlowController.get().player())) {
                final var moves = this.gameFlowController.get().board().moveHistory();
                final var lastMove = moves.get(moves.size() - 1);
                final var lastMoveInString = lastMove
                        .getMove()
                        .stream()
                        .map(Direction::toString)
                        .collect(joining(", "));
                logger.info("sending move " + lastMoveInString);
                this.gameFlowController.get().onWinner(this::winningMessage);
                this.connection.send(lastMove);
            }
            logger.info("Drawing the move");
            this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), this.currentPlayer);
        } else {
            this.dialogPresenter.showMessage(null, "You can not make move right now.\nWait for you enemy move");
        }
    }

    private boolean canMove(final Player player) {
        return this.currentPlayer == player;
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
        if (this.gameFlowController.get().isGameOver()) {
            return;
        }
        if (canMove(this.gameFlowController.get().player())) {
            this.gameFlowController.updateAndGet(GameFlowController::undoOnlyCurrentPlayerMove);
            this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), this.currentPlayer);
        } else {
            this.dialogPresenter.showMessage(null, "You can not undo move.\nYour opponent is making a move.");
        }
    }

    /**
     * Starting the server and starting to listen on localhost port {@link OneVsLanController#SERVER_PORT}.
     * This method deals with the received data and mutates the {@link GameFlowController} and
     * invokes the {@link Table#drawBoard(BoardInterface, Player)}.
     */
    void startServer() {
        final var pool = Executors.newSingleThreadExecutor();
        pool.submit(() -> {
            this.connection = ConnectionManager.Companion.waitForConnection(SERVER_PORT);
            this.connection.onReceivedData(this::consumeTheMoveFromConnection);
        });
        pool.shutdown();
        this.currentPlayer = Player.SECOND;
    }

    private void consumeTheMoveFromConnection(final Move move) {
        if (!this.gameFlowController.get().isGameOver() &&
                canMove(this.gameFlowController.get().player())) {
            final var movesInString = move
                    .getMove()
                    .stream()
                    .map(Direction::toString)
                    .collect(joining(", "));
            logger.info("consuming move from socket: " + movesInString);
            this.gameFlowController.updateAndGet(game -> game.makeAMove(move));
            if (SwingUtilities.isEventDispatchThread()) {
                logger.info("consuming move - drawing");
                this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), this.gameFlowController.get().player());
                this.gameFlowController.get().onWinner(this::winningMessage);
            } else {
                logger.info("consuming move - drawing");
                SwingUtilities.invokeLater(() -> {
                    this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), this.gameFlowController.get().player());
                    this.gameFlowController.get().onWinner(this::winningMessage);
                });
            }
        }
    }

    void connectToServer(final InetAddress ipEnemy) {
        this.currentPlayer = Player.FIRST;
        this.connection = ConnectionManager.Companion.connectTo(ipEnemy, SERVER_PORT);
        this.connection.onReceivedData(this::consumeTheMoveFromConnection);
    }

    void reset() {
        this.gameFlowController.updateAndGet(game -> new GameFlowController(Boards.immutableBoard(), false));
        this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), this.gameFlowController.get().player());
//        this.drawableFootballPitch.activePlayer(this.gameFlowController.player());
        if (this.connection != null) {
            this.connection.close();
        }
    }

    private Unit winningMessage(final Player winner) {
        this.dialogPresenter.showMessage(null, "Player " + winner + " won he game.");
        return null;
    }
}
