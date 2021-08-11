package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.ai.MoveStrategy;
import com.github.lipinskipawel.board.ai.bruteforce.SmartBoardEvaluator;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.DrawableFootballPitch;
import com.github.lipinskipawel.gui.RenderablePoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.lipinskipawel.board.engine.Boards.immutableBoard;
import static com.github.lipinskipawel.board.engine.Player.FIRST;

final class OneVsAiController implements PitchController {

    private final Logger logger = LoggerFactory.getLogger(OneVsAiController.class);
    private final DrawableFootballPitch drawableFootballPitch;
    private final ExecutorService pool;
    private final AtomicBoolean canHumanMove;
    private final AtomicReference<GameFlowController> gameFlowController;
    private final MoveStrategy strategy;

    private Future<?> findMoveForAI;

    public OneVsAiController(final DrawableFootballPitch drawableFootballPitch) {
        this.drawableFootballPitch = drawableFootballPitch;
        this.pool = Executors.newFixedThreadPool(4);
        this.canHumanMove = new AtomicBoolean(true);
        this.gameFlowController = new AtomicReference<>(new GameFlowController());
        this.strategy = MoveStrategy
                .defaultMoveStrategyBuilder()
                .withBoardEvaluator(new SmartBoardEvaluator())
                .withDepth(3)
                .build();
        this.findMoveForAI = CompletableFuture.completedFuture(null);
    }

    @Override
    public void leftClick(final RenderablePoint renderablePoint) {
        if (canHumanMove()) {
            logger.info("left click");
            this.gameFlowController.updateAndGet(it -> it.makeAMove(renderablePoint.getPosition()));
            this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), FIRST);
            this.gameFlowController.get().onWinner(this::winningMessage);
            if (isSearchingForMoveIsNecessary()) {
                this.findMoveForAI = this.pool.submit(this::searchForBestMoveAndDrawIt);
                this.canHumanMove.set(false);
            }
        } else {
            JOptionPane.showMessageDialog(null, "There is AI to move");
        }
    }

    private boolean isSearchingForMoveIsNecessary() {
        final var didHumanMadeFullMove = this.gameFlowController.get().player() != FIRST;
        return didHumanMadeFullMove && !this.gameFlowController.get().isGameOver();
    }

    private boolean canHumanMove() {
        return this.canHumanMove.get();
    }

    private void searchForBestMoveAndDrawIt() {
        logger.info("AI is searching for best Move");
        final var start = System.currentTimeMillis();
        final var moves = this.gameFlowController.get().board().allLegalMoves();
        final var endTime = System.currentTimeMillis();
        logger.info("All moves took : " + (endTime - start) + " and size is : " + moves.size());
        final var move = strategy.searchForTheBestMove(this.gameFlowController.get().board());
        logger.info("computed move : " + move.getMove());
        if (!Thread.currentThread().isInterrupted()) {
            this.gameFlowController.updateAndGet(it -> it.makeAMove(move));
            SwingUtilities.invokeLater(
                    () -> this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), FIRST)
            );
            this.gameFlowController.get().onPlayerHitTheCorner(displayMessageAndSendMetrics());
        } else {
            logger.info("Task has been interrupted");
        }
        this.canHumanMove.set(true);
    }

    private Function0<Unit> displayMessageAndSendMetrics() {
        return () -> {
            this.gameFlowController
                    .get()
                    .board()
                    .takeTheWinner()
                    .ifPresent(it -> {
                        final var message = String.format("Player %s won the game.", it);
                        JOptionPane.showMessageDialog(null, message);
                    });
            this.canHumanMove.set(false);
            return null;
        };
    }

    @Override
    public void rightClick(final RenderablePoint renderablePoint) {
        if (canHumanMove()) {
            this.gameFlowController.updateAndGet(GameFlowController::undoOnlyCurrentPlayerMove);
            this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), FIRST);
        }
    }

    private Unit winningMessage(final Player winner) {
        JOptionPane.showMessageDialog(null, "Player " + winner + " won he game.");
        return null;
    }

    @Override
    public void tearDown() {
        this.gameFlowController.set(new GameFlowController(immutableBoard(), false));
        this.drawableFootballPitch.drawPitch(this.gameFlowController.get().board(), this.gameFlowController.get().player());
        this.findMoveForAI.cancel(true);
        this.canHumanMove.set(true);
        logger.info("tear down");
    }
}
