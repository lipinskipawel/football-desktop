package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.Table;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            if (this.ipLocalhost == null || ipLocalhost.getHostAddress().equals("127.0.0.1")) {
                JOptionPane.showMessageDialog(null, "You don't have connection to internet");
                return;
            }

            int waitingToConnect = JOptionPane.showConfirmDialog(
                    null, "Do you want to wait to connection?");

            if (waitingToConnect == 0) {
                this.table.setOneVsLAN(this.ipLocalhost.getHostAddress());
                nnotifyObesrvers();
                this.table.setButtonEnabled(false);


                setChanged();
                notifyObservers(notifyStates[1]);

            } else if (waitingToConnect == 1) {

                this.table.setOneVsLAN(this.ipLocalhost.getHostAddress());
                nnotifyObesrvers();
                this.table.setButtonEnabled(true);


            } else {
                nnotifyObesrvers();
            }


        } else if (src == this.table.getConnectButton()) {
            if (isValidIPEnemy(this.table.IPEnemy())) {
                this.table.setButtonEnabled(false);
                this.actionGameController.setIPEnemy(this.table.IPEnemy());

                setChanged();
                notifyObservers(notifyStates[2]);

            } else {
                JOptionPane.showMessageDialog(null, "You have written wrong ip address!");
            }

        } else if (src == this.table.getMenuAI()) {
            this.table.setOneVsAI();
            this.table.setButtonEnabled(true);
            nnotifyObesrvers();
        }
    }


    private static boolean isValidIPEnemy(final String ipSubnet) {
        return isGoodFormat(ipSubnet) && isSubnetIsInOurSubnet(ipSubnet) && isMineIP(ipSubnet);
    }

    private static boolean isGoodFormat(final String ipSubnet) {
        Pattern pattern = Pattern.compile("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}");
        Matcher matcher = pattern.matcher(ipSubnet);
        if (matcher.find()) {
            if (matcher.end() == ipSubnet.length() && matcher.start() == 0) {
                return true;
            }

        }
        return false;
    }

    private static boolean isSubnetIsInOurSubnet(final String ipSubnet) {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String ipString = ip.getHostAddress();

            String[] ipEnemyTab = ipSubnet.split("\\.", -1);
            String[] ipOur = ipString.split("\\.", -1);

            for (int i = 0; i < 3; i++) {
                if (!ipOur[i].equals(ipEnemyTab[i]))
                    return false;
            }
            if (Integer.valueOf(ipEnemyTab[3]) > 254)
                return false;


        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Unknown Host Exception: InetAddress.getLocalHost().getHostAddress()");
        }

        return true;
    }

    private static boolean isMineIP(String ipSubnet) {
        try {
            return !ipSubnet.equals(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
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
