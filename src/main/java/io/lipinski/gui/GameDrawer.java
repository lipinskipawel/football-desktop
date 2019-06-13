package io.lipinski.gui;


import io.lipinski.board.BoardInterface;
import io.lipinski.board.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class is public because when I created it I wasn't perfect at
 * that time. Now I would design it differently.
 */
public class GameDrawer extends JPanel {

    private static final Dimension GAME_BOARD_DIMENSION = new Dimension(402, 522);
    private static final Dimension BLOCK_PANEL_DIMENSION = new Dimension(10, 10);
    private final List<PointTracker> pointTrackers;

    private final Color pitch_COLOR = new Color(0, 170, 45);
    private final BasicStroke pitch_stroke = new BasicStroke(3f);
    private final BasicStroke move_stroke = new BasicStroke(2f);
    private final BasicStroke last_move = new BasicStroke(4f);

    private BoardInterface board;
    private Player viewOfPlayer;

    GameDrawer() {
        super(new GridLayout(13, 9));
        this.viewOfPlayer = Player.FIRST;


        this.pointTrackers = new ArrayList<>(117);
        for (int i = 0; i < 117; i++) {
            final PointTracker pointTracker = new PointTracker(i);
            this.pointTrackers.add(pointTracker);
            add(pointTracker);
        }
        setPreferredSize(GAME_BOARD_DIMENSION);
        setSize(GAME_BOARD_DIMENSION);


        //validate();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graph2D = (Graphics2D) g;
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        prepare();
        drawPitch(graph2D);
        drawMove(graph2D);
        drawBall(graph2D);


        PointTracker pointTracker = pointTrackers.get(1);
        PointTracker pointTracker1 = pointTrackers.get(115);
        graph2D.drawString("Player 2", pointTracker.getXMiddle(), pointTracker.getYMiddle());
        graph2D.drawString("Player 1", pointTracker1.getXMiddle(), pointTracker1.getYMiddle());

        graph2D.dispose();
    }





    synchronized void drawMove(final BoardInterface board, final Player viewOfPlayer) {
        this.board = board;
        this.viewOfPlayer = viewOfPlayer;
        repaint();
    }
    void addMouse(MouseListener mouseListener) {
        for (PointTracker point: pointTrackers) {
            point.addMouseListener(mouseListener);
        }
    }

    // --------------------------------------- privates methods -----------------------------------------------

