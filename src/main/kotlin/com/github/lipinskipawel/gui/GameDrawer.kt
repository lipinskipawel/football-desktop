package com.github.lipinskipawel.gui

import com.github.lipinskipawel.board.engine.Board
import com.github.lipinskipawel.board.engine.Direction
import com.github.lipinskipawel.board.engine.Move
import com.github.lipinskipawel.board.engine.Player
import java.awt.*
import java.awt.event.MouseListener
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.stream.Collectors
import javax.swing.JPanel

class GameDrawer : JPanel(GridLayout(13, 9)) {
    private val pointTrackers: MutableList<PointTracker>
    private val pitch_COLOR = Color(0, 170, 45)
    private val pitch_stroke = BasicStroke(3f)
    private val move_stroke = BasicStroke(2f)
    private val last_move_stroke = BasicStroke(4f)
    private var board: Board? = null
    private var viewOfPlayer: Player

    init {
        viewOfPlayer = Player.FIRST
        pointTrackers = ArrayList(117)
        for (i in 0..116) {
            val pointTracker = PointTracker(i)
            pointTrackers.add(pointTracker)
            add(pointTracker)
        }
        preferredSize = GAME_BOARD_DIMENSION
        size = GAME_BOARD_DIMENSION


        //validate();
    }

    override fun paint(g: Graphics) {
        val graph2D = g as Graphics2D
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        prepare()
        drawPitch(graph2D)
        drawMove(graph2D)
        drawBall(graph2D)
        val pointTracker = pointTrackers[1]
        val pointTracker1 = pointTrackers[115]
        graph2D.drawString("Player 2", pointTracker.xMiddle, pointTracker.yMiddle)
        graph2D.drawString("Player 1", pointTracker1.xMiddle, pointTracker1.yMiddle)
        graph2D.dispose()
    }

    fun drawMove(board: Board?, viewOfPlayer: Player) {
        this.board = board
        this.viewOfPlayer = viewOfPlayer
        repaint()
    }

    fun addMouse(mouseListener: MouseListener?) {
        for (point in pointTrackers) {
            point!!.addMouseListener(mouseListener)
        }
    }
    // --------------------------------------- privates methods -----------------------------------------------
    /**
     * This method is like set up the warm-up mode.
     * That's why this method is invoked before all others method in @Override paint.
     */
    private fun prepare() {
        if (viewOfPlayer === Player.FIRST) {
            removeAll()
            for (pointTracker in pointTrackers) {
                add(pointTracker)
            }
        } else {
            removeAll()
            Collections.reverse(pointTrackers)
            for (pointTracker in pointTrackers) {
                add(pointTracker)
            }
            Collections.reverse(pointTrackers)
        }
        // according to https://docs.oracle.com/javase/7/docs/api/javax/swing/JComponent.html#revalidate()
        invalidate()
        revalidate()


//        if (isNecessaryToSwitchPlayers) {
//            removeAll();
//            if (this.board.getCurrentPlayer() == Player.FIRST) {
//
//                for (final PointTracker pointTracker :pointTrackers) {
//                    add(pointTracker);
//                }
//            } else {
//                Collections.reverse(pointTrackers);
//                for (final PointTracker pointTracker :pointTrackers) {
//                    add(pointTracker);
//                }
//                Collections.reverse(pointTrackers);
//            }
//            // according to https://docs.oracle.com/javase/7/docs/api/javax/swing/JComponent.html#revalidate()
//            invalidate();
//            revalidate();
//        }
    }

    private fun drawPitch(graphics2D: Graphics2D) {
        graphics2D.color = pitch_COLOR
        graphics2D.fillRect(0, 0, width, height)


        // dots and PointTrackers
        graphics2D.color = Color.white
        for (point in pointTrackers) {
            val oval: Shape = Rectangle2D.Float(point.xMiddle.toFloat() - 2, point.yMiddle.toFloat() - 2, 4f, 4f)
            graphics2D.fill(oval)
            //graphics2D.drawString(Integer.toString(point.getPosition()), point.getXMiddle() - 2, point.getYMiddle() - 2);
        }

        // pitch
        val pointTracker9 = pointTrackers[9]
        val pointTracker12 = pointTrackers[12]
        val pointTracker3 = pointTrackers[3]
        val pointTracker5 = pointTrackers[5]
        val pointTracker14 = pointTrackers[14]
        val pointTracker17 = pointTrackers[17]
        val pointTracker107 = pointTrackers[107]
        val pointTracker104 = pointTrackers[104]
        val pointTracker113 = pointTrackers[113]
        val pointTracker111 = pointTrackers[111]
        val pointTracker102 = pointTrackers[102]
        val pointTracker99 = pointTrackers[99]
        graphics2D.stroke = pitch_stroke
        graphics2D.drawLine(pointTracker9.xMiddle, pointTracker9.yMiddle, pointTracker12.xMiddle, pointTracker12.yMiddle)
        graphics2D.drawLine(pointTracker9.xMiddle, pointTracker9.yMiddle, pointTracker99.xMiddle, pointTracker99.yMiddle)
        graphics2D.drawLine(pointTracker12.xMiddle, pointTracker12.yMiddle, pointTracker3.xMiddle, pointTracker3.yMiddle)
        graphics2D.drawLine(pointTracker3.xMiddle, pointTracker3.yMiddle, pointTracker5.xMiddle, pointTracker5.yMiddle)
        graphics2D.drawLine(pointTracker5.xMiddle, pointTracker5.yMiddle, pointTracker14.xMiddle, pointTracker14.yMiddle)
        graphics2D.drawLine(pointTracker14.xMiddle, pointTracker14.yMiddle, pointTracker17.xMiddle, pointTracker17.yMiddle)
        graphics2D.drawLine(pointTracker17.xMiddle, pointTracker17.yMiddle, pointTracker107.xMiddle, pointTracker107.yMiddle)
        graphics2D.drawLine(pointTracker104.xMiddle, pointTracker104.yMiddle, pointTracker107.xMiddle, pointTracker107.yMiddle)
        graphics2D.drawLine(pointTracker104.xMiddle, pointTracker104.yMiddle, pointTracker113.xMiddle, pointTracker113.yMiddle)
        graphics2D.drawLine(pointTracker113.xMiddle, pointTracker113.yMiddle, pointTracker111.xMiddle, pointTracker111.yMiddle)
        graphics2D.drawLine(pointTracker111.xMiddle, pointTracker111.yMiddle, pointTracker102.xMiddle, pointTracker102.yMiddle)
        graphics2D.drawLine(pointTracker102.xMiddle, pointTracker102.yMiddle, pointTracker99.xMiddle, pointTracker99.yMiddle)
    }

