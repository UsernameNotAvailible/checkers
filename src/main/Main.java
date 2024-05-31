package main;
import Core.CheckersLogic;
import UI.CheckersGUI;

public class Main {
    public static void main(String[] args) {
        //CheckersTextConsole console = new CheckersTextConsole();

        //CheckersTextConsole console = new CheckersTextConsole("W:W13,15,25,26,28,29,30,31,32:B1,2,3,4,5,7,8,9,20:");
        CheckersGUI console = new CheckersGUI("B:W6,14,20,22,23,24,29,30,31:B4,11,12,13,21:",false);
        //console.gameLoop();
        //console.playTheEngine(false);

        /*for (int i = 0; i < 64; i++) {
            if (i % 8 == 0) System.out.println();
            System.out.print((i) + ",\t");
        }
        System.out.println();
        for (int i = 0; i < 64; i++) {
            if (i % 8 == 0) System.out.println();
            System.out.print((i % 8) + ", ");
        }
        System.out.println();
        for (int i = 0; i < 64; i++) {
            if (i % 8 == 0) System.out.println();
            System.out.print((i / 8) + ", ");
        }
         */
    }
}
