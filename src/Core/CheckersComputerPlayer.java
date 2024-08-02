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
    private static final int minimalPossible = -100;
    private static final int maximumPossible = 100;
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
        /*Move[] moves = game.getLegalMoves();
        move = moves[0];
        game.makeMove(moves[0]);
        int evaluation = recursiveEvaluate(4, minimalPossible, maximumPossible, 4);
        game.unmakeMove();
        for (int i = 1; moves[i] != null && i < 30; i++) {
            game.makeMove(moves[i]);
            if (recursiveEvaluate(4, minimalPossible, maximumPossible, 4) > evaluation) move = moves[i];
            game.unmakeMove();
        }*/
        //move = game.getLegalMoves()[0];
        recursiveEvaluate(4, minimalPossible, maximumPossible, 4);
        return move;
    }
    /**
     * makes a move on the internal board
     * @param move move that will be made
     * */
    public void makeMove(Move move) {
        game.makeMove(move);}

    /**
     * Unmakes the last move
     */
    public void unmakeMove() {
        game.unmakeMove();
    }

    /**
     * Counts material on one side
     * @param side type of piece counted
     * @return checker count for one side
     */
    private int countMaterial(int side) {
        int evaluation = 0;
        int[] board = game.getBoard();

        for (int i = 0; i < 64; i++) {
            if (board[i] == side) evaluation += checkersWorth * ((side & 1) == 1 ? 1 : 5);
        }
        return evaluation;
    }

    /**
     * Evaluates the position on the board, by counting material for both sides, and subtracting one from another
     * @return the evaluation of the position on the board. From the perspective of the side whose move it is
     */
    private int evaluate() {
        int gameState = game.getGameState();
        int evaluation;
        if (gameState == -1) evaluation = maximumPossible;
        else if (gameState == 1) evaluation = minimalPossible;
        else {
            int whiteEval = countMaterial(CheckersLogic.WHITE_CHECKER) + countMaterial(CheckersLogic.WHITE_QUEEN);
            int blackEval = countMaterial(CheckersLogic.BLACK_CHECKER) + countMaterial(CheckersLogic.BLACK_QUEEN);
            evaluation = whiteEval - blackEval;
        }
        int perspective = 1;
        if (game.isBlacksMove()) perspective = -1;
        return evaluation * perspective;
    }

    /**
     * Evaluates the position up to certain depth
     * @param depth Depth of the evaluation
     * @param alpha Alpha parameter for alpha-beta pruning. Starts with something too small to be a possible evaluation
     * @param beta Beta parameter for alpha-beta pruning. Starts with something too big to be a possible evaluation.
     * @param start Exists to check if the function is on its lowest depth, and if so, record the current best move.
     * @return the evaluation of the position.
     */
    private int recursiveEvaluate(int depth, int alpha, int beta, int start) {
        if (depth == 0) {
            //return evaluate();
            return recursiveCapturesEvaluation(alpha, beta);
        }
        Move[] moves = game.getLegalMoves();
        if (moves[0] == null) return -100;
        int evaluation;
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

    /**
     * Evaluates all captures, until they are exhausted
     * @param alpha Alpha parameter for alpha-beta pruning. Passed from the recursive evaluation.
     * @param beta Beta parameter for alpha-beta pruning. Passed from the recursive evaluation.
     * @return The evaluation of the position, after the captures have been exhausted.
     */
    private int recursiveCapturesEvaluation(int alpha, int beta) {
        Move[] captureMoves = game.getCaptureMoves();
        int evaluation = evaluate();
        if (evaluation >= beta) return beta;
        alpha = max(alpha, evaluation);
        for (int i = 0; captureMoves[i] != null; i++) {
            game.makeMove(captureMoves[i]);
            if (game.isOnTheStreak()) {
                evaluation = -capturesStreakHelper(alpha, beta);
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

    /**
     * Helps recursive captures evaluation go through the streak.
     * @param alpha Alpha parameter for alpha-beta pruning. Passed from the recursive captures evaluation.
     * @param beta Beta parameter for alpha-beta pruning. Passed from the recursive captures evaluation.
     * @return calls recursive captures evaluation to return to evaluating the position after the streak ended.
     */
    private int capturesStreakHelper(int alpha, int beta) {
        int evaluation = 0;
        if (game.isOnTheStreak()) {
            Move[] captures = game.getLegalMoves();
            for (int j = 0; captures[j] != null; j++) {
                game.makeMove(captures[j]);
                evaluation = capturesStreakHelper(alpha, beta);
                game.unmakeMove();
            }
        }
        else {
            return recursiveCapturesEvaluation(-beta, -alpha);
        }
        return evaluation;
    }
    /**
     * Helps recursive evaluation go through the streak
     * @param depth Depth parameter of the recursisve evaluation. Passed back to it once the streak is over.
     * @param alpha Alpha parameter for alpha-beta pruning. Passed from the recursive evaluation.
     * @param beta Beta parameter for alpha-beta pruning. Passed from the recursive evaluation.
     * @param start Start parameter of the recursive evaluation. Passed back to it once the streak is over.
     * @return calls recursive evaluation to return to evaluating the position after the streak ended.
     */
    private int streakHelper(int depth, int alpha, int beta, int start) {
        int evaluation = 0;
        if (game.isOnTheStreak()) {
            Move[] captures = game.getLegalMoves();
            for (int j = 0; captures[j] != null; j++) {
                game.makeMove(captures[j]);
                evaluation = streakHelper(depth, alpha, beta, start);
                game.unmakeMove();
            }
        }
        else {
            return recursiveEvaluate(depth - 1, -beta, -alpha, start);
        }
        return evaluation;
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