package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.gui.DefaultUserDialogPresenter;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.gui.Table;
import com.github.lipinskipawel.network.ConnectionChat;
import com.github.lipinskipawel.network.ConnectionState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GameController implements MouseListener, Observer, ActionListener {

    private String gameState;
    private final Table table;

    private InetAddress ipEnemy;

    /**
     * These fields are used for steering flow of the game, so this is important
     * to set up these flags/values to default whenever restartBoard() is invoked.
     */
    private BoardInterface board;

    /**
     * These threads are responsible for handles the connection.
     */
    private ConnectionChat connectionChat;

    private final WarmupController warmupController;
    private final OneVsOneController oneVsOneController;
    private final HellController hellController;
    private final OneVsLanController oneVsLanController;
    private final OneVsAiController oneVsAiController;

    GameController(final Table table) {
        this.board = Boards.immutableBoard();
        this.table = table;
        this.table.drawBoard(Boards.immutableBoard(), this.board.getPlayer());
        this.table.activePlayer(this.board.getPlayer());
        this.gameState = table.getSTATE_OF_GAME();
        this.connectionChat = null;
        this.warmupController = new WarmupController(table);
        this.oneVsOneController = new OneVsOneController(table);
        this.hellController = new HellController(table);
        this.oneVsLanController = new OneVsLanController(table.gameDrawer(), new DefaultUserDialogPresenter());
        this.oneVsAiController = new OneVsAiController(table);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        final var controller = switch (this.gameState) {
            case "warm-up" -> this.warmupController;
            case "1vs1" -> this.oneVsOneController;
            case "hell mode" -> this.hellController;
            case "1vsLAN" -> this.oneVsLanController;
            case "1vsAI" -> this.oneVsAiController;
            default -> throw new IllegalStateException("Unexpected value: " + this.gameState);
        };
        if (isRightMouseButton(e)) {
            controller.rightClick((RenderablePoint) src);
        } else if (isLeftMouseButton(e)) {
            controller.leftClick((RenderablePoint) src);
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
                if (this.connectionChat != null)
                    this.connectionChat.close();
            }
            case "createServer" -> {
                restartBoard();
                this.oneVsLanController.startServer();
            }
            case "createSocket" -> {
                restartBoard();
                this.oneVsLanController.connectToServer(ipEnemy);
            }
        }
    }

    void setIPEnemy(final InetAddress IPEnemy) {
        this.ipEnemy = IPEnemy;
    }

    private void restartBoard() {
        this.board = Boards.immutableBoard();
        this.table.drawBoard(this.board, FIRST);
        this.table.activePlayer(this.board.getPlayer());
        this.warmupController.reset();
        this.oneVsOneController.reset();
        this.hellController.reset();
        this.oneVsLanController.reset();
        this.oneVsAiController.reset();
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
