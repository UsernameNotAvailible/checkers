package main;
import Core.CheckersLogic;
import Core.Size;
import UI.CheckersGUI;

public class Main implements Size {
    public static void main(String[] args) {

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % BOARD_SIDE == 0) System.out.println();
            System.out.print((i) + ",\t");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % BOARD_SIDE == 0) System.out.println();
            System.out.print((i % BOARD_SIDE) + ", ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % BOARD_SIDE == 0) System.out.println();
            System.out.print((i / BOARD_SIDE) + ", ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % BOARD_SIDE == 0) System.out.println();
            System.out.print((i % 2) + ", ");
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (i % BOARD_SIDE == 0) System.out.println();
            System.out.print((i / BOARD_SIDE) % 2 + ", ");
        }
        System.out.println();
        //CheckersTextConsole console = new CheckersTextConsole();

        //CheckersTextConsole console = new CheckersTextConsole("W:W13,15,25,26,28,29,30,31,32:B1,2,3,4,5,7,8,9,20:");
        CheckersGUI console = new CheckersGUI(false);
        //console.gameLoop();
        //console.playTheEngine(false);
    }
}
