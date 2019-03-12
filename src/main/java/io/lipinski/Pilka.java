package io.lipinski;


import io.lipinski.controller.MainController;


public class Pilka {


    public static void main(String[] args) {
        MainController mainController = new MainController();



    }

    private static void tab() {
        int nr = 0;
        int[][] tab = new int[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                tab[i][j] = nr;
                nr++;
            }
        }
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                System.out.print(tab[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(tab.length);
    }
}
