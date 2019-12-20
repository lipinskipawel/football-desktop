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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static com.github.lipinskipawel.board.engine.Player.SECOND;
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

    private AtomicBoolean canHumanMove;
    private BruteForceThinking bruteForce;
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final Map<Player, Integer> tokenForPlayer;


    GameController(final Table table) {
        this.board = Boards.immutableBoard();
        this.table = table;
        this.table.drawBoard(this.board, this.board.getPlayer());
        this.table.activePlayer(this.board.getPlayer());
        this.gameState = table.getSTATE_OF_GAME();
        this.connectionHandler = null;
        this.connectionChat = null;
        this.endGame = false;
        this.playerView = FIRST;
        this.canHumanMove = new AtomicBoolean(true);
        this.tokenForPlayer = new HashMap<>();
        this.tokenForPlayer.put(FIRST, 2);
        this.tokenForPlayer.put(SECOND, 2);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        switch (this.gameState) {
            case "warm-up" -> warmUpMode(e, src);
            case "1vs1" -> {
                try {
                    OneVsOneMode(e, src);
                    this.table.activePlayer(this.board.getPlayer());
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            case "hell mode" -> {
                hellMode(e);
                this.table.activePlayer(this.board.getPlayer());
            }
            case "1vsLAN" -> {
                try {
                    OneVsLANMode(e, src);
                    this.table.activePlayer(this.board.getPlayer());
                } catch (InterruptedException e1) {
                    this.connectionHandler.close();
                    this.connectionChat.close();
                    //e1.printStackTrace();
                }
            }
            case "1vsAI" -> OneVsAIMode(e, src);
            default -> System.out.println("Something goes wrong!");
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

    private void OneVsOneMode(final MouseEvent e, final Object src) throws IOException, InterruptedException {
        if (this.board.isGoal()) {
            return;
        }
        if (isRightMouseButton(e)) {

            final var afterUndo = this.board.undo();
            if (isSmallMoveUndo(afterUndo)) {
                this.board = afterUndo;
            } else {
                final var dataObject = new QuestionService(new InMemoryQuestions())
                        .displayYesNoCancel("1-1");
                new HerokuService()
                        .send(dataObject);
                try {
                    this.board = undoAllPlayerMove(this.board);
                    this.board = undoAllPlayerMove(this.board);
                } catch (RuntimeException ee) {
                    this.board = Boards.immutableBoard();
                }
            }
            this.table.drawBoard(this.board, FIRST);

        } else if (isLeftMouseButton(e)) {
            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
            final var move = this.board.getBallAPI().kickBallTo(pointTracker.getPosition());

            if (!endGame) {
                if (this.board.isMoveAllowed(move)) {
                    logger.info("trying to make a move");
                    this.board = this.board.executeMove(move);
                    logger.info("move has been made");
                    this.table.drawBoard(this.board, FIRST);
                    if (this.board.isGoal()) {
                        var winner = FIRST;
                        if (this.board.getBallPosition() > 10) {
                            winner = SECOND;
                        }
                        endGame = true;
                        this.playerView = this.board.getPlayer();
                        JOptionPane.showMessageDialog(null, "Player " + winner +
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

    private void hellMode(final MouseEvent e) {
        if (this.board.isGoal()) {
            return;
        }
        if (isRightMouseButton(e)) {

            if (playerAllowedToUndo(this.board)) {
                final var afterUndo = this.board.undo();
                if (isSmallMoveUndo(afterUndo)) {
                    this.board = afterUndo;
                } else {
                    final var dataObject = new QuestionService(new InMemoryQuestions())
                            .displayYesNoCancel("hell-mode");
                    new HerokuService()
                            .send(dataObject);
                    try {
                        this.board = undoAllPlayerMove(this.board);
                        this.board = undoAllPlayerMove(this.board);
                    } catch (RuntimeException ee) {
                        this.board = Boards.immutableBoard();
                    }
                    this.tokenForPlayer.compute(this.board.getPlayer(), (key, val) -> val - 1);
                    this.tokenForPlayer.compute(this.board.getPlayer().opposite(), (key, val) -> val + 1);
                    final var message =
                            "Player " + this.board.getPlayer() +
                                    " tokens : " + this.tokenForPlayer.get(this.board.getPlayer()) + "\n" +
                                    "Player " + this.board.getPlayer().opposite() +
                                    " tokens : " + this.tokenForPlayer.get(this.board.getPlayer().opposite());
                    JOptionPane.showMessageDialog(null, message);
                }
                this.table.drawBoard(this.board, FIRST);
            }

        } else if (isLeftMouseButton(e)) {
            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) e.getSource();
            final var move = this.board.getBallAPI().kickBallTo(pointTracker.getPosition());

            if (!endGame) {
                if (this.board.isMoveAllowed(move)) {
                    logger.info("trying to make a move");
                    this.board = this.board.executeMove(move);
                    logger.info("move has been made");
                    this.table.drawBoard(this.board, FIRST);
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

    private boolean playerAllowedToUndo(final BoardInterface board) {
        return this.tokenForPlayer.get(board.getPlayer()) > 0;
    }

    private BoardInterface undoAllPlayerMove(final BoardInterface board) {
        var beforeUndoBoard = board;
        do {
            beforeUndoBoard = beforeUndoBoard.undo();
        } while (beforeUndoBoard.getPlayer() == board.getPlayer());
        return beforeUndoBoard
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove()
                .undoPlayerMove();
    }

    private boolean isSmallMoveUndo(final BoardInterface afterUndo) {
        return afterUndo.getPlayer() == this.board.getPlayer();
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

    private void OneVsAIMode(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

            if (this.canHumanMove.get()) {
                final var afterUndo = this.board.undo();
                if (afterUndo.getPlayer() == this.board.getPlayer()) {
                    this.board = this.board.undo();
                    this.table.drawBoard(this.board, this.board.getPlayer());
                } else {
                    if (this.bruteForce != null) {
                        this.bruteForce.cancel(true);
                    }
                    this.bruteForce = null;
                    try {
                        this.board = undoAllPlayerMove(this.board);
                        this.board = undoAllPlayerMove(this.board);
                    } catch (RuntimeException ee) {
                        this.board = Boards.immutableBoard();
                    }
                    this.canHumanMove = new AtomicBoolean(false);
                    this.table.drawBoard(this.board, FIRST);
                }
            }
            // if you decided to implement undo when ai thinks, watch out on
            // this.canHumanMove
            // this.bruteForce -- managing the state by operations on null

            // use this.bruteForce.cancel(true);

        } else if (isLeftMouseButton(e)) {
            logger.info("canHumanMove : " + this.canHumanMove + ", player : " + this.board.getPlayer());

            if (this.canHumanMove.get()) {
                // here it is save to get move from worker and update board and draw it one more time
                try {
                    if (bruteForce != null) {
                        var aiMove = bruteForce.get();
                        this.board = this.board.executeMove(aiMove);
                        if (this.board.allLegalMoves().size() == 0) {
                            this.table.drawBoard(this.board, this.board.getPlayer().opposite());
                            JOptionPane.showMessageDialog(null, "You won the game!!!");
                            this.canHumanMove.set(false);
                            final var dataObject = new QuestionService(new InMemoryQuestions())
                                    .displayAiQuestion();
                            new HerokuService()
                                    .send(dataObject);
                            return;
                        }
                        this.table.drawBoard(this.board, this.board.getPlayer());
                        this.bruteForce = null;
                        logger.info("redundant update board");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }

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
                            this.canHumanMove.set(false);
                            logger.info("canHumanMove : " + this.canHumanMove);
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
        }
        if (!canHumanMove.get()) {
            // need to check that null because ai can think long, and the human could click and trigger another thead
            if (this.bruteForce == null) {
                this.bruteForce = new BruteForceThinking(
                        new MiniMax(),
                        this.board,
                        2,
                        this.table.gameDrawer(),
                        this.canHumanMove
                );
                bruteForce.execute();
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
        this.endGame = false;
        this.playerView = FIRST;
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
