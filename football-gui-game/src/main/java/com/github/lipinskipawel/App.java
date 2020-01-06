package com.github.lipinskipawel;

import com.github.lipinskipawel.controller.MainController;

import javax.swing.*;
import java.util.concurrent.Executors;

import static com.github.lipinskipawel.controller.HerokuService.wakeUpHeroku;

public class App {


    public static void main(String[] args) {
        new MainController();
        final var pool = Executors.newFixedThreadPool(1);
        pool.submit(() -> {
            try {
                wakeUpHeroku();
            } catch (Exception ee) {
                JOptionPane.showMessageDialog(null, ee.getMessage());
            }
        });
        pool.shutdown();
    }
}
