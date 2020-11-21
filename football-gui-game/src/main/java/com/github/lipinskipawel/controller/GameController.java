package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.RenderablePoint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class GameController implements MouseListener {

    private final Map<String, PitchController> playControllers;
    private PitchController controller;

    GameController(final Map<String, PitchController> playControllers) {
        this.playControllers = playControllers;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object src = e.getSource();
        if (isRightMouseButton(e)) {
            controller.rightClick((RenderablePoint) src);
        } else if (isLeftMouseButton(e)) {
            controller.leftClick((RenderablePoint) src);
        }
    }

    void setGameMode(final String gameMode) {
        if (this.controller != null) {
            this.controller.tearDown();
        }
        this.controller = this.playControllers.get(gameMode);
        this.controller.tearDown();
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
