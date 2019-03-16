package io.lipinski.board.engine;

import io.lipinski.board.Direction;

interface BoardInterface2 {

    boolean isMoveAllowed(final Direction destination);
    void executeMove(final Direction destination);
    void undoMove();

    int getBallPosition();

}
