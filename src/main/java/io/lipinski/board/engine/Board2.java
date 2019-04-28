package io.lipinski.board.engine;

import io.lipinski.board.Direction;

import java.util.ArrayList;
import java.util.List;


class Board2 implements BoardInterface2 {

    private final List<Point2> points;
    private Point2 ballPosition;

    Board2() {
        this.points = PointUtils.initialPoints();
        this.ballPosition = points.get(58);
    }

    Board2(List<Point2> points) {
        this.points = points;
    }


    @Override
    public BoardInterface2 loadMove(Move move) {
        return null;
    }

    @Override
    public BoardInterface2 loadMoves(List<Move> moves) {
        return null;
    }

    @Override
    public boolean isMoveAllowed(final Direction destination) {
        return this.ballPosition.isAvailable(destination);
    }

    @Override
    public Board2 executeMove(final Direction destination) {

        int moveBall = destination.changeToInt();

        List<Point2> points = makeAMove(destination);
        return new Board2(points);
    }

    @Override
    public int getBallPosition() {
        return this.ballPosition.getPosition();
    }

    @Override
    public void undoFullMove() {

    }


    // TODO create list of Points and apply logic here
    // TODO logic like, change if move can be made
    private List<Point2> makeAMove(Direction destination) {

        int moveBall = destination.changeToInt();
        Point2 currentBall = new Point2(getBallPosition()).notAvailableDirection(destination);
        // TODO compute next point and disable move back

        List<Point2> afterMove = new ArrayList<>(points);
        afterMove.set(getBallPosition(), currentBall);


        int newPosition;
        if (destination > 0)  {
            newPosition = ballPosition.getPosition() - destination;
        }
        else {
            newPosition = ballPosition.getPosition() + destination;
        }
        return afterMove;
    }

}
