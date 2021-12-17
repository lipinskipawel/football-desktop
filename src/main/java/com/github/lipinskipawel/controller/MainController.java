package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.Table;
import com.github.lipinskipawel.listener.OptionListener;
import com.github.lipinskipawel.listener.PlayLanListener;
import com.github.lipinskipawel.listener.PlayListener;

/**
 * This class is Master Controller Class.
 */
public class MainController {

    public MainController() {
        Table table = new Table();

        GameController actionGameController = new GameController(table.getDrawableFootballPitch());
        actionGameController.setGameMode("warm-up");

        table.addActionClassForPlayMenu(new PlayListener(table, actionGameController));

        final var playLanListener = new PlayLanListener(table, actionGameController);
        table.addActionClassForPlayMenu(playLanListener);
        table.addConnectListener(playLanListener);

        table.addActionClassForOptionMenu(new OptionListener(table.getOptionsMenu()));

        table.addMouseClassToGameDrawer(actionGameController);
    }
}
