package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.Board;
import com.github.lipinskipawel.board.engine.Player;

final class DrawableFacade implements DrawableFootballPitch {

    private final GameDrawer gameDrawer;
    private final GamePanel gamePanel;

    DrawableFacade(final GameDrawer gameDrawer, final GamePanel gamePanel) {
        this.gameDrawer = gameDrawer;
        this.gamePanel = gamePanel;
    }

    @Override
    public void drawPitch(Board board, Player playerView) {
        this.gameDrawer.drawMove(board, playerView);
        this.gamePanel.activePlayer(board.getPlayer());
    }

    @Override
    public void activePlayer(Player activePlayer) {
        this.gamePanel.activePlayer(activePlayer);
    }
}
