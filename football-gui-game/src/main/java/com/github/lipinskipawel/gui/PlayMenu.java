package com.github.lipinskipawel.gui;

import javax.swing.*;
import java.awt.event.ActionListener;

public final class PlayMenu extends JMenu {

    private final JMenuItem menuItemWarmup;
    private final JMenuItem menuItemOneVsOne;
    private final JMenuItem menuItemHellMove;
    private final JMenuItem menuItemLAN;
    private final JMenuItem menuItemAI;

    PlayMenu() {
        super("Play");
        this.menuItemWarmup = new JMenuItem("Warm-up");
        this.menuItemOneVsOne = new JMenuItem("1 vs 1");
        this.menuItemHellMove = new JMenuItem("Hell mode");
        this.menuItemLAN = new JMenuItem("1 vs LAN");
        this.menuItemAI = new JMenuItem("1 vs AI");

        add(menuItemWarmup);
        add(menuItemOneVsOne);
        add(menuItemHellMove);
        add(menuItemLAN);
        add(menuItemAI);
    }

    public void addActionClassForAllMenuItems(final ActionListener actionListener) {
        this.menuItemWarmup.addActionListener(actionListener);
        this.menuItemOneVsOne.addActionListener(actionListener);
        this.menuItemHellMove.addActionListener(actionListener);
        this.menuItemLAN.addActionListener(actionListener);
        this.menuItemAI.addActionListener(actionListener);
    }

    public JMenuItem getMenuItemWarmup() {
        return this.menuItemWarmup;
    }

    public JMenuItem getMenuOneVsOne() {
        return this.menuItemOneVsOne;
    }

    public JMenuItem getMenuItemHellMove() {
        return this.menuItemHellMove;
    }

    public JMenuItem getMenuLAN() {
        return this.menuItemLAN;
    }

    public JMenuItem getMenuAI() {
        return this.menuItemAI;
    }
}
