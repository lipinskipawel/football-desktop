package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.DefaultUserDialogPresenter;
import com.github.lipinskipawel.gui.DrawableFootballPitch;
import com.github.lipinskipawel.gui.RenderablePoint;
import com.github.lipinskipawel.network.Connection;
import com.github.lipinskipawel.network.ConnectionManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GameController implements MouseListener {
    private final DrawableFootballPitch drawableFootballPitch;
    private final ExecutorService pool;
    private PitchController currentActiveController;

    GameController(final DrawableFootballPitch drawableFootballPitch) {
        this.drawableFootballPitch = drawableFootballPitch;
        this.pool = Executors.newSingleThreadExecutor();
        this.currentActiveController = new WarmupController(this.drawableFootballPitch);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        if (isRightMouseButton(e)) {
            this.currentActiveController.rightClick((RenderablePoint) src);
        } else if (isLeftMouseButton(e)) {
            this.currentActiveController.leftClick((RenderablePoint) src);
        }
    }

    public void setGameMode(final String gameMode) {
        tearDownActiveController();

        switch (gameMode) {
            case "warm-up" -> this.currentActiveController = new WarmupController(drawableFootballPitch);
            case "1vs1" -> this.currentActiveController = new OneVsOneController(drawableFootballPitch);
            case "hell mode" -> this.currentActiveController = new HellController(drawableFootballPitch);
            case "1vsAI" -> this.currentActiveController = new OneVsAiController(drawableFootballPitch);
            case "1vsLAN-server" -> {
                this.currentActiveController = new OneVsLanController(drawableFootballPitch, new DefaultUserDialogPresenter());
                pool.submit(() -> {
                    final Connection connection = ConnectionManager.Companion
                            .waitForConnection(ConnectionManager.Companion.getInetAddress());
                    ((OneVsLanController) this.currentActiveController).injectConnection(connection, false);
                });
            }
            case "1vsLAN-client" -> this.currentActiveController = new OneVsLanController(drawableFootballPitch, new DefaultUserDialogPresenter());

            default -> throw new RuntimeException("Should never happen");
        }

    }

    private void tearDownActiveController() {
        if (this.currentActiveController != null) {
            this.currentActiveController.tearDown();
        }
    }

    public void connectTo(final InetAddress address) {
        final Connection connection = ConnectionManager.Companion.connectTo(address);
        ((OneVsLanController) this.currentActiveController).injectConnection(connection, true);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
