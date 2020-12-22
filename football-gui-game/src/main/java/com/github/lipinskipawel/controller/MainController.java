package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.DefaultUserDialogPresenter;
import com.github.lipinskipawel.gui.PlayMenu;
import com.github.lipinskipawel.gui.Table;
import com.github.lipinskipawel.network.ConnectionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is Master Controller Class.
 */
public class MainController implements ActionListener {

    private final Table table;
    private final PlayMenu playMenu;
    private final Map<String, PitchController> playControllers;
    private final GameController actionGameController;
    private final ExecutorService pool;

    public MainController() {
        this.table = new Table();
        this.playMenu = this.table.getPlayMenu();
        this.table.addActionClassForPlayMenu(this);

        this.playControllers = new ConcurrentHashMap<>();
        this.playControllers.put("warm-up", new WarmupController(this.table.getDrawableFootballPitch()));
        this.playControllers.put("1vs1", new OneVsOneController(this.table.getDrawableFootballPitch()));
        this.playControllers.put("hell mode", new HellController(this.table.getDrawableFootballPitch()));
        this.playControllers.put("1vsLAN", new OneVsLanController(this.table.getDrawableFootballPitch(),
                new DefaultUserDialogPresenter()));
        this.playControllers.put("1vsAI", new OneVsAiController(this.table.getDrawableFootballPitch()));

        this.actionGameController = new GameController(this.playControllers);
        this.actionGameController.setGameMode("warm-up");
        this.pool = Executors.newSingleThreadExecutor();
        this.table.addMouseClassToGameDrawer(this.actionGameController);

        this.table.addConnectListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final var src = e.getSource();
        if (src == this.playMenu.getMenuItemWarmup()) {
            this.table.setWarmUp();
            this.actionGameController.setGameMode("warm-up");
        } else if (src == this.playMenu.getMenuOneVsOne()) {
            this.table.setOneVsOne();
            this.actionGameController.setGameMode("1vs1");

        } else if (src == this.playMenu.getMenuItemHellMove()) {
            this.table.setHellMode();
            this.actionGameController.setGameMode("hell mode");
        } else if (src == this.playMenu.getMenuLAN()) {

            int waitingToConnect = JOptionPane.showConfirmDialog(
                    null, "Do you want to wait to connection?");

            if (waitingToConnect == JOptionPane.YES_OPTION) {
                this.table.setButtonEnabled(false);
                pool.submit(() -> {
                    final var connection = ConnectionManager.Companion.waitForConnection(
                            ConnectionManager.Companion.getInetAddress()
                    );
                    ((OneVsLanController) this.playControllers.get("1vsLAN")).injectConnection(connection, false);
                });
            } else if (waitingToConnect == JOptionPane.NO_OPTION) {
                this.table.setButtonEnabled(true);
            }
            this.actionGameController.setGameMode("1vsLAN");
            this.table.setOneVsLAN(ConnectionManager.Companion.getInetAddress().getHostAddress());

        } else if (src == this.table.getConnectButton()) {
            try {
                this.table.setButtonEnabled(false);
                final InetAddress address = InetAddress.getByName(this.table.IPEnemy());

                final var connection = ConnectionManager.Companion.connectTo(address);
                ((OneVsLanController) this.playControllers.get("1vsLAN")).injectConnection(connection, true);

            } catch (UnknownHostException unknownHostException) {
                JOptionPane.showMessageDialog(null, "You have written wrong ip address!");
                unknownHostException.printStackTrace();
            }
        } else if (src == this.playMenu.getMenuAI()) {
            this.table.setOneVsAI();
            this.actionGameController.setGameMode("1vsAI");
        }
    }
}
