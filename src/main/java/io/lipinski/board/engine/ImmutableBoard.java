package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.IntStream;


class ImmutableBoard implements BoardInterface2 {

    private final List<Point2> points;
    private final Point2 ballPosition;
    private final Player playerToMove;
    private final MoveHistory moveHistory;

    private static ThreadLocal<Stack<Direction>> stack = initStack();
    private static ThreadLocal<List<Move>> allMoves = initListMoves();

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
        final var afterMovePosition = computeBallPosition(direction.opposite());

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

        stack.set(new Stack<>());
        allMoves.set(new ArrayList<>());
        get3(this);

        return allMoves.get();
    }

    private void get3(final BoardInterface2 boardInterface2) {

        for (var move : boardInterface2.getBallAPI().getAllowedDirection()) {

            stack.get().push(move);
            final var afterMove = boardInterface2.executeMove(move);

            if (isItEnd(afterMove.getBallAPI())) {
                final var moveToSave = new Move(new ArrayList<>(stack.get()));
                allMoves.get().add(moveToSave);
            } else {

                final var afterMove2 = boardInterface2.executeMove(move);
                get3(afterMove2);
            }

            stack.get().pop();
        }
    }

    @Override
    public Point2 getBallAPI() {
        return this.points.get(this.ballPosition.getPosition());
    }

    private boolean isItEnd(final Point2 ball) {
        return ball.getAllowedDirection().size() == 7;
    }

    @Override
    public Player getPlayer() {
        return this.playerToMove;
    }

    private List<Point2> makeAMove(final Direction destination) {

        if (isMoveAllowed(destination)) {

            final var newPosition = computeBallPosition(destination);
            final var currentBall = new Point2(points.get(getBallPosition())).notAvailableDirection(destination);

            final var afterMove = new ArrayList<>(points);
            afterMove.set(getBallPosition(), currentBall);


            afterMove.set(newPosition, afterMove.get(newPosition).notAvailableDirection(destination.opposite()));
            return afterMove;
        }
        throw new IllegalMoveException("Can't make a move " + destination.toString());
    }

    private int computeBallPosition(final Direction destination) {
        int moveBall = destination.changeToInt();
        return this.ballPosition.getPosition() + moveBall;
    }

    private boolean allowToMoveAnywhere() {
        return this.ballPosition.getUnavailableDirection().size() == 0;
    }

    private boolean allowToMoveIn7Directions(Point2 point) {
        return point.getUnavailableDirection().size() == 1;
    }

    private static ThreadLocal<Stack<Direction>> initStack() {

        final var thread = new ThreadLocal<Stack<Direction>>();
        final var stackDir = new Stack<Direction>();

        thread.set(stackDir);
        return thread;
    }

    private static ThreadLocal<List<Move>> initListMoves() {

        final var thread = new ThreadLocal<List<Move>>();
        final var stackMoves = new ArrayList<Move>();

        thread.set(stackMoves);
        return thread;
    }

}
