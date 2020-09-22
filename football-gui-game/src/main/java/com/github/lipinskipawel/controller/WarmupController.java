package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.GameDrawer;
import com.github.lipinskipawel.gui.Table;
import kotlin.Unit;

import javax.swing.*;
import java.awt.event.MouseEvent;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

final class WarmupController {

    private GameFlowController gameFlowController;
    private final Table table;

    WarmupController(final Table table) {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.table = table;
    }

    void onClick(final MouseEvent e, final Object src) {
        if (isRightMouseButton(e)) {

            this.gameFlowController = this.gameFlowController.undo();
            this.table.drawBoard(this.gameFlowController.board(), FIRST);

        } else if (isLeftMouseButton(e)) {

            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;
            try {
                this.gameFlowController = this.gameFlowController.makeAMove(pointTracker.getPosition());
                this.table.drawBoard(this.gameFlowController.board(), FIRST);
                this.gameFlowController.onWinner(this::winningMessage);
            } catch (RuntimeException ee) {
                JOptionPane.showMessageDialog(null, "You cannot move like that.");
            }
        }
    }

    private Unit winningMessage(final Player winner) {
        JOptionPane.showMessageDialog(null, "Player " + winner + " won he game.");
        return null;
    }

    void reset() {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player());
        this.table.activePlayer(this.gameFlowController.player());
    }
}
