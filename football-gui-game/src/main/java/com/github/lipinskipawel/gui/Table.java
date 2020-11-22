package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Table {

    private final JFrame gameFrame;
    private final PlayMenu playMenu;
    private final GameDrawer gameDrawer;
    private final GamePanel gamePanel;

    private static final Dimension WINDOW_SIZE = new Dimension(700, 600);
    private static final Font globalMenuFont = new Font("sans-serif", Font.PLAIN, 20);
    private static final Font textAreaFont = new Font("sans-serif", Font.PLAIN, 15);


    private final static String TITLE = "Football game";


    /**
     * Default behavior of this class is to set-up warm-up mode.
     * In this case constructor invoke setWarmUp() method to create proper right view.
     * The board is created as warm-up in GameDrawer class.
     */
    public Table() {
        // https://stackoverflow.com/questions/1951558/list-of-java-swing-ui-properties
        //UIManager.put("MenuBar.font", f);
        UIManager.put("Menu.font", globalMenuFont);
        UIManager.put("MenuItem.font", globalMenuFont);
        this.gameFrame = new JFrame(TITLE);
        gameFrameSetup(gameFrame);

        final JMenuBar tableMenuBar = new JMenuBar();
        this.playMenu = new PlayMenu();
        tableMenuBar.add(playMenu);
        this.gameFrame.setJMenuBar(tableMenuBar);

        this.gamePanel = new GamePanel();
        this.gameDrawer = new GameDrawer(this.gamePanel);
        this.gamePanel.setFontToPlayerPanel(globalMenuFont);
        this.gamePanel.setFontToCenterAndSouth(textAreaFont);

        this.gameFrame.add(gameDrawer, BorderLayout.CENTER);
        this.gameFrame.add(gamePanel, BorderLayout.EAST);

        setWarmUp();
        gameFrame.setVisible(true);
    }

    public void addConnectListener(final ActionListener listener) {
        this.gamePanel.addButtonConnectListener(listener);
    }

    public void addMouseClassToGameDrawer(final GameController actionClassGameBoard) {
        this.gameDrawer.addMouse(actionClassGameBoard);
    }

    public void addActionClassForPlayMenu(final ActionListener actionListener) {
        this.playMenu.addActionClassForAllMenuItems(actionListener);
    }
    // --------------------------------- GETer to ActionTable --------------------------------

    public synchronized void setWarmUp() {
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setWarmUP();
    }

    public synchronized void setOneVsOne() {
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setONEvsONE();
    }

    public synchronized void setHellMode() {
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setHellMode();
    }

    public synchronized void setOneVsLAN(final String ipLocalhost) {
        this.gameFrame.setTitle("Ip address: " + ipLocalhost);
        this.gamePanel.setONEvsLAN();
    }

    public synchronized void setOneVsAI() {
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setONEvsAI();
    }

    public void setButtonEnabled(final boolean toBoolean) {
        this.gamePanel.buttonSouth.setEnabled(toBoolean);
    }


    private void gameFrameSetup(final JFrame gameFrame) {
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setSize(WINDOW_SIZE);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
    }

    public PlayMenu getPlayMenu() {
        return this.playMenu;
    }

    public void drawBoard(final BoardInterface board, final Player viewOfPlayer) {
        this.gameDrawer.drawMove(board, viewOfPlayer);
    }

    public Object getConnectButton() {
        return this.gamePanel.buttonSouth;
    }

    public String IPEnemy() {
        return this.gamePanel.textFieldSouth.getText();
    }

    public void activePlayer(final Player playerActive) {
        this.gamePanel.activePlayer(playerActive);
    }

    public GameDrawer gameDrawer() {
        return this.gameDrawer;
    }
}
