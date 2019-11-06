package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.BruteForceThinking;
import com.github.lipinskipawel.board.ai.bruteforce.MiniMax;
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
import java.util.concurrent.ExecutionException;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GameController implements MouseListener, Observer, ActionListener {

    private BoardInterface board;
    private String gameState;
    private final Table table;

    private String ipEnemy;

    /**
     * These fields are used for steering flow of the game, so this is important
     * to set up these flags/values to default whenever restartBoard() is invoked.
     */
    private boolean endGame;
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

    private boolean canHumanMove;


    GameController(final Table table) {
        this.board = Boards.immutableBoard();
        this.table = table;
        this.table.drawBoard(this.board, this.board.getPlayer());
        this.table.activePlayer(this.board.getPlayer());
        this.gameState = table.getSTATE_OF_GAME();
        this.connectionHandler = null;
        this.connectionChat = null;
        this.endGame = false;
        this.playerView = Player.FIRST;
        this.canHumanMove = true;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        switch (this.gameState) {
            case "warm-up":
                warmUpMode(e, src);
                break;
            case "1vs1":
                OneVsOneMode(e, src);
                this.table.activePlayer(this.board.getPlayer());
                break;
            case "1vsLAN":
                try {
                    OneVsLANMode(e, src);
                    this.table.activePlayer(this.board.getPlayer());
                } catch (InterruptedException e1) {
                    this.connectionHandler.close();
                    this.connectionChat.close();
                    //e1.printStackTrace();
                }
                break;
            case "1vsAI":
                OneVsAIMode(e, src);
                break;
            default:
                System.out.println("Something goes wrong!");
                break;
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

            case "kill":
                restartBoard();
                if (this.connectionHandler != null)
                    this.connectionHandler.close();
                if (this.connectionChat != null)
                    this.connectionChat.close();
                break;

            case "createServer":
                restartBoard();
                if (this.connectionHandler == null ||
                        this.connectionHandler.getConnectionState() == ConnectionState.CLOSED) {

                    this.playerView = this.board.getPlayer().opposite();
                    this.connectionHandler = new ConnectionHandler(this);
                    this.connectionChat = new ConnectionChat(this.table);

                }

                break;

            case "createSocket":
                restartBoard();
                if (this.connectionHandler == null ||
                        this.connectionHandler.getConnectionState() == ConnectionState.CLOSED) {

                    this.playerView = this.board.getPlayer();
                    this.connectionHandler = new ConnectionHandler(this, this.ipEnemy);
                    this.connectionChat = new ConnectionChat(this.table, this.ipEnemy);

                }

                break;
            default:
                break;
        }

    }

    private void warmUpMode(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

            this.board = this.board.undo();
            this.table.drawBoard(this.board, this.playerView);

        } else if (isLeftMouseButton(e)) {

            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
            try {
                final var move = this.board.getBallAPI().kickBallTo(pointTracker.getPosition());


                if (this.board.isMoveAllowed(move)) {
                    this.board = this.board.executeMove(move);
                    this.table.drawBoard(this.board, this.playerView);
                    if (this.board.isGoal())
                        JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                " won the game.");
                } else
                    JOptionPane.showMessageDialog(null, "You cannot move like that.");
            } catch (RuntimeException ee) {
                JOptionPane.showMessageDialog(null, "You can't goes on already made moves");
                return;
            }


        }
    }

    private void OneVsOneMode(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

            final var afterUndo = this.board.undo();
            if (afterUndo.getPlayer() == this.board.getPlayer()) {
                this.board = afterUndo;
                this.table.drawBoard(this.board, this.board.getPlayer());
            } else {
                JOptionPane.showMessageDialog(null, "To be implemented");
                return;
            }

        } else if (isLeftMouseButton(e)) {

            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
            final var move = this.board.getBallAPI().kickBallTo(pointTracker.getPosition());


            if (!endGame) {
                if (this.board.isMoveAllowed(move)) {
                    this.board = this.board.executeMove(move);
                    this.table.drawBoard(this.board, this.board.getPlayer());
                    if (this.board.isGoal()) {
                        endGame = true;
                        this.playerView = this.board.getPlayer();
                        JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                " won the game.");
                        this.table.appendRight("Player " + this.playerView + " won the game.");
                    }

                } else
                    JOptionPane.showMessageDialog(null, "You cannot move like that.");

                if (this.board.allLegalMoves().size() == 0 && !this.board.isGoal()) {
                    this.endGame = true;
                    this.playerView = this.board.getPlayer().opposite();
                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                            " won the game.");
                    this.table.appendRight("Player " + this.playerView + " won the game.");
                }

            } else
                JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                        " won the game.\nPlease start game again.");
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


                if (!endGame) {
                    try {

                        if (this.board.isMoveAllowed(move)) {
                            this.board = this.board.executeMove(move);
                            this.table.drawBoard(this.board, this.playerView);
                            if (this.board.isGoal()) {
                                endGame = true;
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
                            this.endGame = true;
                            this.playerView = this.board.getPlayer().opposite();
                            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                    " won the game.");
                            this.table.appendChat("Player " + this.playerView + " won the game.");
                            this.connectionHandler.send(this.board);
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

    // TODO this is warmup
    private void OneVsAIMode(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

//            if (this.board.undoMove())
//                this.table.drawBoard(this.board, this.playerView);
//            else {
//                if (this.board.undoMove(true))
//                    this.table.drawBoard(this.board, this.playerView);
//            }

        } else if (isLeftMouseButton(e)) {

            if (this.canHumanMove) {
                GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
                try {
                    final var move = this.board.getBallAPI().kickBallTo(pointTracker.getPosition());

                    if (this.board.isMoveAllowed(move)) {
                        this.board = this.board.executeMove(move);
                        this.table.drawBoard(this.board, this.playerView);
                        if (this.board.isGoal()) {
                            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                    " won the game.");
                        }
                        if (this.board.getPlayer() == Player.SECOND) {
                            this.canHumanMove = false;
                        }
                    } else
                        JOptionPane.showMessageDialog(null, "You cannot move like that.");
                } catch (RuntimeException ee) {
                    JOptionPane.showMessageDialog(null, "You can't goes on already made moves");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(null, "There is AI to move");
            }
            if (!canHumanMove) {
                try {
                    final var bruteForce = new BruteForceThinking(new MiniMax(), this.board, 2);
                    bruteForce.execute();
                    final var aiMove = bruteForce.get();
                    this.board = this.board.executeMove(aiMove);
                    if (this.board.allLegalMoves().size() == 0) {
                        this.table.drawBoard(this.board, this.board.getPlayer().opposite());
                        JOptionPane.showMessageDialog(null, "You won the game!!!");
                        this.canHumanMove = false;
                        return;
                    }
                    this.table.drawBoard(this.board, this.board.getPlayer());
                    this.canHumanMove = true;
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
            }

        }
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

    void setIPEnemy(final String IPEnemy) {
        this.ipEnemy = IPEnemy;
    }

    private void restartBoard() {
        this.board = Boards.immutableBoard();
        this.table.drawBoard(this.board, this.playerView);
        this.table.activePlayer(this.board.getPlayer());
        this.endGame = false;
        this.playerView = Player.FIRST;
    }

    public void postBoard(final BoardInterface board) {
        this.board = board;
        this.table.drawBoard(this.board, this.playerView);
        this.table.activePlayer(this.board.getPlayer());

        if (this.board.isGoal()) {
            endGame = true;
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
}
