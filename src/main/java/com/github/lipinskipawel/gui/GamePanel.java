package com.github.lipinskipawel.gui;

import com.github.lipinskipawel.board.engine.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class GamePanel extends JPanel {

    // NORTH
    private JLabel playerONE;
    private JLabel playerTWO;
    private JPanel playerPanel; // GRID(1, 2) holds player name and time
    private final static Color defaultColor = new Color(238, 238, 238);
    private final static Color activeColor = new Color(0x64fe64);

    // CENTER
    private JTextArea textArea;
    private JScrollPane scrollPaneText;
    private final static String textWarUp = staticTextWarmUp();
    private final static String text1vs1 = staticText1vs1();
    private final static String text1vsLAN = staticText1vsLAN();
    private final static String text1vsAI = staticText1vsAI();

    // SOUTH
    private JLabel labelSouth;
    JTextField textFieldSouth;
    JButton buttonSouth;
    private JPanel littleBox;
    private JPanel southPanel;


    GamePanel() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(280, 100));

        creatingNORTH();
        creatingCENTER();
        creatingSOUTH();

    }

    /**
     * All method required to create all JPanels. Also to setUp these JPanels from other object
     */
    void setWarmUP() {
        removeAll();
        this.textArea.setText(textWarUp);
        add(scrollPaneText, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::revalidate);
        repaint();
    }

    void setONEvsONE() {
        removeAll();
        this.playerPanel.removeAll();
        this.playerPanel.add(playerONE);
        this.playerPanel.add(playerTWO);
        this.textArea.setText(text1vs1);

        add(playerPanel, BorderLayout.NORTH);
        add(scrollPaneText, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::revalidate);
        repaint();
    }

    void setONEvsLAN() {
        removeAll();
        this.playerPanel.removeAll();
        this.playerPanel.add(playerONE);
        this.playerPanel.add(playerTWO);
        this.textArea.setText(text1vsLAN);

        add(playerPanel, BorderLayout.NORTH);
        add(scrollPaneText, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        SwingUtilities.invokeLater(this::revalidate);
        repaint();
    }

    void setONEvsAI() {
        removeAll();
        this.playerPanel.removeAll();
        this.playerPanel.add(playerONE);
        this.playerPanel.add(playerTWO);
        this.textArea.setText(text1vsAI);

        add(playerPanel, BorderLayout.NORTH);
        add(scrollPaneText, BorderLayout.CENTER);
        SwingUtilities.invokeLater(this::revalidate);
        repaint();
    }

    private static String staticTextWarmUp() {
        return """
        This is Warmp-up mode.
        You can play here and try new tactics
        Created by Paweł Lipiński
                """;
    }

    private static String staticText1vs1() {
        return """
        In this mode you can play 1 vs 1 in
        one computer. Invite your friend
        and try to win a game. Rules are
        really simple, try to score a goal
        by putting ball into enemy goal area.
                """;
    }

    private static String staticText1vsLAN() {
        return """
        In this mode try to win with your friend
        connected by LAN. Rules are really
        simple, try to score a goal by putting
        ball into enemy goal area.
        Good luck.""";
    }

    private static String staticText1vsAI() {
        return """
        In this mode you will be faced of AI.
        This is powerful tool which can beat
        you. Try to win with it. In future
        this AI will be working as Neural
        Network. Good luck""";
    }

    private void creatingNORTH() {
        this.playerONE = new JLabel("FIRST");
        this.playerTWO = new JLabel("SECOND");
        this.playerONE.setOpaque(true);
        this.playerTWO.setOpaque(true);
        this.playerPanel = new JPanel(new GridLayout(1, 2));
    }

    private void creatingCENTER() {
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.scrollPaneText = new JScrollPane(textArea);
    }

    private void creatingSOUTH() {
        this.labelSouth = new JLabel("Write IP address of enemy");
        this.textFieldSouth = new JTextField(10);
        this.textFieldSouth.setToolTipText("On top of window you have IP address");
        this.buttonSouth = new JButton("Connect");
        littleBox = new JPanel();
        this.littleBox.add(textFieldSouth);
        this.littleBox.add(buttonSouth);
        this.southPanel = new JPanel(new BorderLayout());

        this.southPanel.add(labelSouth, BorderLayout.NORTH);
        this.southPanel.add(littleBox, BorderLayout.CENTER);

    }


    /**
     * Package method
     *
     * @param font It's used to set up a font.
     */
    void setFontToPlayerPanel(final Font font) {
        this.playerONE.setFont(font);
        this.playerTWO.setFont(font);
    }

    void setFontToCenterAndSouth(final Font font) {
        this.textArea.setFont(font);

        this.labelSouth.setFont(font);
        this.textFieldSouth.setFont(font);
        this.buttonSouth.setFont(font);
    }

    void activePlayer(final Player activePlayer) {
        if (activePlayer == Player.FIRST) {
            this.playerONE.setBackground(activeColor);
            this.playerTWO.setBackground(defaultColor);
        } else {
            this.playerTWO.setBackground(activeColor);
            this.playerONE.setBackground(defaultColor);
        }
    }


    void addButtonConnectListener(final ActionListener listener) {
        this.buttonSouth.addActionListener(listener);
    }


    JTextArea getTextArea() {
        return this.textArea;
    }


}
