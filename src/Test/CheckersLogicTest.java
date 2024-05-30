package Test;

import Core.CheckersLogic;
import Core.Move;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class CheckersLogicTest {

    @Test
    void getLegalMoves() {
        CheckersLogic game = new CheckersLogic();
        game.printBoard();
        recursivePositions(game, 2);
    }
    private void recursivePositions(CheckersLogic game, int depth) {
        if (depth == 0) return;
        else {
            Move[] moves = game.getLegalMoves();
            for (int i = 0; moves[i] != null; i++) {
                game.makeMove(moves[i]);
                game.printBoard();
                System.out.println("depth = " + depth + " ---------------");
                recursivePositions(game, depth - 1);
                game.unmakeMove();
            }
        }

    }
    @Test
    void unmakeMove() {
        CheckersLogic game = new CheckersLogic("B:W6,15,25,26,28,29,30,31,32:B1,2,3,4,5,7,8,20:");
        //CheckersGUI game = new CheckersGUI("B:W6,15,25,26,28,29,30,31,32:B1,2,3,4,5,7,8,20:");
        Move move = new Move();
        move.startingSquare = 1;
        move.endingSquare = 19;
        move.colour = true;
        game.printBoard();
        game.debugPrint();
        game.makeMove(move);
        game.printBoard();
        game.debugPrint();
        move.startingSquare = 19;
        move.endingSquare = 37;
        move.colour = true;
        game.makeMove(move);
        game.printBoard();
        game.debugPrint();
        game.unmakeMove();
        game.printBoard();
        game.debugPrint();
        game.unmakeMove();
        game.printBoard();
        game.debugPrint();

    }
    @Test
    void ruleCheck() {
        CheckersLogic game = new CheckersLogic("B:W13,19,22,23,25,26,27,28,29,30,31,32:B1,2,3,4,6,7,8,9,10,11,12,14:");
        Move move = new Move(17, 31, true);
        Assertions.assertFalse(game.ruleCheck(move));
    }
    @Test
    void getCaptureMoves() {
        CheckersLogic game = new CheckersLogic("W:W18,21,23,24,25,26,27,28,29,30,31,32:B1,2,3,4,5,6,7,8,10,11,12,14:");
        Move[] captures = game.getCaptureMoves();
        for (Move capture: captures) System.out.println(capture);
    }
}