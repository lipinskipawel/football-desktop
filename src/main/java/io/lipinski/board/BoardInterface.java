package io.lipinski.board;


import io.lipinski.player.Player;

import java.util.List;

/**
 * Probably change this interface to be use externally
 * and provide more rich interface above this one for internal use case
 */
public interface BoardInterface {


    boolean tryMakeMove(final Point point);
    boolean tryMakeMove(final Direction direction);
    boolean undoMove(final boolean canIgoBack);
    boolean undoMove();


    List<List<Integer>> getMoveList();

    Player getCurrentPlayer();
    Player getOppositePlayer();
    int getBallPosition();
    Point getPoint(final int position);
    boolean isThisGoal(final Point point);
    Player winnerIs(final int goalCandidatePosition);


}