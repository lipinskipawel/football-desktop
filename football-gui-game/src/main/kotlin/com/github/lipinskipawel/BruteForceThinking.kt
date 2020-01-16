package com.github.lipinskipawel

import com.github.lipinskipawel.board.ai.MoveStrategy
import com.github.lipinskipawel.board.engine.BoardInterface
import com.github.lipinskipawel.board.engine.Move
import com.github.lipinskipawel.gui.GameDrawer
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import javax.swing.SwingWorker

class BruteForceThinking(
        private val moveStrategy: MoveStrategy,
        private val board: BoardInterface,
        private val depth: Int,
        private val gameDrawer: GameDrawer,
        private val consumer: Consumer<Move>) : SwingWorker<Move, Int>() {

    companion object {
        private val logger = LoggerFactory.getLogger(BruteForceThinking::class.java)
    }

    override fun doInBackground(): Move {
        logger.info("searching for move")
        val bestMove = moveStrategy.execute(board, depth)
        consumer.accept(bestMove)
        return bestMove
    }
}