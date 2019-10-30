package com.github.lipinskipawel.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class GameChat extends JPanel {

    private final JTextField typeTextField;
    private final JTextArea chatArea;


    private static final Dimension SIZE = new Dimension(600, 200);

    GameChat() {
        super(new BorderLayout());
        setPreferredSize(SIZE);
        this.typeTextField = new JTextField();
        this.chatArea = new JTextArea();
        this.chatArea.setEditable(false);
        setChatEditable(false);

        this.add(typeTextField, BorderLayout.SOUTH);
        this.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        //this.add(chatArea, BorderLayout.CENTER);

        this.chatArea.append("\nNie jest połączony z żadnym użytkownikiem z sieci LAN\n");
    }

    void addActionClass(ActionListener actionListener) {this.typeTextField.addActionListener(actionListener);}
    JTextField getTypeTextField() {return this.typeTextField;}
    JTextArea getChatArea() {return this.chatArea;}
    void setMyFont(final Font font) {
        typeTextField.setFont(font);
        chatArea.setFont(font);
    }
    void setChatEditable(final boolean setChatEditable) {
        this.typeTextField.setEditable(setChatEditable);
    }
    void down() {
        this.chatArea.setCaretPosition(this.chatArea.getDocument().getLength());
    }

}
