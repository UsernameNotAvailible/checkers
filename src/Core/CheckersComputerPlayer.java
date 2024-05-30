package Core;

import static java.lang.Math.max;
/**
 * An engine that provides the best move it finds in the position
 * @author Artem Tarnavskyi
 * @version 0.2
 */
public class CheckersComputerPlayer {
    private static final int checkersWorth = 1;
    private Move move = new Move();
    private final CheckersLogic game;
    /** A default constructor. It starts with a standard starting position*/
    public CheckersComputerPlayer() {
        game = new CheckersLogic();
    }
    /**
     * A constructor that takes a FEN string and makes it a starting position
     * @param FEN a FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * */
    public CheckersComputerPlayer(String FEN) {
        game = new CheckersLogic(FEN);
    }
    /**
     * returns the engine's suggestion for the move
     * @return move field of the class.
     * */
    public Move returnMove() {
        recursiveEvaluate(4, -100, 100, 4);
        return move;
    }
    /**
     * makes a move on the internal board
     * @param move move that will be made
     * */
    public void makeMove(Move move) {
        game.makeMove(move);}
    private int countMaterial(int side) {
        int evaluation = 0;
        int[] board = game.getBoard();
        if (game.isBlacksMove()) {
            for (int i = 0; i < 64; i++) {
                if (board[i] == side) evaluation += checkersWorth;
            }
        }
        return evaluation;
    }
    private int evaluate() {
        int whiteEval = countMaterial(CheckersLogic.WHITE_CHECKER);
        int blackEval = countMaterial(CheckersLogic.BLACK_CHECKER);
        int evaluation = whiteEval - blackEval;
        int perspective = 1;
        if (game.isBlacksMove()) perspective = -1;
        return evaluation * perspective;
    }
    private int recursiveEvaluate(int depth, int alpha, int beta, int start) {
        if (depth == 0) {
            return evaluate();
            //return recursiveCapturesEvaluation(alpha, beta);
        }
        Move[] moves = game.getLegalMoves();
        if (moves[0] == null) return -100;
        int evaluation = 0;
        for (int i = 0; moves[i] != null && i < 30; i++) {
            game.makeMove(moves[i]);
            if (game.isOnTheStreak()) {
                evaluation = -streakHelper(depth, alpha, beta, start);
            }
            else {
                evaluation = -recursiveEvaluate(depth - 1, -beta, -alpha, start);
            }
            game.unmakeMove();
            if (depth == start && alpha < evaluation) this.move = moves[i];
            if (evaluation >= beta) {
                return beta;
            }
            alpha = max(alpha, evaluation);

        }
        return alpha;
    }
    private int recursiveCapturesEvaluation(int alpha, int beta) {
        Move[] captureMoves = game.getCaptureMoves();
        int evaluation = evaluate();
        if (evaluation >= beta) return beta;
        alpha = max(alpha, evaluation);
        for (int i = 0; captureMoves[i] != null; i++) {
            game.makeMove(captureMoves[i]);
            if (game.isOnTheStreak()) {
                evaluation = capturesStreakHelper(alpha, beta);
            }
            else {
                evaluation = -recursiveCapturesEvaluation(-beta, -alpha);
            }
            game.unmakeMove();
            if (evaluation >= beta) return beta;
            alpha = max(alpha, evaluation);
        }
        return alpha;
    }
    private int capturesStreakHelper(int alpha, int beta) {
        if (game.isOnTheStreak()) {
            Move[] captures = game.getLegalMoves();
            for (int j = 0; captures[j] != null; j++) {
                game.makeMove(captures[j]);
                capturesStreakHelper(alpha, beta);
                game.unmakeMove();
            }
        }
        else {
            return recursiveCapturesEvaluation(-beta, -alpha);
        }
        return 0;
    }
    private int streakHelper(int depth, int alpha, int beta, int start) {
        if (game.isOnTheStreak()) {
            Move[] captures = game.getLegalMoves();
            for (int j = 0; captures[j] != null; j++) {
                game.makeMove(captures[j]);
                streakHelper(depth, alpha, beta, start);
                game.unmakeMove();
            }
        }
        else {
            return recursiveEvaluate(depth - 1, -beta, -alpha, start);
        }
        return 0;
    }

    /**
     * Prints board the engine sees. Exists for debugging purposes.
     */
    public void printBoard() {game.printBoard();}

    /**
     * Calls the debug method of the CheckersLogic class of the game field.
     */
    public void debugPrint() {
        game.debugPrint();
    }

    /**
     * Returns the FEN of the position engine has stored currently
     */
    public String getFen() {
        return game.getFEN();
    }
}