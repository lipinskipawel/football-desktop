package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.DrawableFootballPitch;
import com.github.lipinskipawel.gui.RenderablePoint;
import kotlin.Unit;

import javax.swing.*;

import static com.github.lipinskipawel.board.engine.Player.FIRST;

final class WarmupController implements PitchController {

    private final DrawableFootballPitch drawableFootballPitch;
    private GameFlowController gameFlowController;

    WarmupController(final DrawableFootballPitch drawableFootballPitch) {
        this.drawableFootballPitch = drawableFootballPitch;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
        try {
            this.gameFlowController = this.gameFlowController.makeAMove(renderablePoint.getPosition());
            this.drawableFootballPitch.drawPitch(this.gameFlowController.board(), FIRST);
            this.gameFlowController.onWinner(this::winningMessage);
        } catch (RuntimeException ee) {
            JOptionPane.showMessageDialog(null, "You cannot move like that.");
        }
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
        this.gameFlowController = this.gameFlowController.undo();
        this.drawableFootballPitch.drawPitch(this.gameFlowController.board(), FIRST);
    }

    private Unit winningMessage(final Player winner) {
        JOptionPane.showMessageDialog(null, "Player " + winner + " won he game.");
        return null;
    }

    @Override
    public void tearDown() {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.drawableFootballPitch.drawPitch(this.gameFlowController.board(), this.gameFlowController.player());
    }
}
