package UI;

import Core.CheckersComputerPlayer;
import Core.CheckersLogic;
import Core.Move;

import java.util.Scanner;
/**
 * A class providing an interface to play the game, by printing to the console
 * @author Artem Tarnavskyi
 * @version 0.3
 */
public class CheckersTextConsole {
    private static CheckersLogic game;
    private static CheckersComputerPlayer engine;
    /** Default constructor */
    public CheckersTextConsole() {
        game = new CheckersLogic();
    }
    /**
     * Takes a FEN string. Sets the starting position according to it.
     * @param FEN FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * */
    public CheckersTextConsole(String FEN) {
        game = new CheckersLogic(FEN);
    }
    /** prints out the board in a readable way*/
    private static void printBoard(int[] board) {
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
    /**
     * game loop method. It starts the game from the traditional starting position.
     * Informs players of whose move it is.
     * Informs players of the state of the game (who won).
     * Informs players of the legality of their move.
     * Gives an example move.
     * If the move entered by the player is illegal, prompts the player to try again.
     * */
    public void gameLoop() {
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);
        String input;
        Move move;
        System.out.println("Begin game. ");
        while (loop) {
            //game.debugPrint();
            printBoard(game.getBoard());
            System.out.println();
            if (game.getGameState() == 1) {System.out.println("Black won"); loop = false;}
            else if (game.getGameState() == -1) {System.out.println("White won"); loop = false;}
            else if (game.getGameState() == 0) {
                if (game.isBlacksMove()) System.out.println("Black's move");
                else System.out.println("White's move");
                System.out.println("Choose a cell position of piece to be moved and the new position. e.g., 3a-4b");
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("stop")) {
                    loop = false;
                }
                else {
                    try {
                        move = interpretMove(input);
                        if (game.ruleCheck(move)) game.makeMove(move);
                        else System.out.println("illegal move");
                    }
                    catch (Exception e) {
                        System.out.println("A move must be of the format rankFile-rankFile rank is a number between 1 and 8" + "\n" +
                                "and file is a lowercase latter of latin alphabet between a and h");
                    }
                }
            }


        }
    }
    private static Move interpretMove(String input) throws IllegalArgumentException{
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
        if (move.startingSquare < 0 || move.startingSquare > 63) throw new IllegalArgumentException();
        if (game.checkChecker(move.startingSquare) == CheckersLogic.BLACK_CHECKER) move.colour = true;
        else if (game.checkChecker(move.startingSquare) == CheckersLogic.WHITE_CHECKER) move.colour = false;
        else throw new IllegalArgumentException();
        //System.out.println(move);
        return move;
    }
    /**
     * Allows to play the engine. Takes a boolean as an argument, true sets engine as white, false sets engine as black.
     * If the move entered by the player is illegal, prompts the player to try again.
     * @param side Sets the colour of the player. True = black, false = white.
     * */
    public void playTheEngine(boolean side) {
        engine = new CheckersComputerPlayer(game.getFEN());
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);
        String input;
        Move move;
        System.out.println("Begin game. ");
        while (loop) {
            //game.debugPrint();
            printBoard(game.getBoard());
            System.out.println();
            if (game.getGameState() == 1) {System.out.println("Black won"); loop = false;}
            else if (game.getGameState() == -1) {System.out.println("White won"); loop = false;}
            else if (game.getGameState() == 0) {
                if (game.isBlacksMove() == side) {
                    System.out.println("Player's move");
                    System.out.println("Choose a cell position of piece to be moved and the new position. e.g., 3a-4b");
                    input = scanner.nextLine();
                    if (input.equalsIgnoreCase("stop")) {
                        loop = false;
                    } else {
                        try {
                            move = interpretMove(input);
                            if (game.ruleCheck(move)) {
                                game.makeMove(move);
                                engine.makeMove(move);
                            }
                            else System.out.println("illegal move");
                        } catch (Exception e) {
                            System.out.println("A move must be of the format rankFile-rankFile rank is a number between 1 and 8" + "\n" +
                                    "and file is a lowercase latter of latin alphabet between a and h");
                        }
                    }
                }
                else {
                    move = engine.returnMove();
                    game.makeMove(move);
                    engine.makeMove(move);
                }
            }


        }
    }
}