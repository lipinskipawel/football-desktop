package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;

import java.util.ArrayList;
import java.util.List;


class Board2 implements BoardInterface2 {

    private final List<Point2> points;
    private final Point2 ballPosition;
    private List<Move> moveHistory;

    Board2() {
        this.points = PointUtils.initialPoints();
        this.ballPosition = points.get(58);
        this.moveHistory = new ArrayList<>();
    }

    Board2(final List<Point2> points,
           final Point2 ballPosition) {
        this.points = points;
        this.ballPosition = ballPosition;
        this.moveHistory = new ArrayList<>();
    }


    @Override
    public BoardInterface2 loadMove(final Move move) {
        if (move.getMove().size() == 1) {
            return executeMove(move.getMove().get(0));
        }
        return null;
    }

    @Override
    public BoardInterface2 loadMoves(final List<Move> moves) {
        return null;
    }

    @Override
    public boolean isMoveAllowed(final Direction destination) {
        return this.ballPosition.isAvailable(destination);
    }

    @Override
    public Board2 executeMove(final Direction destination) {

        final List<Point2> points = makeAMove(destination);
        int ballPosition = computeBallPosition(destination);
        return new Board2(points, points.get(ballPosition));
    }

    @Override
    public int getBallPosition() {
        return this.ballPosition.getPosition();
    }

    // TODO make something about return null
    @Override
    public Board2 undoMove() {

        if (this.ballPosition.isOnStartingPoint() && allowToMoveAnywhere()) {
            return null;
        }

        final var cantMoveThere = this.ballPosition.getUnavailableDirection();
        if (cantMoveThere.size() != 1) {
            return null;
        }
        final var direction = cantMoveThere.get(0);

        final int moveBall = direction.changeToInt();
        final int afterMovePosition = computePositionAfterMove(moveBall);

        final var afterMovePoints = new ArrayList<>(points);

        afterMovePoints.set(this.ballPosition.getPosition(),
                new Point2(this.ballPosition.getPosition()));

        final var previousPositionPoint = afterMovePoints.get(afterMovePosition);
        previousPositionPoint.setAvailableDirections(direction.opposite());
        afterMovePoints.set(afterMovePosition, previousPositionPoint);

        return new Board2(afterMovePoints, afterMovePoints.get(afterMovePosition));
    }

    // TODO size() == 0 this is workaround if someone undo moves to the starting point
    @Override
    public Board2 undoFullMove() {
        var counter = 0;
        while (counter < 100) {
            final var boardAfterUndo = undoMove();
            if (boardAfterUndo.ballPosition.getUnavailableDirection().size() == 1 ||
                    boardAfterUndo.ballPosition.getUnavailableDirection().size() == 0) {
                return boardAfterUndo;
            }
            counter++;
        }
        return null;
    }


    // TODO create list of Points and apply logic here
    // TODO logic like, change if move can be made
    private List<Point2> makeAMove(final Direction destination) {

        if (isMoveAllowed(destination)) {

            int moveBall = destination.changeToInt();
            Point2 currentBall = new Point2(getBallPosition()).notAvailableDirection(destination);

            List<Point2> afterMove = new ArrayList<>(points);
            afterMove.set(getBallPosition(), currentBall);


            int newPosition = computePositionAfterMove(moveBall);

            afterMove.set(newPosition, afterMove.get(newPosition).notAvailableDirection(destination.opposite()));
            return afterMove;
        }
        throw new IllegalMoveException("Can't make a move " + destination.toString());
    }

    private int computeBallPosition(final Direction destination) {
        int moveBall = destination.changeToInt();
        return computePositionAfterMove(moveBall);
    }

    private int computePositionAfterMove(final int moveBall) {
        return this.ballPosition.getPosition() + moveBall;
    }

    private boolean allowToMoveAnywhere() {
        return this.ballPosition.getUnavailableDirection().size() == 0;
    }

}
