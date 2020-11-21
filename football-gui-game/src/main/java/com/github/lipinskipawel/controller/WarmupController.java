package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.gui.Table;
import kotlin.Unit;

import javax.swing.*;

import static com.github.lipinskipawel.board.engine.Player.FIRST;

final class WarmupController implements PitchController {

    private GameFlowController gameFlowController;
    private final Table table;

    WarmupController(final Table table) {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.table = table;
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
        try {
            this.gameFlowController = this.gameFlowController.makeAMove(renderablePoint.getPosition());
            this.table.drawBoard(this.gameFlowController.board(), FIRST);
            this.gameFlowController.onWinner(this::winningMessage);
        } catch (RuntimeException ee) {
            JOptionPane.showMessageDialog(null, "You cannot move like that.");
        }
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
        this.gameFlowController = this.gameFlowController.undo();
        this.table.drawBoard(this.gameFlowController.board(), FIRST);
    }

    private Unit winningMessage(final Player winner) {
        JOptionPane.showMessageDialog(null, "Player " + winner + " won he game.");
        return null;
    }

    @Override
    public void tearDown() {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.table.drawBoard(this.gameFlowController.board(), this.gameFlowController.player());
        this.table.activePlayer(this.gameFlowController.player());
    }
}
