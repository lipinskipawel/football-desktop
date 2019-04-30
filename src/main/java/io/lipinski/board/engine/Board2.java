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

    Board2(final List<Point2> points,
           final Point2 ballPosition) {
        this.points = points;
        this.ballPosition = ballPosition;
    }


    @Override
    public BoardInterface2 loadMove(Move move) {
        if (move.getMove().size() == 1) {
            return executeMove(move.getMove().get(0));
        }
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

        List<Point2> points = makeAMove(destination);
        int ballPosition = computeBallPosition(destination);
        return new Board2(points, points.get(ballPosition));
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

        List<Point2> afterMove = new ArrayList<>(points);
        afterMove.set(getBallPosition(), currentBall);


        int newPosition = computePositionAfterMove(moveBall);

        afterMove.set(newPosition, new Point2(newPosition)).notAvailableDirection(destination.opposite());
        return afterMove;
    }

    private int computeBallPosition(Direction destination) {
        int moveBall = destination.changeToInt();
        return computePositionAfterMove(moveBall);
    }

    private int computePositionAfterMove(int moveBall) {
        int newPosition;
        if (moveBall > 0)   newPosition = ballPosition.getPosition() - moveBall;
        else                newPosition = ballPosition.getPosition() + moveBall;
        return newPosition;
    }

}
