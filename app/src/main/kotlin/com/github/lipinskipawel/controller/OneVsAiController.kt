package com.github.lipinskipawel.controller

import com.github.lipinskipawel.game.GameFlowController
import com.github.lipinskipawel.gui.DrawableFootballPitch
import com.github.lipinskipawel.gui.RenderablePoint
import io.github.lipinskipawel.board.ai.MoveStrategy
import io.github.lipinskipawel.board.ai.bruteforce.SmartBoardEvaluator
import io.github.lipinskipawel.board.engine.Boards
import io.github.lipinskipawel.board.engine.Move
import io.github.lipinskipawel.board.engine.Player
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

internal class OneVsAiController(private val drawableFootballPitch: DrawableFootballPitch) : PitchController {
    private val logger = LoggerFactory.getLogger(OneVsAiController::class.java)
    private val pool: ExecutorService = Executors.newFixedThreadPool(4)
    private val canHumanMove: AtomicBoolean = AtomicBoolean(true)
    private val gameFlowController: AtomicReference<GameFlowController> = AtomicReference(GameFlowController())
    private val strategy: MoveStrategy = MoveStrategy
        .defaultMoveStrategyBuilder()
        .withBoardEvaluator(SmartBoardEvaluator())
        .withDepth(3)
        .build()
    private var findMoveForAI: Future<*>

    init {
        findMoveForAI = CompletableFuture.completedFuture<Any?>(null)
    }

    override fun leftClick(renderablePoint: RenderablePoint) {
        if (canHumanMove()) {
            logger.info("left click")
            gameFlowController.updateAndGet { it.makeAMove(renderablePoint.position) }
            drawableFootballPitch.drawPitch(gameFlowController.get().board(), Player.FIRST)
            gameFlowController.get().onWinner { winningMessage(it) }
            if (isSearchingForMoveIsNecessary()) {
                findMoveForAI = pool.submit { searchForBestMoveAndDrawIt() }
                canHumanMove.set(false)
            }
        } else {
            JOptionPane.showMessageDialog(null, "There is AI to move")
        }
    }

    private fun isSearchingForMoveIsNecessary(): Boolean {
        val didHumanMadeFullMove = gameFlowController.get().player() !== Player.FIRST
        return didHumanMadeFullMove && !gameFlowController.get().isGameOver()
    }

    private fun canHumanMove(): Boolean {
        return canHumanMove.get()
    }

    private fun searchForBestMoveAndDrawIt() {
        logger.info("AI is searching for best Move")
        val start = System.currentTimeMillis()
        val moves = gameFlowController.get().board().allLegalMoves()
        val endTime = System.currentTimeMillis()
        logger.info("All moves took : " + (endTime - start) + " and size is : " + moves.size)
        val move = strategy.searchForTheBestMove(gameFlowController.get().board())
        logger.info("computed move : " + move.move)
        if (!Thread.currentThread().isInterrupted) {
            gameFlowController.updateAndGet { it.makeAMove(move) }
            SwingUtilities.invokeLater {
                drawableFootballPitch.drawPitch(
                    gameFlowController.get().board(),
                    Player.FIRST
                )
            }
            gameFlowController.get().onPlayerHitTheCorner(displayMessageAndSendMetrics())
        } else {
            logger.info("Task has been interrupted")
        }
        canHumanMove.set(true)
    }

    private fun displayMessageAndSendMetrics(): Function0<Unit> {
        return {
            gameFlowController
                .get()
                .board()
                .takeTheWinner()
                .ifPresent {
                    val message = String.format("Player %s won the game.", it)
                    JOptionPane.showMessageDialog(null, message)
                }
            canHumanMove.set(false)
        }
    }

    override fun rightClick(renderablePoint: RenderablePoint) {
        if (canHumanMove()) {
            gameFlowController.updateAndGet { it.undoOnlyCurrentPlayerMove() }
            drawableFootballPitch.drawPitch(gameFlowController.get().board(), Player.FIRST)
        }
    }

    private fun winningMessage(winner: Player) {
        JOptionPane.showMessageDialog(null, "Player $winner won he game.")
    }

    override fun tearDown() {
        gameFlowController.set(GameFlowController(Boards.immutableBoard(), false))
        drawableFootballPitch.drawPitch(gameFlowController.get().board(), gameFlowController.get().player())
        findMoveForAI.cancel(true)
        canHumanMove.set(true)
        logger.info("tear down")
    }

    override fun gameMoves(): List<Move> {
        return gameFlowController.get().board().moveHistory()
    }
}
