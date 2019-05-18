package io.lipinski.board.engine;

import io.lipinski.board.BoardInterface;
import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
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

//        List<Move> moves = new ArrayList<>();
//
//        final var shallowMoves = ballPosition.getAllowedDirection();
//
//        for (var move : shallowMoves) {
//
//            if (isItEnd(move)) {
//                moves.add(new Move(Arrays.asList(move)));
//            }
//        }

        // the way to go
        for (var move : ballPosition.getAllowedDirection()) {
            get2(executeMove(move), move);
        }

        final List<Move> moves = allMoves.get();

//        return moves;
        return moves;
//        for (Direction move : shallowMoves) {
//            final var i = computeBallPosition(move);
//
//            if (this.points.get(i).getAllowedDirection().size() == 7)
//                moves.add(move);
//            else {
//                Point newPoint = move.toPoint();
//                moves.add(allLegalMoves(newPoint.getPosition()));
//            }
//        }
//        return moves;
    }

    private ThreadLocal<Stack<Direction>> stack = initStack();

    private ThreadLocal<List<Move>> allMoves = initListMoves();

    private ThreadLocal<Stack<Direction>> initStack() {

        final var thread = new ThreadLocal<Stack<Direction>>();
        final var stackDir = new Stack<Direction>();

        thread.set(stackDir);
        return thread;
    }

    private ThreadLocal<List<Move>> initListMoves() {

        final var thread = new ThreadLocal<List<Move>>();
        final var allMoves = new ArrayList<Move>();

        thread.set(allMoves);
        return thread;

    }



    private void get(BoardInterface2 boardInterface2, Direction direction) {

        final var ball = getBall(boardInterface2);

        if (isItEnd(ball)) {
            stack.get().push(direction);
            allMoves.get().add(new Move(
                    // dump threadlocal
                    stack.get().stream().collect(Collectors.toUnmodifiableList())
            ));
        }
        else {

            final var shallowMoves = ball.getAllowedDirection();
            for (var move : shallowMoves) {
                stack.get().push(move);
                get(boardInterface2.executeMove(move), move);
            }

            stack.get().pop();
        }
    }

    private void get2(final BoardInterface2 boardInterface2,
                      final Direction direction) {

        stack.get().push(direction);

        if (isItEnd(getBall(boardInterface2))) {
            final var moveToSave = new Move(new ArrayList<>(stack.get()));
            allMoves.get().add(moveToSave);
            stack.get().pop();
        } else {

            for (var move: getBall(boardInterface2).getAllowedDirection()) {
                get2(executeMove(move), move);
            }
        }



    }

    private Point2 getBall(BoardInterface2 boardInterface2) {
        return this.points.get(boardInterface2.getBallPosition());
    }

    private boolean isItEnd(final Point2 ball) {
        return ball.getAllowedDirection().size() != 7 ||
                ball.getAllowedDirection().size() != 8;
    }

    @Override
    public Player getPlayer() {
        return this.playerToMove;
    }


    // TODO create list of Points and apply logic here
    // TODO logic like, change if move can be made
    private List<Point2> makeAMove(final Direction destination) {

        if (isMoveAllowed(destination)) {

            final var newPosition = computeBallPosition(destination);
            Point2 currentBall = new Point2(getBallPosition()).notAvailableDirection(destination);

            List<Point2> afterMove = new ArrayList<>(points);
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

}
