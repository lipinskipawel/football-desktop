package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;

import java.util.List;

public interface BoardInterface2 {

    BoardInterface2 executeMove(final Direction destination) throws IllegalMoveException;
    BoardInterface2 undoMove();
    BoardInterface2 undoFullMove();

    // change those two methods by one getLegalMoves()
    boolean isMoveAllowed(final Direction destination);
    int getBallPosition();

    Player getPlayer();

}
