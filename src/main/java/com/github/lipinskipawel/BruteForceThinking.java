package com.github.lipinskipawel;

import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;

import javax.swing.*;

public final class BruteForceThinking extends SwingWorker<Move, Integer> {

    private final MoveStrategy moveStrategy;
    private final BoardInterface board;
    private final int depth;

    public BruteForceThinking(final MoveStrategy moveStrategy,
                              final BoardInterface board,
                              final int depth) {
        this.moveStrategy = moveStrategy;
        this.board = board;
        this.depth = depth;
    }

    @Override
    protected Move doInBackground() {
        return moveStrategy.execute(this.board, this.depth);
    }


}
