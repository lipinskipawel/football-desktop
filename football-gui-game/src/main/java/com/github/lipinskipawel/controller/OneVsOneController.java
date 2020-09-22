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

final class OneVsOneController {

    private GameFlowController gameFlowController;
    private final Table table;

    public OneVsOneController(final Table table) {
        this.table = table;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
    }

    void onClick(final MouseEvent e, final Object src) {
        if (this.gameFlowController.isGameOver()) {
            return;
        }
        if (isRightMouseButton(e)) {

            this.gameFlowController = this.gameFlowController.undoPlayerMove(
                    () -> {
                        final var dataObject = new QuestionService(new InMemoryQuestions())
                                .displayYesNoCancel("1-1");
                        new HerokuService().send(dataObject);
                        return null;
                    }
            );
            this.table.drawBoard(this.gameFlowController.board(), FIRST);

        } else if (isLeftMouseButton(e)) {
            GameDrawer.PointTracker pointTracker = (GameDrawer.PointTracker) src;

            this.gameFlowController = this.gameFlowController.makeAMove(pointTracker.getPosition());
            this.table.drawBoard(this.gameFlowController.board(), FIRST);
            this.gameFlowController.onWinner(this::winningMessage);
        }
        this.table.activePlayer(this.gameFlowController.player());
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
