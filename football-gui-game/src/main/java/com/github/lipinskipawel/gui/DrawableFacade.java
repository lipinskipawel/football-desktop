package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Player;

final class DrawableFacade implements DrawableFootballPitch {

    private final GameDrawer gameDrawer;
    private final GamePanel gamePanel;

    DrawableFacade(final GameDrawer gameDrawer, final GamePanel gamePanel) {
        this.gameDrawer = gameDrawer;
        this.gamePanel = gamePanel;
    }

    @Override
    public void drawPitch(BoardInterface boardInterface, Player playerView) {
        this.gameDrawer.drawMove(boardInterface, playerView);
        this.gamePanel.activePlayer(boardInterface.getPlayer());
    }

    @Override
    public void activePlayer(Player activePlayer) {
        this.gamePanel.activePlayer(activePlayer);
    }
}