    private fun drawMove(graphics2D: Graphics2D) {
        if (board == null) return
        graphics2D.color = Color.white
        graphics2D.stroke = move_stroke
        if (thisAreTheSameMoves(board!!.moveHistory(), board!!.allMoves())) {
            val moves = ArrayDeque<PointTracker?>()
            moves.add(pointTrackers[58])
            val allMoves = board!!.moveHistory()
            for (i in allMoves.indices) {
                if (shouldDrawBold(allMoves, i)) {
                    graphics2D.stroke = last_move_stroke
                    val directions = allMoves[i].move
                    for (direction in directions) {
                        drawByDirection(graphics2D, moves, direction)
                    }
                } else {
                    graphics2D.stroke = move_stroke
                    val directions = allMoves[i].move
                    for (direction in directions) {
                        drawByDirection(graphics2D, moves, direction)
                    }
                }
            }
        } else {
            val moves = ArrayDeque<PointTracker?>()
            moves.add(pointTrackers[58])
            val numberOfMoves = board!!.moveHistory()
                    .stream()
                    .map { obj: Move -> obj.move }
                    .mapToLong { obj: List<Direction> -> obj.size.toLong() }
                    .sum()
            val allDirections = board!!.allMoves()
            for (i in allDirections.indices) {
                if (i >= numberOfMoves) {
                    // draw bold
                    graphics2D.stroke = last_move_stroke
                    drawByDirection(graphics2D, moves, allDirections[i])
                } else {
                    graphics2D.stroke = move_stroke
                    drawByDirection(graphics2D, moves, allDirections[i])
                }
            }
        }
    }

    private fun drawBall(graphics2D: Graphics2D) {
        if (board == null) return
        graphics2D.color = Color.white
        val ball = pointTrackers[board!!.ballPosition]
        val oval: Shape = Ellipse2D.Double(ball.xMiddle.toDouble() - 7, ball.yMiddle.toDouble() - 7, 14.0, 14.0)
        graphics2D.fill(oval)
    }

    private fun thisAreTheSameMoves(allMoves: List<Move>,
                                    allDirections: List<Direction>): Boolean {
        val allMoveDirections = allMoves
                .stream()
                .map { obj: Move -> obj.move }
                .flatMap { obj: List<Direction> -> obj.stream() }
                .collect(Collectors.toList())
        return allMoveDirections.size == allDirections.size
    }

    private fun drawByDirection(graphics2D: Graphics2D,
                                moves: ArrayDeque<PointTracker?>,
                                direction: Direction) {
        val start = moves.poll()
        val next = pointTrackers[start!!.position + direction.changeToInt()]
        val line = Line2D.Float(start.xMiddle.toFloat(), start.yMiddle.toFloat(), next.xMiddle.toFloat(), next.yMiddle.toFloat())
        graphics2D.draw(line)
        moves.add(next)
    }

    private fun shouldDrawBold(allMoves: List<Move>, i: Int): Boolean {
        return i == allMoves.size - 1
    }

    inner class PointTracker(position: Int) : JPanel(GridBagLayout()), RenderablePoint {
        override val position: Int

        init {
            preferredSize = BLOCK_PANEL_DIMENSION
            this.position = position
        }

        override fun toString(): String {
            return position.toString()
        }

         val xMiddle: Int get() = x + this.width / 2
         val yMiddle: Int get() = y + this.height / 2
    }

    companion object {
        private val GAME_BOARD_DIMENSION = Dimension(402, 522)
        private val BLOCK_PANEL_DIMENSION = Dimension(10, 10)
    }
}
