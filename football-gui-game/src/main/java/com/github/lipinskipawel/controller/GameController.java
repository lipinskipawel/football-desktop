package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.GameDrawer;
import com.github.lipinskipawel.gui.Table;
import com.github.lipinskipawel.network.ConnectionChat;
import com.github.lipinskipawel.network.ConnectionHandler;
import com.github.lipinskipawel.network.ConnectionState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GameController implements MouseListener, Observer, ActionListener {

    private String gameState;
    private final Table table;

    private String ipEnemy;

    /**
     * These fields are used for steering flow of the game, so this is important
     * to set up these flags/values to default whenever restartBoard() is invoked.
     */
    private BoardInterface board;
    /**
     * This is to handle proper drawing board to the user. In one vs one and
     * one vs lan mode is also used to display proper winner of the game.
     */
    private Player playerView;

    /**
     * These threads are responsible for handles the connection.
     */
    private ConnectionHandler connectionHandler;
    private ConnectionChat connectionChat;

    private GameFlowController gameFlowController;
    private final WarmupController warmupController;
    private final OneVsOneController oneVsOneController;
    private final HellController hellController;
    private final OneVsAiController oneVsAiController;

    GameController(final Table table) {
        this.board = Boards.immutableBoard();
        this.table = table;
        this.table.drawBoard(this.board, this.board.getPlayer());
        this.table.activePlayer(this.board.getPlayer());
        this.gameState = table.getSTATE_OF_GAME();
        this.connectionHandler = null;
        this.connectionChat = null;
        this.playerView = FIRST;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.warmupController = new WarmupController(table);
        this.oneVsOneController = new OneVsOneController(table);
        this.hellController = new HellController(table);
        this.oneVsAiController = new OneVsAiController(table);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        switch (this.gameState) {
            case "warm-up" -> this.warmupController.onClick(e, src);
            case "1vs1" -> this.oneVsOneController.onClick(e, src);
            case "hell mode" -> this.hellController.onClick(e, src);
            case "1vsLAN" -> {
                try {
                    OneVsLANMode(e, src);
                    this.table.activePlayer(this.gameFlowController.player());
                } catch (InterruptedException e1) {
                    this.connectionHandler.close();
                    this.connectionChat.close();
                    //e1.printStackTrace();
                }
            }
            case "1vsAI" -> this.oneVsAiController.onClick(e, src);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == this.table.enterPressed()) {
            if (this.connectionChat != null &&
                    this.connectionChat.getConnectionState() == ConnectionState.CONNECTED)
                this.connectionChat.send(this.table.getText());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.gameState = table.getSTATE_OF_GAME();
        switch ((String) arg) {
            case "kill" -> {
                restartBoard();
                if (this.connectionHandler != null)
                    this.connectionHandler.close();
                if (this.connectionChat != null)
                    this.connectionChat.close();
            }
            case "createServer" -> {
                restartBoard();
                if (this.connectionHandler == null ||
                        this.connectionHandler.getConnectionState() == ConnectionState.CLOSED) {

                    this.playerView = this.board.getPlayer().opposite();
                    this.connectionHandler = new ConnectionHandler(this);
                    this.connectionChat = new ConnectionChat(this.table);
                }
            }
            case "createSocket" -> {
                restartBoard();
                if (this.connectionHandler == null ||
                        this.connectionHandler.getConnectionState() == ConnectionState.CLOSED) {

                    this.playerView = this.board.getPlayer();
                    this.connectionHandler = new ConnectionHandler(this, this.ipEnemy);
                    this.connectionChat = new ConnectionChat(this.table, this.ipEnemy);

                }
            }
        }
    }

    private void OneVsLANMode(final MouseEvent e, final Object src) throws InterruptedException {

        if (this.connectionHandler != null &&
                this.connectionHandler.getConnectionState() == ConnectionState.CONNECTED &&
                this.playerView == this.board.getPlayer()) {

            if (isRightMouseButton(e)) {

//                if (!endGame) {
//                    if (this.board.undoMove())
//                        this.table.drawBoard(this.board, this.playerView);
//                    else {
//                        JOptionPane.showMessageDialog(null,
//                                "You can't step back that far away!");
//                    }
//                } else
//                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
//                            " won the game.\nPlease start game again.");

            } else if (isLeftMouseButton(e)) {

                GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
                final var move = this.board.getBallAPI().kickBallTo(pointTracker.getPosition());

                if (!this.gameFlowController.isGameOver()) {
                    try {

                        if (this.board.isMoveAllowed(move)) {
                            this.board = this.board.executeMove(move);
                            this.table.drawBoard(this.board, this.playerView);
                            if (this.board.isGoal()) {
                                this.gameFlowController = this.gameFlowController.gameOver(this.board, true);
                                this.playerView = this.board.getPlayer();
                                JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                        " won the game.");
                                this.table.appendChat("Player " + this.playerView + " won the game.");
                                this.connectionHandler.send(this.board);
                                // this is to prevent executing if statement below. This isn't allow two time send winning board
                                this.playerView = this.board.getPlayer();
                            }
                            if (this.board.getPlayer() != this.playerView)
                                this.connectionHandler.send(this.board);
                        } else
                            JOptionPane.showMessageDialog(null, "You cannot move like that.");

                        if (this.board.allLegalMoves().size() == 0 && !this.board.isGoal()) {
                            this.gameFlowController = this.gameFlowController.gameOver(this.board, true);
                            this.playerView = this.board.getPlayer().opposite();
                            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                    " won the game.");
                            this.table.appendChat("Player " + this.playerView + " won the game.");
                            this.connectionHandler.send(this.gameFlowController.board());
                        }

                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                } else
                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                            " won the game.\nPlease start game again.");
            }
        }
    }

    void setIPEnemy(final String IPEnemy) {
        this.ipEnemy = IPEnemy;
    }

    private void restartBoard() {
        this.board = Boards.immutableBoard();
        this.table.drawBoard(this.board, this.playerView);
        this.table.activePlayer(this.board.getPlayer());
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.playerView = FIRST;
        this.warmupController.reset();
        this.oneVsOneController.reset();
        this.hellController.reset();
        this.oneVsAiController.reset();
    }

    public void postBoard(final BoardInterface board) {
        this.board = board;
        this.table.drawBoard(this.board, this.playerView);
        this.table.activePlayer(this.board.getPlayer());

        if (this.board.isGoal()) {
            this.gameFlowController = this.gameFlowController.gameOver(this.board, true);
            this.playerView = this.board.getPlayer();
            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                    " won the game.");
            this.table.appendChat("Player " + this.playerView + " won the game.");
        }

        //TODO handles if someone hits the corner
        //TODO point.isZeroMovesAvailable()
//        if (this.board.getPoint(this.board.getBallPosition()).isZeroMovesAvailable() &&
//                !this.board.isThisGoal(this.board.getPoint(this.board.getBallPosition()))) {
//            this.endGame = true;
//            this.playerView = this.board.getPlayer().opposite();
//            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
//                    " won the game.");
//            this.table.appendChat("Player " + this.playerView + " won the game.");
//        }
    }

    public void postMessage(final String message) {
        this.table.appendChat(message + "\n");
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
