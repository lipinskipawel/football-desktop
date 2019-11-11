package com.github.lipinskipawel;

import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Move;
import com.github.lipinskipawel.gui.GameDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BruteForceThinking extends SwingWorker<Move, Integer> {

    private final MoveStrategy moveStrategy;
    private final BoardInterface board;
    private final int depth;
    private final GameDrawer gameDrawer;
    private AtomicBoolean canHumanMove;
    private final Logger logger = LoggerFactory.getLogger(BruteForceThinking.class);

    public BruteForceThinking(final MoveStrategy moveStrategy,
                              final BoardInterface board,
                              final int depth,
                              final GameDrawer gameDrawer,
                              final AtomicBoolean canHumanMove) {
        this.moveStrategy = moveStrategy;
        this.board = board;
        this.depth = depth;
        this.gameDrawer = gameDrawer;
        this.canHumanMove = canHumanMove;
    }

    @Override
    protected Move doInBackground() {
        logger.info("searching for move");
        final var bestMove = moveStrategy.execute(this.board, this.depth);
        this.gameDrawer.drawMove(board.executeMove(bestMove), board.getPlayer().opposite());
        this.canHumanMove.set(true);
        logger.info("set canHumanMove to : " + this.canHumanMove);
        return bestMove;
    }


}
