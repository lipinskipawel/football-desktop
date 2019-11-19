package com.github.lipinskipawel;

import com.github.lipinskipawel.controller.MainController;

import java.io.IOException;

import static com.github.lipinskipawel.controller.HerokuService.wakeUpHeroku;

public class App {


    public static void main(String[] args) throws IOException {
        wakeUpHeroku();
        new MainController();
    }
}
