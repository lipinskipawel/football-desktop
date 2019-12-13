package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.BoardInterface;
import com.github.lipinskipawel.board.engine.Player;
import com.github.lipinskipawel.controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Table  {

    private final JFrame gameFrame;
    private final GameDrawer gameDrawer;
    private final GameChat gameChat;
    private final GamePanel gamePanel;

    private JMenuItem menuItemWarmup;
    private JMenuItem menuItemOneVsOne;
    private JMenuItem menuItemHellMove;
    private JMenuItem menuItemLAN;
    private JMenuItem menuItemAI;
    //private JMenuItem zapisz;

    private static final Dimension WINDOW_SIZE = new Dimension(700, 800);
    private static final Font globalMenuFont = new Font("sans-serif", Font.PLAIN, 20);
    private static final Font textAreaFont = new Font("sans-serif", Font.PLAIN, 15);


    private String STATE_OF_GAME;
    private final static String[] possibility = {"warm-up", "1vs1", "1vsLAN", "1vsAI", "hell mode"};
    private final static String TITLE = "Piłka";


    /**
     * Default behavior of this class is to set-up warm-up mode.
     * In this case constructor invoke setWarmUp() method to create proper right view.
     * The board is created as warm-up in GameDrawer class.
     */
    public Table() {
        this.gameFrame = new JFrame(TITLE);
        gameFrameSetup(gameFrame);
        final JMenuBar tableMenuBar = createTableMenu();
        this.gameFrame.setJMenuBar(tableMenuBar);


        this.gameDrawer = new GameDrawer();
        this.gameChat = new GameChat();
        this.gamePanel = new GamePanel();
        this.gameChat.setMyFont(globalMenuFont);
        this.gamePanel.setFontToPlayerPanel(globalMenuFont);
        this.gamePanel.setFontToCenterAndSouth(textAreaFont);


        this.gameFrame.add(gameDrawer, BorderLayout.CENTER);
        this.gameFrame.add(gameChat, BorderLayout.SOUTH);
        this.gameFrame.add(gamePanel, BorderLayout.EAST);

        setWarmUp();
        gameFrame.setVisible(true);
    }

    public void addActionClassToTable(final ActionListener actionListener) {
        this.menuItemWarmup.addActionListener(actionListener);
        this.menuItemOneVsOne.addActionListener(actionListener);
        this.menuItemHellMove.addActionListener(actionListener);
        this.menuItemLAN.addActionListener(actionListener);
        this.menuItemAI.addActionListener(actionListener);
        //this.zapisz.addActionListener(actionListener);

    }
    public void addConnectListener(final ActionListener listener) {
        this.gamePanel.addButtonConnectListener(listener);
    }
    public void addActionChatClass(final ActionListener actionGameController) {
        this.gameChat.addActionClass(actionGameController);
    }
    public void addMouseClassToGameDrawer(final GameController actionClassGameBoard) {
        this.gameDrawer.addMouse(actionClassGameBoard);
    }

    // --------------------------------- GETer to ActionTable --------------------------------
    public JMenuItem getMenuItemWarmup() {return this.menuItemWarmup;}
    public JMenuItem getMenuOneVsOne() {return this.menuItemOneVsOne;}
    public JMenuItem getMenuItemHellMove() {return this.menuItemHellMove;}
    public JMenuItem getMenuLAN() {return this.menuItemLAN;}
    public JMenuItem getMenuAI() {return this.menuItemAI;}
    //public JMenuItem getZapisz() {return this.zapisz;}

    public JTextField enterPressed() {return this.gameChat.getTypeTextField();}
    // --------------------------------- GETer to ActionTable --------------------------------


    public void appendRight(final String string) {
        this.gamePanel.getTextArea().append(string);
    }
    public void appendChat(String text) {
        this.gameChat.getChatArea().append(text);
        this.gameChat.getTypeTextField().setText("");
        this.gameChat.down();
    }
    public void setChatEditable(final boolean setChatEditable) {
        this.gameChat.setChatEditable(setChatEditable);
    }
    public String getText() {return this.gameChat.getTypeTextField().getText();}


    public synchronized void setWarmUp() {
        STATE_OF_GAME = possibility[0];
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setWarmUP();
    }
    public synchronized void setOneVsOne() {
        STATE_OF_GAME = possibility[1];
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setONEvsONE();
    }
    public synchronized void setHellMode() {
        STATE_OF_GAME = possibility[4];
        this.gameFrame.setTitle(TITLE);
        this.gamePanel.setHellMode();
    }
    public synchronized void setOneVsLAN(final String ipLocalhost) {
        STATE_OF_GAME = possibility[2];
        this.gameFrame.setTitle("Ip address: " + ipLocalhost);
        this.gamePanel.setONEvsLAN();
    }
    public synchronized void setOneVsAI() {
        STATE_OF_GAME = possibility[3];
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
    private JMenuBar createTableMenu() {
        // https://stackoverflow.com/questions/1951558/list-of-java-swing-ui-properties
        //UIManager.put("MenuBar.font", f);
        UIManager.put("Menu.font", globalMenuFont);
        UIManager.put("MenuItem.font", globalMenuFont);

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createGameStartMenu());
        //menuBar.add(createMenuGame());
        return menuBar;
    }
    private JMenu createGameStartMenu() {
        final JMenu jMenu = new JMenu("Play");
        this.menuItemWarmup = new JMenuItem("Warm-up");
        this.menuItemOneVsOne = new JMenuItem("1 vs 1");
        this.menuItemHellMove = new JMenuItem("Hell mode");
        this.menuItemLAN = new JMenuItem("1 vs LAN");
        this.menuItemLAN.setEnabled(false);
        this.menuItemAI = new JMenuItem("1 vs AI");
//        this.menuItemAI.setEnabled(false);

        jMenu.add(menuItemWarmup);
        jMenu.add(menuItemOneVsOne);
        jMenu.add(menuItemHellMove);
        jMenu.add(menuItemLAN);
        jMenu.add(menuItemAI);
        return jMenu;
    }
//    private JMenu createMenuGame() {
//        final JMenu jMenu = new JMenu("Options");
//        this.zapisz = new JMenuItem("Save game history");
//        this.zapisz.setEnabled(false);
//        jMenu.add(zapisz);
//        return jMenu;
//    }

    public void drawBoard(final BoardInterface board, final Player viewOfPlayer) {this.gameDrawer.drawMove(board, viewOfPlayer);}


    public String getSTATE_OF_GAME() {return this.STATE_OF_GAME;}
    public Object getConnectButton() {
        return this.gamePanel.buttonSouth;
    }
    public String IPEnemy() {
        return this.gamePanel.textFieldSouth.getText();
    }
    public void activePlayer(final Player playerActive) {this.gamePanel.activePlayer(playerActive);}

    public GameDrawer gameDrawer() {
        return this.gameDrawer;
    }
}
