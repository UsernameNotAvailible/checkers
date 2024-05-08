package UI;

import Core.CheckersLogic;
import Core.Move;

import java.util.Scanner;

public class CheckersTextConsole {
    private static CheckersLogic c = new CheckersLogic();
    public CheckersTextConsole() {
    }
    public static void printBoard(int[] board) {
        int counter = 0;
        int fileCounter = 8;
        char checker = ' ';
        for (int i: board) {
            if(counter % 8 == 0) {
                System.out.print(fileCounter + "| ");
                fileCounter --;
            }
            if (i == CheckersLogic.BLACK_CHECKER) checker = 'b';
            else if (i == CheckersLogic.WHITE_CHECKER) checker = 'w';
            else if (i == CheckersLogic.EMPTY_SQUARE) checker = ' ';
            System.out.print(checker + ", ");
            counter++;
            if(counter % 8 == 0) {
                System.out.println();
                //System.out.println("   _______________________________");
            }
        }
        //System.out.println("   ______________________");
        System.out.println("   a, b, c, d, e, f, g, h");
    }
    public static void gameLoop() {
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);
        String input = "";
        Move move;
        c.emptyBoard();
        c.setWhite();
        while (loop) {
            printBoard(c.getBoard());
            System.out.println();
            if (c.getGameState() == 1) System.out.println("Black won");
            else if (c.getGameState() == -1) System.out.println("White won");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("stop")) {
                loop = false;
            }
            else {
                move = interpretMove(input);
                if (c.ruleCheck(move)) c.makeMove(move);
                else System.out.println("illegal move");
            }

        }
    }
    private static Move interpretMove(String input) {
        Move move = new Move();
        //int counter = 0;
        boolean start = true;
        move.startingSquare = 0;
        move.endingSquare = 0;
        for (char  c: input.toCharArray()) {
            if (c > 47 && c < 58) {
                if (start) move.startingSquare += 64 - ((c - 48) * 8);
                else move.endingSquare += 64 - ((c - 48) * 8);
            }
            if (c > 96 && c < 105) {
                if (start) move.startingSquare += (c - 97);
                else move.endingSquare += (c - 97);
            }
            if (c == 45) start = false;
        }
        if (c.checkChecker(move.startingSquare) == CheckersLogic.BLACK_CHECKER) move.colour = true;
        else if (c.checkChecker(move.startingSquare) == CheckersLogic.WHITE_CHECKER) move.colour = false;
        else System.out.println("invalid move");
        //System.out.println(move);
        return move;
    }

    private static void debugPrint() {

    }

    public static void main(String[] args) {
        gameLoop();

        /*for (int i = 0; i < 64; i++) {
            if (i % 8 == 0) System.out.println();
            System.out.print(i + "," + "\t");
        }

         */
        /*for (int i = 0; i < 64; i++) {
            if (i % 8 == 0) System.out.println();
            System.out.print(i % 8 + "," + "\t");
        }

         */


    }
}
