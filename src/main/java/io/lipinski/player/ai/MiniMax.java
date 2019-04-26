package io.lipinski.player.ai;

import io.lipinski.board.Point;
import io.lipinski.board.engine.BoardInterface2;

import java.util.ArrayList;
import java.util.List;
/*
class MiniMax implements MoveStrategy {


    private final BoardEvaluator evaluator;
    private BoardInterface2 board;


    public MiniMax() {
        this.evaluator = null;
    }

    @Override
    public Move execute(final BoardInterface2 board,
                        final int depth) {

        Move bestMove = null;

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;


        int numMoves = board.getAvailableDirection().size();


        for (final Move move : board.getAvailableDirection()) {

            BoardInterface2 afterMove = board.executeMove(move);

            currentValue = board.isFirstPlayer() ? // here is CURRENT board
                    min(afterMove, depth - 1) : // here is AFTER board
                    max(afterMove, depth - 1); // here is AFTER board


            if (board.isFirstPlayer() &&
                    currentValue >= highestSeenValue) {

                highestSeenValue = currentValue;
                bestMove = move;
            } else if (board.isSecondPlayer() &&
                    currentValue <= lowestSeenValue) {

                lowestSeenValue = currentValue;
                bestMove = move;
            }
        }
        return bestMove;
    }


    int min(final BoardInterface2 board,
            final int depth) {

        if (depth <= 0)
            return evaluator.evaluate(board);

        int lowestSeenValue = Integer.MAX_VALUE;

        for (Move move : allLegalMoves(board.getBallPosition())) {
            BoardInterface2 afterMove = board.executeMove(move);
            int currentValue = max(afterMove, depth - 1);
            if (currentValue <= lowestSeenValue)
                lowestSeenValue = currentValue;
        }
        return lowestSeenValue;
    }


    int max(final BoardInterface2 board,
            final int depth) {

        if (depth <= 0)
            return evaluator.evaluate(board);

        int highestSeenValue = Integer.MIN_VALUE;

        for (Move move : allLegalMoves(board.getBallPosition())) {
            BoardInterface2 afterMove = board.executeMove(move);
            int currentValue = min(afterMove, depth - 1);
            if (currentValue >= highestSeenValue)
                highestSeenValue = currentValue;
        }
        return highestSeenValue;
    }

    List<Move> allLegalMoves(final int currentPointPosition) {

        List<Move> moves = new ArrayList<>();

        List<MoveQ> shallowMoves = board.getPoint(currentPointPosition).getAllMoves();


        for (MoveQ move : shallowMoves) {
            if (move.done)
                moves.add(move);
            else {
                Point newPoint = move.toPoint();
                moves.add(allLegalMoves(newPoint.getPosition()));
            }
        }
        return moves;
    }


    @Override
    public String toString() {
        return "MiniMax";
    }
}
*/