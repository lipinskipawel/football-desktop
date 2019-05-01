package io.lipinski.board.engine;

import io.lipinski.board.Direction;

import java.util.List;

public interface BoardInterface2 {

    BoardInterface2 loadMove(final Move move);
    BoardInterface2 loadMoves(final List<Move> moves);

    BoardInterface2 executeMove(final Direction destination);
    BoardInterface2 undoMove();
    BoardInterface2 undoFullMove();
    boolean isMoveAllowed(final Direction destination);

    int getBallPosition();
    Player getPlayer();

}