    /**
     * This method is like set up the warm-up mode.
     * That's why this method is invoked before all others method in @Override paint.
     */
    private void prepare() {


            if (this.viewOfPlayer == Player.FIRST) {
                removeAll();

                for (final PointTracker pointTracker : pointTrackers) {
                    add(pointTracker);
                }
            } else {
                removeAll();
                Collections.reverse(pointTrackers);
                for (final PointTracker pointTracker : pointTrackers) {
                    add(pointTracker);
                }
                Collections.reverse(pointTrackers);
            }
            // according to https://docs.oracle.com/javase/7/docs/api/javax/swing/JComponent.html#revalidate()
            invalidate();
            revalidate();


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
    private void drawPitch(final Graphics2D graphics2D) {
        graphics2D.setColor(pitch_COLOR);
        graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());


        // dots and PointTrackers
        graphics2D.setColor(Color.white);
        for (PointTracker point : pointTrackers) {
            Shape oval = new Rectangle2D.Float(point.getXMiddle() - 2, point.getYMiddle() - 2, 4, 4);
            graphics2D.fill(oval);
            //graphics2D.drawString(Integer.toString(point.getPosition()), point.getXMiddle() - 2, point.getYMiddle() - 2);

        }

        // pitch
        PointTracker pointTracker9 = pointTrackers.get(9);
        PointTracker pointTracker12 = pointTrackers.get(12);
        PointTracker pointTracker3 = pointTrackers.get(3);
        PointTracker pointTracker5 = pointTrackers.get(5);
        PointTracker pointTracker14 = pointTrackers.get(14);
        PointTracker pointTracker17 = pointTrackers.get(17);
        PointTracker pointTracker107 = pointTrackers.get(107);
        PointTracker pointTracker104 = pointTrackers.get(104);
        PointTracker pointTracker113 = pointTrackers.get(113);
        PointTracker pointTracker111 = pointTrackers.get(111);
        PointTracker pointTracker102 = pointTrackers.get(102);
        PointTracker pointTracker99 = pointTrackers.get(99);
        graphics2D.setStroke(pitch_stroke);
        graphics2D.drawLine(pointTracker9.getXMiddle(), pointTracker9.getYMiddle(), pointTracker12.getXMiddle(), pointTracker12.getYMiddle());
        graphics2D.drawLine(pointTracker9.getXMiddle(), pointTracker9.getYMiddle(), pointTracker99.getXMiddle(), pointTracker99.getYMiddle());
        graphics2D.drawLine(pointTracker12.getXMiddle(), pointTracker12.getYMiddle(), pointTracker3.getXMiddle(), pointTracker3.getYMiddle());
        graphics2D.drawLine(pointTracker3.getXMiddle(), pointTracker3.getYMiddle(), pointTracker5.getXMiddle(), pointTracker5.getYMiddle());
        graphics2D.drawLine(pointTracker5.getXMiddle(), pointTracker5.getYMiddle(), pointTracker14.getXMiddle(), pointTracker14.getYMiddle());
        graphics2D.drawLine(pointTracker14.getXMiddle(), pointTracker14.getYMiddle(), pointTracker17.getXMiddle(), pointTracker17.getYMiddle());
        graphics2D.drawLine(pointTracker17.getXMiddle(), pointTracker17.getYMiddle(), pointTracker107.getXMiddle(), pointTracker107.getYMiddle());
        graphics2D.drawLine(pointTracker104.getXMiddle(), pointTracker104.getYMiddle(), pointTracker107.getXMiddle(), pointTracker107.getYMiddle());
        graphics2D.drawLine(pointTracker104.getXMiddle(), pointTracker104.getYMiddle(), pointTracker113.getXMiddle(), pointTracker113.getYMiddle());
        graphics2D.drawLine(pointTracker113.getXMiddle(), pointTracker113.getYMiddle(), pointTracker111.getXMiddle(), pointTracker111.getYMiddle());
        graphics2D.drawLine(pointTracker111.getXMiddle(), pointTracker111.getYMiddle(), pointTracker102.getXMiddle(), pointTracker102.getYMiddle());
        graphics2D.drawLine(pointTracker102.getXMiddle(), pointTracker102.getYMiddle(), pointTracker99.getXMiddle(), pointTracker99.getYMiddle());
    }
    private void drawMove(final Graphics2D graphics2D) {
        if (this.board == null)
            return;

        graphics2D.setColor(Color.white);
        graphics2D.setStroke(move_stroke);


        final List<List<Integer>> tempList = this.board.getMoveList();
        List<Integer> oneMove;
        for (int i = 0; i < tempList.size(); i++) {

            oneMove = tempList.get(i);
            if (i == tempList.size() - 1 && oneMove.size() == 1) {
                if ((i - 1) < 0)
                    return;
                oneMove = tempList.get(i -1);
                graphics2D.setStroke(last_move);
                for (int j = 1; j < oneMove.size(); j++) {
                    PointTracker start = pointTrackers.get(oneMove.get(j - 1));
                    PointTracker now = pointTrackers.get(oneMove.get(j));
                    Shape line = new Line2D.Float(start.getXMiddle(), start.getYMiddle(), now.getXMiddle(), now.getYMiddle());
                    graphics2D.draw(line);
                }
                return;
            }
            else if (i == tempList.size() - 1 && oneMove.size() > 1) {
                graphics2D.setStroke(last_move);
                for (int j = 1; j < oneMove.size(); j++) {
                    PointTracker start = pointTrackers.get(oneMove.get(j - 1));
                    PointTracker now = pointTrackers.get(oneMove.get(j));
                    Shape line = new Line2D.Float(start.getXMiddle(), start.getYMiddle(), now.getXMiddle(), now.getYMiddle());
                    graphics2D.draw(line);
                }
                return;
            }
            else if (i != tempList.size() - 1) {
                graphics2D.setStroke(move_stroke);
                //oneMove = tempList.get(i);
                for (int j = 1; j < oneMove.size(); j++) {
                    PointTracker start = pointTrackers.get(oneMove.get(j - 1));
                    PointTracker now = pointTrackers.get(oneMove.get(j));
                    Shape line = new Line2D.Float(start.getXMiddle(), start.getYMiddle(), now.getXMiddle(), now.getYMiddle());
                    graphics2D.draw(line);
                }
            }

        }


    }
    private void drawBall(final Graphics2D graphics2D) {
        if (this.board == null)
            return;

        graphics2D.setColor(Color.white);
        PointTracker ball = pointTrackers.get(board.getBallPosition());
        Shape oval = new Ellipse2D.Double(ball.getXMiddle() - 7, ball.getYMiddle() - 7, 14, 14);
        graphics2D.fill(oval);
    }




    public class PointTracker extends JPanel {

        final int position;

        private PointTracker(final int position) {
            super(new GridBagLayout());
            setPreferredSize(BLOCK_PANEL_DIMENSION);
            this.position = position;
        }


        public int getPosition() {return this.position;}
        @Override
        public String toString() {
            return Integer.toString(this.position);
        }


        private int getXMiddle() {return getX() + (this.getWidth() / 2);}
        private int getYMiddle() {
            return getY() + (this.getHeight() / 2);
        }
    }

}














