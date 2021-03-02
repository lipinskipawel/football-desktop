package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.Board;
import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.DrawableFootballPitch;
import com.github.lipinskipawel.gui.RenderablePoint;
import kotlin.Unit;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import static com.github.lipinskipawel.board.engine.Player.FIRST;
import static com.github.lipinskipawel.board.engine.Player.SECOND;

final class HellController implements PitchController {

    private final DrawableFootballPitch drawableFootballPitch;
    private final Map<Player, Integer> tokenForPlayer;
    private GameFlowController gameFlowController;


    public HellController(final DrawableFootballPitch drawableFootballPitch) {
        this.drawableFootballPitch = drawableFootballPitch;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.tokenForPlayer = new HashMap<>();
        this.tokenForPlayer.put(FIRST, 2);
        this.tokenForPlayer.put(SECOND, 2);
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
        if (playerAllowedToUndo(this.gameFlowController.board())) {
            this.gameFlowController = this.gameFlowController.undoPlayerMove(
                    () -> {
                        final var dataObject = new QuestionService(new InMemoryQuestions())
                                .displayYesNoCancel("hell-mode");
                        new HerokuService().send(dataObject);
                        return null;
                    }
            );
            this.tokenForPlayer.compute(this.gameFlowController.player(), (key, val) -> val - 1);
            this.tokenForPlayer.compute(this.gameFlowController.player().opposite(), (key, val) -> val + 1);
            final var message =
                    "Player " + this.gameFlowController.player() +
                            " tokens : " + this.tokenForPlayer.get(this.gameFlowController.player()) + "\n" +
                            "Player " + this.gameFlowController.player().opposite() +
                            " tokens : " + this.tokenForPlayer.get(this.gameFlowController.player().opposite());
            JOptionPane.showMessageDialog(null, message);
        }
        this.drawableFootballPitch.drawPitch(this.gameFlowController.board(), FIRST);
    }

    private boolean playerAllowedToUndo(final Board board) {
        return this.tokenForPlayer.get(board.getPlayer()) > 0;
    }

    private Unit winningMessage(final Player winner) {
        JOptionPane.showMessageDialog(null, "Player " + winner + " won he game.");
        return null;
    }

    @Override
    public void tearDown() {
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
        this.drawableFootballPitch.drawPitch(this.gameFlowController.board(), this.gameFlowController.player());
        this.tokenForPlayer.compute(FIRST, (currentValue, oldValue) -> 2);
        this.tokenForPlayer.compute(SECOND, (currentValue, oldValue) -> 2);
    }
}
