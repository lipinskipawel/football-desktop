package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.Table;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * This class is Master Controller Class.
 */
public class MainController extends Observable implements ActionListener {

    private final Table table;

    private InetAddress ipLocalhost;

    private GameController actionGameController;
    static final String[] notifyStates = createNotifyStates();


    public MainController() {
        this.table = new Table();

        try {
            this.ipLocalhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            this.ipLocalhost = null;
            e.printStackTrace();
        }

        this.actionGameController = new GameController(this.table);
        this.table.addMouseClassToGameDrawer(actionGameController);
        this.table.addActionChatClass(actionGameController);
        this.addObserver(actionGameController);


        this.table.addActionClassToTable(this);
        this.table.addConnectListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        final var src = e.getSource();
        if (src == table.getMenuItemWarmup()) {
            this.table.setWarmUp();
            this.table.setButtonEnabled(true);
            nnotifyObesrvers();
        } else if (src == table.getMenuOneVsOne()) {
            this.table.setOneVsOne();
            this.table.setButtonEnabled(true);
            nnotifyObesrvers();

        } else if (src == table.getMenuItemHellMove()) {
            this.table.setHellMode();
            this.table.setButtonEnabled(true);
            nnotifyObesrvers();
        } else if (src == table.getMenuLAN()) {

            int waitingToConnect = JOptionPane.showConfirmDialog(
                    null, "Do you want to wait to connection?");

            if (waitingToConnect == JOptionPane.YES_OPTION) {
                this.table.setOneVsLAN(this.ipLocalhost.getHostAddress());
                nnotifyObesrvers();
                this.table.setButtonEnabled(false);


                setChanged();
                notifyObservers(notifyStates[1]);

            } else if (waitingToConnect == JOptionPane.NO_OPTION) {

                this.table.setOneVsLAN(this.ipLocalhost.getHostAddress());
                nnotifyObesrvers();
                this.table.setButtonEnabled(true);


            } else {
                nnotifyObesrvers();
            }


        } else if (src == this.table.getConnectButton()) {
            try {
                final InetAddress address = InetAddress.getByName(this.table.IPEnemy());
                this.table.setButtonEnabled(false);
                this.actionGameController.setIPEnemy(address);

                setChanged();
                notifyObservers(notifyStates[2]);
            } catch (UnknownHostException unknownHostException) {
                JOptionPane.showMessageDialog(null, "You have written wrong ip address!");
                unknownHostException.printStackTrace();
            }
        } else if (src == this.table.getMenuAI()) {
            this.table.setOneVsAI();
            this.table.setButtonEnabled(true);
            nnotifyObesrvers();
        }
    }

    private void nnotifyObesrvers() {
        setChanged();
        notifyObservers(notifyStates[0]);
    }

    private static String[] createNotifyStates() {
        String[] states = new String[3];
        states[0] = "kill";
        states[1] = "createServer";
        states[2] = "createSocket";
        return states;
    }

    public Table getTable() {
        return this.table;
    }
}
