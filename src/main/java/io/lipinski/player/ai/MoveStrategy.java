package io.lipinski.player.ai;

import io.lipinski.board.engine.BoardInterface2;

interface MoveStrategy {

    Move execute(BoardInterface2 board, int depth);

}
