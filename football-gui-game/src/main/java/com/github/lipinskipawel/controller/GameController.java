package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.DefaultUserDialogPresenter;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.gui.Table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GameController implements MouseListener, Observer {

    private final Table table;
    private String gameState;

    private InetAddress ipEnemy;

    private final WarmupController warmupController;
    private final OneVsOneController oneVsOneController;
    private final HellController hellController;
    private final OneVsLanController oneVsLanController;
    private final OneVsAiController oneVsAiController;

    GameController(final Table table) {
        this.table = table;
        this.gameState = table.getSTATE_OF_GAME();
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
    public void update(Observable o, Object arg) {
        this.gameState = table.getSTATE_OF_GAME();
        switch ((String) arg) {
            case "kill" -> restartBoard();
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
        this.warmupController.reset();
        this.oneVsOneController.reset();
        this.hellController.reset();
        this.oneVsLanController.reset();
        this.oneVsAiController.reset();
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
