package Core;

import java.util.Arrays;
import java.util.Stack;

import static java.lang.Math.abs;

/**
 * A class containing the logic of a checkers game
 * @author Artem Tarnavskyi
 * @version 0.3
 */
public class CheckersLogic {
    /** An array representing the board on which the game is played */
    private final int[] board = new int[64];
    public final int BOARD_SIDE = 8;
    public final int BOARD_SIZE = BOARD_SIDE*BOARD_SIDE;
    private static final int[] allowableDifferences = new int[]{7, -7, 9, -9, 14, -14, 18, -18};
    /** A constant representing the empty square on the board */
    public static final int EMPTY_SQUARE = 0;
    /** A constant representing the white checker on the board */
    public static final int WHITE_CHECKER = 1;
    public static final boolean WHITES_MOVE = false;
    public static final boolean BLACKS_MOVE = true;
    /** A constant representing the black checker on the board */
    public static final int BLACK_CHECKER = 2;
    private String FEN;
    private String previousFEN;
    private final Stack<String> FENStack = new Stack<>();
    private boolean blacksMove = false;
    private boolean capture = false;
    private boolean onTheStreak = false;
    private int gameState = 0;
    private int streakingSquare = -1;
    private int moveCounter = 0;
    private int plyCounter = 0;
    /**
     * takes no arguments,
     * sets black and white pieces to their traditional positions
     * at the beginning of the game
     * */
    public CheckersLogic() {
        emptyBoard();
        setBlack();
        setWhite();
        makeFEN();
    }
    /**
     * Takes a FEN string, arranges the starting position accordingly.
     * @param FEN FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * */
    public CheckersLogic(String FEN) {
        this.FEN = FEN;
        readFen();
    }
    /** sets every square on the board to empty square*/
    public void emptyBoard() {
        Arrays.fill(board, EMPTY_SQUARE);
    }
    /** sets black pieces on their traditional starting squares */
    public void setBlack() {
        boolean set = false;
        for (int i = 0; i < 24; i++) {
            if (set) board[i] = BLACK_CHECKER;
            set = !set;
            if (i % 8 == 7) set = !set;
        }
    }
    /** sets white pieces on their traditional starting squares */
    public void setWhite() {
        boolean set = false;
        for (int i = 63; i > 39; i--) {
            if (set) board[i] = WHITE_CHECKER;
            set = !set;
            if (i % 8 == 0) set = !set;
        }
    }
    /** prints board to the console */
    public void printBoard() {
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
    private boolean withinAllowableSquares(int dif) {
        for (int i: allowableDifferences) {
            if (i == dif) return true;
        }
        return false;
    }
    /**
     * takes a Move object,
     * makes the move, erases taken pieces and updates fields according to the rules
     * @param move a move being made.
     * */
    public void makeMove(Move move) {
        //if (!ruleCheck(move)) return;
        board[move.startingSquare] = EMPTY_SQUARE;
        if (move.colour) board[move.endingSquare] = BLACK_CHECKER;
        else board[move.endingSquare] = WHITE_CHECKER;
        int dif = move.endingSquare - move.startingSquare;
        if (abs(dif) > 9) {
            board[move.startingSquare + (move.endingSquare - move.startingSquare) / 2] = EMPTY_SQUARE;
            if (captureCheck(move.endingSquare)) {
                blacksMove = !blacksMove;
                onTheStreak = true;
                streakingSquare = move.endingSquare;
            }
        }
        blacksMove = !blacksMove;
        if (blacksMove) {
            if (loseCheck(BLACK_CHECKER)) gameState = -1;
        }
        else {
            if (loseCheck(WHITE_CHECKER)) gameState = 1;
        }
        FENStack.push(FEN);
        makeFEN();
        if (!onTheStreak) {
            if (!blacksMove) moveCounter++;
        }
        plyCounter++;
    }
    /** Unmakes move. Unless it's the starting position of the game, in which case it does nothing */
    public void unmakeMove() {
        if (plyCounter != 0) {
            FEN = FENStack.pop();
            readFen();
            plyCounter--;
        }
    }
    /**
     * takes a Move object,
     * checks whether the move is within the rules
     * @param move A move that is being attempted
     * @return true if the move is within the rules, false otherwise.
     * */
    public boolean ruleCheck(Move move) {
        if (move.endingSquare < 0 || move.endingSquare > 63 || move.startingSquare < 0 || move.startingSquare > 63) return false;
        int dif = move.endingSquare - move.startingSquare;
        if (!withinAllowableSquares(dif)) return false;
        if (move.colour != blacksMove) return false;
        if (board[move.endingSquare] != EMPTY_SQUARE) return false;
        if (move.startingSquare % 8 == 0 && (move.endingSquare % 8) > 2) return false;
        if (move.startingSquare % 8 == 7 && (move.endingSquare % 8) < 5) return false;
        if (move.startingSquare % 8 == 1 && (move.endingSquare % 8) > 3) return false;
        if (move.startingSquare % 8 == 6 && (move.endingSquare % 8) < 4) return false;
        if (move.colour) {
            if (!onTheStreak) {
                if (dif < 0) return false;
                if (captureForwardAllCheck(BLACK_CHECKER) && abs(dif) <= 9) return false;
                if (dif > 9 && board[move.startingSquare + dif / 2] != WHITE_CHECKER) return false;
                else if (dif > 9) capture = true;
            }
            else {
                if (!(abs(dif) > 9)) return false;
                else capture = true;
                if (board[move.startingSquare + dif / 2] != WHITE_CHECKER) return false;
                if (move.startingSquare != streakingSquare) return false;
            }
        }
        else {
            if (!onTheStreak) {
                if (dif > 0) return false;
                if (captureForwardAllCheck(WHITE_CHECKER) && abs(dif) <= 9) return false;
                if (dif < -9 && board[move.startingSquare + dif / 2] != BLACK_CHECKER) return false;
                else if (dif < -9) capture = true;
            }
            else {
                if (!(abs(dif) > 9)) return false;
                else capture = true;
                if (board[move.startingSquare + dif / 2] != BLACK_CHECKER) return false;
                if (move.startingSquare != streakingSquare) return false;
            }
        }
        return true;
    }
    /** checks whether the colour has lost already */
    private boolean loseCheck(int colour) {
        Move move = new Move();
        if (colour == WHITE_CHECKER) move.colour = false;
        else if (colour == BLACK_CHECKER) move.colour = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == colour) {
                move.startingSquare = i;
                if (move.colour) {
                    if(onTheStreak) {
                        move.endingSquare = i - 14;
                        if (ruleCheck(move)) return false;
                        move.endingSquare = i - 18;
                        if (ruleCheck(move)) return false;
                    }
                    move.endingSquare = i + 7;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + 9;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + 14;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + 18;
                }
                else {
                    if (onTheStreak) {
                        move.endingSquare = i + 14;
                        if (ruleCheck(move)) return false;
                        move.endingSquare = i + 18;
                        if (ruleCheck(move)) return false;
                    }
                    move.endingSquare = i - 7;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - 9;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - 14;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - 18;
                }
                if (ruleCheck(move)) return false;
            }
        }
        return true;
    }
    private boolean captureCheck(int square) {
        int file = square % 8;
        if (square + 14 < BOARD_SIZE && file > 1) {
            if (blacksMove) {
                if (board[square + 7] == WHITE_CHECKER && board[square + 14] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square + 7] == BLACK_CHECKER && board[square + 14] == EMPTY_SQUARE)
                    return true;
            }
        }
        if (square + 18 < BOARD_SIZE && file < 6) {
            if (blacksMove) {
                if (board[square + 9] == WHITE_CHECKER && board[square + 18] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square + 9] == BLACK_CHECKER && board[square + 18] == EMPTY_SQUARE)
                    return true;
            }
        }
        if (square - 14 > 0 && file < 6) {
            if (!blacksMove) {
                if (board[square - 7] == BLACK_CHECKER && board[square - 14] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square - 7] == WHITE_CHECKER && board[square - 14] == EMPTY_SQUARE)
                    return true;
            }
        }
        if (square - 18 > 0 && file > 1) {
            if (!blacksMove) {
                if (board[square - 9] == BLACK_CHECKER && board[square - 18] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square - 9] == WHITE_CHECKER && board[square - 18] == EMPTY_SQUARE)
                    return true;
            }
        }
        onTheStreak = false;
        streakingSquare = -1;
        return false;
    }
    private boolean captureForwardCheck(int square) {
        int file = square % 8;
        if (square + 14 < BOARD_SIZE && file > 1 && blacksMove) {
            if (board[square + 7] == WHITE_CHECKER && board[square + 14] == EMPTY_SQUARE)
                return true;
        }
        if (square + 18 < BOARD_SIZE && file < 6 && blacksMove) {
            if (board[square + 9] == WHITE_CHECKER && board[square + 18] == EMPTY_SQUARE)
                return true;
        }
        if (square - 14 >= 0 && file < 6 && !blacksMove) {
            if (board[square - 7] == BLACK_CHECKER && board[square - 14] == EMPTY_SQUARE)
                return true;
        }
        if (square - 18 >= 0 && file > 1 && !blacksMove) {
            if (board[square - 9] == BLACK_CHECKER && board[square - 18] == EMPTY_SQUARE)
                return true;
        }
        capture = false;
        return false;
    }
    private boolean captureForwardAllCheck(int checker) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == checker) {
                if (captureForwardCheck(i)) return true;
            }
        }
        return false;
    }

    private int checkerOfTheOtherColour(boolean colour) {
        if (colour) return BLACK_CHECKER;
        else return WHITE_CHECKER;
    }

    private void readFen() {
        int counter = 0;
        char[] FENArray = FEN.toCharArray();
        char[] square = new char[2];
        int s;
        int i;
        emptyBoard();
        for (char c: FENArray) {
            if (counter == 0) {
                if (c == 'W') blacksMove = false;
                else if (c == 'B') blacksMove = true;
            }
            else if (c == 'W') {
                for (int j = counter + 1; FENArray[j] != ':'; j++) {
                    if (FENArray[j] == ',') continue;
                    square[1] = '/';
                    for (i = j; FENArray[i] != ',' && FENArray[i] != ':'; i++) {
                        square[i - j] = FENArray[i];
                    }
                    s = interpretSquare(square);
                    board[s] = WHITE_CHECKER;
                    j = i - 1;
                }
            }
            else if (c == 'B') {
                for (int j = counter + 1; FENArray[j] != ':'; j++) {
                    if (FENArray[j] == ',') continue;
                    square[1] = '/';
                    for (i = j; FENArray[i] != ',' && FENArray[i] != ':'; i++) {
                        square[i - j] = FENArray[i];
                    }
                    s = interpretSquare(square);
                    board[s] = BLACK_CHECKER;
                    j = i - 1;
                }
            }
            counter++;
        }
    }
    private void makeFEN() {
        StringBuilder s = new StringBuilder();
        if (blacksMove) s.append('B');
        else s.append('W');
        s.append(":W");
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == WHITE_CHECKER) {
                s.append(interpretSquare(i));
                s.append(',');
            }
        }
        if (s.lastIndexOf(",") != -1) s.deleteCharAt(s.lastIndexOf(","));
        s.append(":B");
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == BLACK_CHECKER) {s.append(interpretSquare(i)); s.append(',');}
        }
        if (s.lastIndexOf(",") != -1) s.deleteCharAt(s.lastIndexOf(","));
        s.append(':');
        FEN = s.toString();
    }
    private int interpretSquare(int square) {
        if ((square / 8) % 2 == 1) square++;
        square = (square + 1) / 2;
        return square;
    }
    private int interpretSquare(char[] square) {
        int s = 0;
        if (square[0] - 48 == -1) return -1;
        if (square[1] - 48 != -1) s += (square[0] - 48) * 10 + (square[1] - 48);
        else s += square[0] - 48;
        s = (2 * s) - 1;
        if ((s / 8) % 2 == 1) s--;
        return s;
    }
    /**
     * returns all available legal move in the position
     * @return an array of Move objects
     * */
    public Move[] getLegalMoves() {
        Move[] moves = new Move[30];
        int counter = 0;
        Move move = new Move();
        move.colour = blacksMove;
        if (blacksMove) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == BLACK_CHECKER) {
                    move.startingSquare = i;
                    if(onTheStreak) {
                        move.endingSquare = i - 14;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                        move.endingSquare = i - 18;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    }
                    move.endingSquare = i + 7;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i + 9;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i + 14;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i + 18;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                }
            }
        }
        else {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == WHITE_CHECKER) {
                    move.startingSquare = i;
                    if(onTheStreak) {
                        move.endingSquare = i + 14;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                        move.endingSquare = i + 18;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    }
                    move.endingSquare = i - 7;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i - 9;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i - 14;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i - 18;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                }
            }
        }
        return moves;
    }

    /**
     * Returns all possible captures.
     * @return an array of all possible captures
     */
    public Move[] getCaptureMoves() {
        Move[] moves = new Move[30];
        int counter = 0;
        Move move = new Move();
        move.colour = blacksMove;
        if (blacksMove) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == BLACK_CHECKER) {
                    move.startingSquare = i;
                    if(onTheStreak) {
                        move.endingSquare = i - 14;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                        move.endingSquare = i - 18;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    }
                    move.endingSquare = i + 14;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i + 18;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                }
            }
        }
        else {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == WHITE_CHECKER) {
                    move.startingSquare = i;
                    if(onTheStreak) {
                        move.endingSquare = i + 14;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                        move.endingSquare = i + 18;
                        if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    }
                    move.endingSquare = i - 14;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                    move.endingSquare = i - 18;
                    if (ruleCheck(move)) {moves[counter] = move.clone(); counter++;}
                }
            }
        }
        return moves;
    }
    /** prints out fields for debugging purposes */
    public void debugPrint() {
        System.out.println("black's move = " + blacksMove + "\n"
        + "capture = " + capture + "\n" + "streak = " + onTheStreak + "\n"
        + "game state = " + gameState + "\n" + "streaking square = " + streakingSquare);
        System.out.println("FEN is " + FEN);
        System.out.println();

    }
    /** returns the piece that's on the provided square
     * @param square a square being inquired.
     * */
    public int checkChecker(int square) {return board[square];}
    /**
     * returns the board array
     * @return board field of the class.
     * */
    public int[] getBoard() {
        return board;
    }
    /**
     * checks whether it's black's move
     * @return true if black's move, false otherwise
     * */
    public boolean isBlacksMove() {
        return blacksMove;
    }
    /**
     * gives the state of the game
     * @return 1 if black won, 0 if neither (game goes on), -1 if white won
     * */
    public int getGameState() {
        return gameState;
    }
    /**
     * Getter. Returns the onTheStreak field
     * @return onTheStreak
     * */
    public boolean isOnTheStreak() {
        return onTheStreak;
    }
    /**
     * Getter returns the FEN of the current position
     * @return FEN field
     * */
    public String getFEN() {return FEN;}
    /**
     * Getter. Returns the number if ply since the start of the game
     * @return plyCounter
     * */
    public int getPlyCounter() {
        return plyCounter;
    }

    public boolean isCapture() {
        return capture;
    }
}