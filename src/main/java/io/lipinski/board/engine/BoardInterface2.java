package io.lipinski.board.engine;

import io.lipinski.board.Direction;

import java.util.List;

public interface BoardInterface2 {

    BoardInterface2 loadMove(Move move);
    BoardInterface2 loadMoves(List<Move> moves);

    BoardInterface2 executeMove(final Direction destination);
    boolean isMoveAllowed(final Direction destination);
    void undoFullMove();

    int getBallPosition();

}
