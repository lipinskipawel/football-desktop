package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Player;

public interface DrawableFootballPitch {

    /**
     * This method draw {@link BoardInterface} state on the Pitch.
     *
     * @param boardInterface
     * @param playerView
     */
    void drawPitch(final BoardInterface boardInterface, final Player playerView);

    /**
     * This method set the active player in the PLayer panel.
     *
     * @param activePlayer
     */
    void activePlayer(final Player activePlayer);
}
