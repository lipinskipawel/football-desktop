package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Direction;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.gui.DrawableFootballPitch;

import static java.util.stream.Collectors.joining;

final class NoopDrawableFootballPitch implements DrawableFootballPitch {

    @Override
    public void drawPitch(final BoardInterface boardInterface, final Player playerView) {
        final var moves = boardInterface.moveHistory();
        if (moves.isEmpty()) {
            return;
        }
        final var last = moves
                .get(moves.size() - 1)
                .getMove()
                .stream()
                .map(Direction::toString)
                .collect(joining(", "));
        System.out.println(Thread.currentThread().getName() + ": making the move and sending: " + last);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activePlayer(Player activePlayer) {
        System.out.println("No-op");
    }
}
