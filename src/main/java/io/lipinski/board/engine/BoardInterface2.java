package io.lipinski.board.engine;

import io.lipinski.board.Direction;
import io.lipinski.board.engine.exceptions.IllegalMoveException;
import io.lipinski.board.engine.exceptions.IllegalUndoMoveException;

import java.util.List;

public interface BoardInterface2 {

    BoardInterface2 executeMove(final Direction destination) throws IllegalMoveException;
    BoardInterface2 undoMove() throws IllegalUndoMoveException;

    // change those two methods by one getLegalMoves()
    List<Direction> allLegalMoves();
    boolean isMoveAllowed(final Direction destination);
    int getBallPosition();

    Player getPlayer();

}
