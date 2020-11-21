package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.Boards;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.gui.Table;
import kotlin.Unit;

import javax.swing.*;

import static com.github.lipinskipawel.board.engine.Player.FIRST;

final class OneVsOneController implements PitchController {

    private GameFlowController gameFlowController;
    private final Table table;

    public OneVsOneController(final Table table) {
        this.table = table;
        this.gameFlowController = new GameFlowController(Boards.immutableBoard(), false);
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
        if (this.gameFlowController.isGameOver()) {
            return;
        }
        this.gameFlowController = this.gameFlowController.makeAMove(renderablePoint.getPosition());
        this.table.drawBoard(this.gameFlowController.board(), FIRST);
        this.gameFlowController.onWinner(this::winningMessage);

        this.table.activePlayer(this.gameFlowController.player());
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
        if (this.gameFlowController.isGameOver()) {
            return;
        }
        this.gameFlowController = this.gameFlowController.undoPlayerMove(
                () -> {
                    final var dataObject = new QuestionService(new InMemoryQuestions())
                            .displayYesNoCancel("1-1");
                    new HerokuService().send(dataObject);
                    return null;
                }
        );
        this.table.drawBoard(this.gameFlowController.board(), FIRST);
        this.table.activePlayer(this.gameFlowController.player());
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
