package com.github.lipinskipawel

import com.github.lipinskipawel.board.ai.MoveStrategy
import com.github.lipinskipawel.board.engine.BoardInterface
import com.github.lipinskipawel.board.engine.Move
import com.github.lipinskipawel.gui.GameDrawer
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingWorker

class BruteForceThinking(
        private val moveStrategy: MoveStrategy,
        private val board: BoardInterface,
        private val depth: Int,
        private val gameDrawer: GameDrawer,
        private val canHumanMove: AtomicBoolean) : SwingWorker<Move, Int>() {

    companion object {
        private val logger = LoggerFactory.getLogger(BruteForceThinking::class.java)
    }

    override fun doInBackground(): Move {
        logger.info("searching for move")
        val bestMove = moveStrategy.execute(board, depth)
        gameDrawer.drawMove(board.executeMove(bestMove), board.player.opposite())
        canHumanMove.set(true)
        logger.info("set canHumanMove to : $canHumanMove")
        return bestMove
    }
}