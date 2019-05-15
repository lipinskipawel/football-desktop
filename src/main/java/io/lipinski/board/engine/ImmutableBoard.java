package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


class ImmutableBoard implements BoardInterface2 {

    private final List<Point2> points;
    private final Point2 ballPosition;
    private final Player playerToMove;
    private final MoveHistory moveHistory;

    ImmutableBoard() {
        this.points = PointUtils.initialPoints();
        this.ballPosition = points.get(58);
        this.playerToMove = Player.FIRST;
        this.moveHistory = new MoveHistory();
        IntStream.iterate(0, i -> i < 10, i -> i + 1);
    }

    private ImmutableBoard(final List<Point2> points,
                           final Point2 ballPosition,
                           final Player currentPlayer,
                           final MoveHistory moveHistory) {
        this.points = points;
        this.ballPosition = ballPosition;
        this.playerToMove = currentPlayer;
        this.moveHistory = moveHistory;
    }

    @Override
    public boolean isMoveAllowed(final Direction destination) {
        return this.ballPosition.isAvailable(destination);
    }

    @Override
    public ImmutableBoard executeMove(final Direction destination) {

        final var points = makeAMove(destination);
        final int ballPosition = computeBallPosition(destination);
        final var newMoveHistory = this.moveHistory.add(destination);

        if (allowToMoveIn7Directions(points.get(ballPosition))) {
            return new ImmutableBoard(points,
                    points.get(ballPosition),
                    this.playerToMove.opposite(),
                    newMoveHistory);
        }
        return new ImmutableBoard(points,
                points.get(ballPosition),
                this.playerToMove,
                newMoveHistory);
    }

    @Override
    public int getBallPosition() {
        return this.ballPosition.getPosition();
    }

    @Override
    public ImmutableBoard undoMove() {

        if (this.ballPosition.isOnStartingPoint() && allowToMoveAnywhere()) {
            throw new IllegalUndoMoveException("You can undo move when no move has been done");
        }

        final var direction = moveHistory.getLastMove();
        final int moveBall = direction.changeToInt();
        final int afterMovePosition = computePositionAfterMove(-moveBall);

        final var afterMovePoints = new ArrayList<>(points);

        afterMovePoints.set(this.ballPosition.getPosition(),
                new Point2(this.ballPosition.getPosition()));

        final var previousPositionPoint = afterMovePoints.get(afterMovePosition);
        previousPositionPoint.setAvailableDirections(direction);
        afterMovePoints.set(afterMovePosition, previousPositionPoint);

        final var moveHistoryNew = this.moveHistory.subtract();

        if (allowToMoveIn7Directions(points.get(this.ballPosition.getPosition()))) {
            return new ImmutableBoard(points,
                    afterMovePoints.get(afterMovePosition),
                    this.playerToMove.opposite(),
                    moveHistoryNew);
        }
        return new ImmutableBoard(points,
                afterMovePoints.get(afterMovePosition),
                this.playerToMove,
                moveHistoryNew);
    }

    @Override
    public List<Move> allLegalMoves() {

        return null;
    }

    @Override
    public Player getPlayer() {
        return this.playerToMove;
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

    private boolean allowToMoveIn7Directions(Point2 point) {
        return point.getUnavailableDirection().size() == 1;
    }

}
