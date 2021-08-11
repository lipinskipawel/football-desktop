package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.DrawableFootballPitch;
import com.github.lipinskipawel.gui.RenderablePoint;
import kotlin.Unit;

import javax.swing.*;

import static com.github.lipinskipawel.board.engine.Player.FIRST;

final class OneVsOneController implements PitchController {

    private final DrawableFootballPitch drawableFootballPitch;
    private GameFlowController gameFlowController;

    public OneVsOneController(final DrawableFootballPitch drawableFootballPitch) {
        this.drawableFootballPitch = drawableFootballPitch;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
        if (this.gameFlowController.isGameOver()) {
            return;
        }
        this.gameFlowController = this.gameFlowController.makeAMove(renderablePoint.getPosition());
        this.drawableFootballPitch.drawPitch(this.gameFlowController.board(), FIRST);
        this.gameFlowController.onWinner(this::winningMessage);
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
        if (this.gameFlowController.isGameOver()) {
            return;
        }
        this.gameFlowController = this.gameFlowController.undoPlayerMove(() -> null);
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
