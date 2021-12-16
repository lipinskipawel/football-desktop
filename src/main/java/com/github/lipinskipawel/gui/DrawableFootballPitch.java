package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.Board;
import com.github.lipinskipawel.board.engine.Player;

public interface DrawableFootballPitch {

    /**
     * This method draw {@link Board} state on the Pitch.
     *
     * @param board
     * @param playerView
     */
    void drawPitch(final Board board, final Player playerView);

    /**
     * This method set the active player in the PLayer panel.
     *
     * @param activePlayer
     */
    void activePlayer(final Player activePlayer);
}
