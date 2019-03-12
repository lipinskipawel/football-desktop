package io.lipinski.controller;

import io.lipinski.gui.GameDrawer;
import io.lipinski.gui.Table;
import io.lipinski.network.ConnectionHandler;
import io.lipinski.network.ConnectionState;
import io.lipinski.board.Board;
import io.lipinski.board.BoardInterface;
import io.lipinski.board.Point;
import io.lipinski.network.ConnectionChat;
//import io.lipinski.player.BoardTransition;
//import io.lipinski.player.MiniMax;
import io.lipinski.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

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


    GameController(final Table table) {
        this.board = new Board();
        this.table = table;
        this.table.drawBoard(this.board, this.board.getCurrentPlayer());
        this.table.activePlayer(this.board.getCurrentPlayer());
        this.gameState = table.getSTATE_OF_GAME();
        this.connectionHandler = null;
        this.connectionChat = null;
        this.endGame = false;
        this.playerView = Player.FIRST;
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
                this.table.activePlayer(this.board.getCurrentPlayer());
                break;
            case "1vsLAN":
                try {
                    OneVsLANMode(e, src);
                    this.table.activePlayer(this.board.getCurrentPlayer());
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

                    this.playerView = this.board.getOppositePlayer();
                    this.connectionHandler = new ConnectionHandler(this);
                    this.connectionChat = new ConnectionChat(this.table);

                }

                break;

            case "createSocket":
                restartBoard();
                if (this.connectionHandler == null ||
                        this.connectionHandler.getConnectionState() == ConnectionState.CLOSED) {

                    this.playerView = this.board.getCurrentPlayer();
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

            if (this.board.undoMove())
                this.table.drawBoard(this.board, this.playerView);
            else {
                if (this.board.undoMove(true))
                    this.table.drawBoard(this.board, this.playerView);
            }

        } else if (isLeftMouseButton(e)) {

            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
            Point point = this.board.getPoint(pointTracker.getPosition());



            if (this.board.tryMakeMove(point)) {
                this.table.drawBoard(this.board, this.playerView);
                if (this.board.isThisGoal(point))
                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                            " won the game.");
            } else
                JOptionPane.showMessageDialog(null, "You cannot move like that.");
        }

    }
    private void OneVsOneMode(final MouseEvent e, final Object src) {
         if (isRightMouseButton(e)) {

             if (!endGame) {
                 if (this.board.undoMove())
                     this.table.drawBoard(this.board, this.board.getCurrentPlayer());
                 else {
                     JOptionPane.showMessageDialog(null,
                             "You can't step back that far away!");
                 }
             }
             else
                 JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                         " won the game.\nPlease start game again.");

         } else if (isLeftMouseButton(e)) {

             GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
             Point point = this.board.getPoint(pointTracker.getPosition());


             if (!endGame) {
                 if (this.board.tryMakeMove(point)) {
                     this.table.drawBoard(this.board, this.board.getCurrentPlayer());
                     if (this.board.isThisGoal(point)) {
                         endGame = true;
                         this.playerView = this.board.winnerIs(this.board.getBallPosition());
                         JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                 " won the game.");
                         this.table.appendRight("Player " + this.playerView + " won the game.");
                     }

                 } else
                     JOptionPane.showMessageDialog(null, "You cannot move like that.");

                 if (point.isZeroMovesAvailable() && !this.board.isThisGoal(point)) {
                     this.endGame = true;
                     this.playerView = this.board.getOppositePlayer();
                     JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                             " won the game.");
                     this.table.appendRight("Player " + this.playerView + " won the game.");
                 }

             }
             else
                 JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                         " won the game.\nPlease start game again.");
         }
    }
    private void OneVsLANMode(final MouseEvent e, final Object src) throws InterruptedException {

        if (this.connectionHandler != null &&
                this.connectionHandler.getConnectionState() == ConnectionState.CONNECTED &&
                this.playerView == this.board.getCurrentPlayer()) {

            if (isRightMouseButton(e)) {

                if (!endGame) {
                    if (this.board.undoMove())
                        this.table.drawBoard(this.board, this.playerView);
                    else {
                        JOptionPane.showMessageDialog(null,
                                "You can't step back that far away!");
                    }
                }
                else
                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                            " won the game.\nPlease start game again.");

            } else if (isLeftMouseButton(e)) {

                GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
                Point point = this.board.getPoint(pointTracker.getPosition());


                if (!endGame) {
                    try {

                        if (this.board.tryMakeMove(point)) {
                            this.table.drawBoard(this.board, this.playerView);
                            if (this.board.isThisGoal(point)) {
                                endGame = true;
                                this.playerView = this.board.winnerIs(this.board.getBallPosition());
                                JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                        " won the game.");
                                this.table.appendChat("Player " + this.playerView + " won the game.");
                                this.connectionHandler.send(this.board);
                                // this is to prevent executing if statement below. This isn't allow two time send winning board
                                this.playerView = this.board.getCurrentPlayer();
                            }
                            if (this.board.getCurrentPlayer() != this.playerView)
                                this.connectionHandler.send(this.board);
                        } else
                            JOptionPane.showMessageDialog(null, "You cannot move like that.");

                        if (point.isZeroMovesAvailable() && !this.board.isThisGoal(point)) {
                            this.endGame = true;
                            this.playerView = this.board.getOppositePlayer();
                            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                                    " won the game.");
                            this.table.appendChat("Player " + this.playerView + " won the game.");
                            this.connectionHandler.send(this.board);
                        }

                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                }
                else
                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                            " won the game.\nPlease start game again.");
            }
        }

    }
   // TODO this is warmup
    private void OneVsAIMode(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

            if (this.board.undoMove())
                this.table.drawBoard(this.board, this.playerView);
            else {
                if (this.board.undoMove(true))
                    this.table.drawBoard(this.board, this.playerView);
            }

        } else if (isLeftMouseButton(e)) {

            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
            Point point = this.board.getPoint(pointTracker.getPosition());


            if (this.board.tryMakeMove(point)) {
                this.table.drawBoard(this.board, this.playerView);
                if (this.board.isThisGoal(point))
                    JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                            " won the game.");
            } else
                JOptionPane.showMessageDialog(null, "You cannot move like that.");
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
        this.board = new Board();
        this.table.drawBoard(this.board, this.playerView);
        this.table.activePlayer(this.board.getCurrentPlayer());
        this.endGame = false;
        this.playerView = Player.FIRST;
    }
    public void postBoard(final BoardInterface board) {
        this.board = board;
        this.table.drawBoard(this.board, this.playerView);
        this.table.activePlayer(this.board.getCurrentPlayer());

        if (this.board.isThisGoal(this.board.getPoint(this.board.getBallPosition()))) {
            endGame = true;
            this.playerView = this.board.winnerIs(this.board.getBallPosition());
            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                    " won the game.");
            this.table.appendChat("Player " + this.playerView + " won the game.");
        }

        if (this.board.getPoint(this.board.getBallPosition()).isZeroMovesAvailable() &&
                !this.board.isThisGoal(this.board.getPoint(this.board.getBallPosition()))) {
            this.endGame = true;
            this.playerView = this.board.getOppositePlayer();
            JOptionPane.showMessageDialog(null, "Player " + this.playerView +
                    " won the game.");
            this.table.appendChat("Player " + this.playerView + " won the game.");
        }

    }
    public void postMessage(final String message) {
        this.table.appendChat(message + "\n");

    }


}
