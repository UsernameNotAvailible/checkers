package Core;

import java.util.Arrays;
import java.util.Stack;

import static java.lang.Math.abs;

/**
 * A class containing the logic of a checkers game
 * @author Artem Tarnavskyi
 * @version 0.5
 */
public class CheckersLogic implements Size{
    /*public static final int BOARD_SIDE = 8;
    public static final int BOARD_SIZE = BOARD_SIDE*BOARD_SIDE;
    Black man - 101 = 5
    Black king - 110 = 6
    White man - 001 = 1
    White king - 010 = 2
    Empty square - 100 = 8
     */
    /** An array representing the board on which the game is played */
    private final int[] board = new int[BOARD_SIZE];
    private static final int[] allowableDifferences = new int[]{BOARD_SIDE - 1, -1 * (BOARD_SIDE - 1), BOARD_SIDE + 1, -1 * (BOARD_SIDE + 1), (BOARD_SIDE - 1) * 2, -1 * (BOARD_SIDE - 1) * 2, (BOARD_SIDE + 1) * 2, -1 * (BOARD_SIDE + 1) * 2};
    private static final int[] diagonalMovements = new int[]{BOARD_SIDE - 1, BOARD_SIDE + 1, -1 * (BOARD_SIDE - 1), -1 * (BOARD_SIDE + 1)};
    /** A constant representing the empty square on the board */
    public static final int EMPTY_SQUARE = 8;
    public static final int NO_SQUARE = -1;
    /** A constant representing the white checker on the board */
    public static final int WHITE_CHECKER = 1;
    /** A constant representing the black checker on the board */
    public static final int BLACK_CHECKER = 5;
    public static final int WHITE_QUEEN = 2;
    public static final int BLACK_QUEEN = 6;
    public static final boolean WHITES_MOVE = false;
    public static final boolean BLACKS_MOVE = true;
    private String FEN;
    private String previousFEN;
    private final Stack<String> FENStack = new Stack<>();
    private boolean blacksMove = false;
    private boolean capture = false;
    private boolean onTheStreak = false;
    private int gameState = 0;
    private int streakingSquare = NO_SQUARE;
    private int plyCounter = 0;
    private int moveCounter = 0;
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
        for (int i = 0; i < ((BOARD_SIDE / 2) - 1) * (BOARD_SIDE); i++) {
            if (set) board[i] = BLACK_CHECKER;
            set = !set;
            if (i % BOARD_SIDE == BOARD_SIDE - 1) set = !set;
        }
    }
    /** sets white pieces on their traditional starting squares */
    public void setWhite() {
        boolean set = false;
        for (int i = BOARD_SIZE - 1; i > (BOARD_SIZE - 1) - (((BOARD_SIDE / 2) - 1) * BOARD_SIDE); i--) {
            if (set) board[i] = WHITE_CHECKER;
            set = !set;
            if (i % BOARD_SIDE == 0) set = !set;
        }
    }
    /** prints board to the console */
    public void printBoard() {
        int counter = 0;
        int fileCounter = BOARD_SIDE;
        char checker = ' ';
        for (int i: board) {
            if(counter % BOARD_SIDE == 0) {
                System.out.print(fileCounter + "| ");
                fileCounter --;
            }
            if (i == CheckersLogic.BLACK_CHECKER) checker = 'b';
            else if (i == CheckersLogic.WHITE_CHECKER) checker = 'w';
            else if (i == CheckersLogic.EMPTY_SQUARE) checker = ' ';
            System.out.print(checker + ", ");
            counter++;
            if(counter % BOARD_SIDE == 0) {
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
        board[move.endingSquare] = board[move.startingSquare];
        board[move.startingSquare] = EMPTY_SQUARE;
        if ((move.endingSquare / BOARD_SIDE == BOARD_SIDE - 1 && board[move.endingSquare] >> 2 == 1)) board[move.endingSquare] = BLACK_QUEEN;
        else if ((move.endingSquare / BOARD_SIDE == 0 && board[move.endingSquare] >> 2 == 0)) {
            board[move.endingSquare] = WHITE_QUEEN;
        }
        int dif = move.endingSquare - move.startingSquare;
        if (abs(dif) > (BOARD_SIDE + 1) && (board[move.endingSquare] & 1) == 1) {
            board[move.startingSquare + (move.endingSquare - move.startingSquare) / 2] = EMPTY_SQUARE;
            capture = true;
        }
        if ((board[move.endingSquare] & 2) == 2 && capturedPieceExists(move.startingSquare, move.endingSquare)) {
            int beginningSquare = move.startingSquare;
            int endingSquare = move.endingSquare;
            int diagonalConstant = BOARD_SIDE - 1;
            if (move.endingSquare < move.startingSquare) {
                beginningSquare = move.endingSquare;
                endingSquare = move.startingSquare;
            }
            if (abs(move.endingSquare - move.startingSquare) % (BOARD_SIDE + 1) == 0) diagonalConstant = BOARD_SIDE + 1;
            for (int i = beginningSquare + diagonalConstant; i < endingSquare; i += diagonalConstant) {
                board[i] = EMPTY_SQUARE;
            }
            capture = true;
        }
        boolean captureAvailable = captureAvailabilityCheck(move.endingSquare);
        if (capture && captureAvailable) {
            blacksMove = !blacksMove;
            onTheStreak = true;
            streakingSquare = move.endingSquare;
        }
        else if (capture) {
            onTheStreak = false;
            streakingSquare = -1;
        }
        capture = false;
        blacksMove = !blacksMove;
        if (loseCheck()) gameState = blacksMove ? -1 : 1;
        FENStack.push(FEN);
        makeFEN();
        //capture = false;
        if (!onTheStreak) {
            if (!blacksMove) moveCounter++;
        }
        plyCounter++;
    }
    /** Unmakes move and resets some fields. Unless it's the starting position of the game, in which case it does nothing */
    public void unmakeMove() {
        if (plyCounter != 0) {
            FEN = FENStack.pop();
            readFen();
            plyCounter--;
            //moveCounter = (plyCounter / 2) + (plyCounter % 2);
            onTheStreak = false;
            gameState = 0;
        }
    }
    /**
     * takes a Move object,
     * checks whether the move is within the rules
     * @param move A move that is being attempted
     * @return true if the move is within the rules, false otherwise.
     * */
    public boolean ruleCheck(Move move) {
        if (move.endingSquare < 0 || move.endingSquare > BOARD_SIZE - 1 || move.startingSquare < 0 || move.startingSquare > BOARD_SIZE - 1) return false; // check if inside the board
        int dif = move.endingSquare - move.startingSquare;
        if (board[move.startingSquare] == EMPTY_SQUARE) return false;
        if (!withinAllowableSquares(dif) && (board[move.startingSquare] == WHITE_CHECKER || board[move.startingSquare] == BLACK_CHECKER)) return false; // check if the move is 1 or 2 squares on the diagonal
        if (move.colour != blacksMove) return false; // check if the right colour is moving
        if (board[move.endingSquare] != EMPTY_SQUARE) return false; // checks if the target square is empty
        /*if (move.startingSquare % BOARD_SIDE == 0 && (move.endingSquare % BOARD_SIDE) > 2) return false; // checks if the move goes through the edge of the board
        if (move.startingSquare % BOARD_SIDE == BOARD_SIDE - 1 && (move.endingSquare % 8) < 5) return false;
        if (move.startingSquare % BOARD_SIDE == 1 && (move.endingSquare % BOARD_SIDE) > 3) return false;
        if (move.startingSquare % BOARD_SIDE == BOARD_SIDE - 2 && (move.endingSquare % 8) < 4) return false;

         */
        if ((move.endingSquare / BOARD_SIDE) % 2 == move.endingSquare % 2 || (move.startingSquare / BOARD_SIDE) % 2 == move.startingSquare % 2) return false;
        if ((board[move.startingSquare] & 1) == 1) { // check if the moving piece is a checker
            if (abs(dif) > BOARD_SIDE + 1 && (board[move.startingSquare + dif / 2] == EMPTY_SQUARE || board[move.startingSquare + dif / 2] >> 2 == board[move.startingSquare] >> 2)) return false; // check if there's a checker being captured
            if (!onTheStreak) { // check if not on streak
                if ((blacksMove == dif < 0) && abs(dif) <= BOARD_SIDE + 1) return false; // check if it's a non-capture backwards move
                if (captureAvailabilityAllCheck() && abs(dif) <= BOARD_SIDE + 1) return false; // check if a capture is available and the move isn't a capture
                if (abs(dif) > BOARD_SIDE + 1 && board[move.startingSquare + dif / 2] >> 2 == board[move.startingSquare] >> 2) return false; // check if the captured checker is the opponent's one
                //else if (abs(dif) > BOARD_SIDE + 1) capture = true; // mark the move as a capture
            }
            else {
                if (!(abs(dif) > BOARD_SIDE + 1)) return false; // check if it's a capture
                //else capture = true; // mark the move as a capture
                if (board[move.startingSquare + dif / 2] >> 2 == board[move.startingSquare] >> 2) return false; // check if the captured checker is the opponent's one
                if (move.startingSquare != streakingSquare)
                    return false; // check if the capturing checker is the same as for the previous streak capture
                }
            }
        else { // if it's not a checker, it must be a queen (king?)
            if (!onTheStreak) { // check if not on streak
                if (captureAvailabilityAllCheck() && !capturedPieceExists(move.startingSquare, move.endingSquare))
                    return false;
                if (wayBlocked(move.startingSquare, move.endingSquare)) return false;
            }
            else {
                if (!(abs(dif) > BOARD_SIDE + 1)) return false; // check if it's a capture
                //else capture = true; // mark the move as a capture
                if (!capturedPieceExists(move.startingSquare, move.endingSquare)) return false; // check if the captured checker is the opponent's one
                if (move.startingSquare != streakingSquare) return false; // check if the capturing checker is the same as for the previous streak capture
            }
        }
        return true;
    }
    /** checks whether the colour has lost already */
    private boolean loseCheck() {
        /*Move move = new Move();
        move.colour = colour;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if ((colour ? 1: 0) == board[i] >> 2) {
                move.startingSquare = i;
                if (move.colour) {
                    move.endingSquare = i - (BOARD_SIDE - 1) * 2;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE + 1) * 2;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE - 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE + 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE - 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE + 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE - 1) * 2;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE + 1) * 2;
                }
                else {
                    move.endingSquare = i + (BOARD_SIDE - 1) * 2;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE + 1) * 2;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE - 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE + 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE - 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + (BOARD_SIDE + 1);
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE - 1) * 2;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i - (BOARD_SIDE + 1) * 2;
                }
                if (ruleCheck(move)) return false;
            }
        }
        return true;

         */
        return getLegalMoves()[0] == null;
    }
    private boolean captureCheck(int square) {
        int file = square % BOARD_SIDE;
        if (square + (BOARD_SIDE - 1) * 2 < BOARD_SIZE && file > 1) {
            if (blacksMove) {
                if (board[square + (BOARD_SIDE - 1)] == WHITE_CHECKER && board[square + (BOARD_SIDE - 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square + (BOARD_SIDE - 1)] == BLACK_CHECKER && board[square + (BOARD_SIDE - 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
        }
        if (square + (BOARD_SIDE + 1) * 2 < BOARD_SIZE && file < BOARD_SIDE - 2) {
            if (blacksMove) {
                if (board[square + (BOARD_SIDE + 1)] == WHITE_CHECKER && board[square + (BOARD_SIDE + 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square + (BOARD_SIDE + 1)] == BLACK_CHECKER && board[square + (BOARD_SIDE + 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
        }
        if (square - (BOARD_SIDE - 1) * 2 > 0 && file < BOARD_SIDE - 2) {
            if (!blacksMove) {
                if (board[square - (BOARD_SIDE - 1)] == BLACK_CHECKER && board[square - (BOARD_SIDE - 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square - (BOARD_SIDE - 1)] == WHITE_CHECKER && board[square - (BOARD_SIDE - 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
        }
        if (square - (BOARD_SIDE + 1) * 2 > 0 && file > 1) {
            if (!blacksMove) {
                if (board[square - (BOARD_SIDE + 1)] == BLACK_CHECKER && board[square - (BOARD_SIDE + 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
            else {
                if (board[square - (BOARD_SIDE + 1)] == WHITE_CHECKER && board[square - (BOARD_SIDE + 1) * 2] == EMPTY_SQUARE)
                    return true;
            }
        }
        onTheStreak = false;
        streakingSquare = NO_SQUARE;
        return false;
    }
    private boolean captureAvailabilityCheck(int square) {
        int checker = board[square];
        int squareChecked;
        if ((checker & 1) == 1) {
            for (int diagonalConstant: diagonalMovements) {
                squareChecked = square + diagonalConstant;
                if (squareChecked + diagonalConstant >= 0 && squareChecked + diagonalConstant < BOARD_SIZE && ((squareChecked + diagonalConstant) / BOARD_SIDE) % 2 != (squareChecked + diagonalConstant) % 2) {
                    if (board[squareChecked] != EMPTY_SQUARE && board[squareChecked] >> 2 != checker >> 2 && board[squareChecked + diagonalConstant] == EMPTY_SQUARE)
                        return true;
                }
            }
        }
        else if ((checker & 2) >> 1 == 1) {
            int counter;
            for (int diagonalConstant: diagonalMovements) {
                for (int i = 1; i <= BOARD_SIDE - 2; i++) {
                    counter = 0;
                    for (int j = i - 1; j > 0; j--) {
                        if (square + i * diagonalConstant < BOARD_SIZE && square + i * diagonalConstant >= 0 && board[square + i * diagonalConstant] == EMPTY_SQUARE) {
                            int i1 = square + i * diagonalConstant - j * diagonalConstant;
                            if (board[i1] != EMPTY_SQUARE && board[i1] >> 2 != checker >> 2) {
                                counter++;
                            }
                            else if (board[i1] != EMPTY_SQUARE) {
                                counter += 2;
                            }
                        }
                    }
                    if (counter == 1 && board[square + i * diagonalConstant] == EMPTY_SQUARE) return true;
                }
            }
        }
        //capture = false;
        return false;
    }
    private int getOpponentsChecker() {
        if (blacksMove) return WHITE_CHECKER;
        else return BLACK_CHECKER;
    }
    private boolean captureAvailabilityAllCheck() {
        for (int i = 0; i < board.length; i++) {
            if (board[i] != EMPTY_SQUARE && board[i] >> 2 == (blacksMove ? 1 : 0)) {
                if (captureAvailabilityCheck(i)) return true;
            }
        }
        return false;
    }

    private boolean wayBlocked(int startingSquare, int endingSquare) {
        int dif = abs(endingSquare - startingSquare);
        int numberOfSquaresToCheck;
        int firstSquare = startingSquare;
        int lastSquare = endingSquare;
        int counter = 0;
        if (endingSquare < startingSquare) {
            firstSquare = endingSquare;
            lastSquare = startingSquare;
        }
        int diagonalConstant = (BOARD_SIDE + 1);
        if (dif % (BOARD_SIDE - 1) == 0) {
            diagonalConstant = (BOARD_SIDE - 1);
        }
        for (int i = firstSquare + diagonalConstant; i < lastSquare; i += diagonalConstant) {
            if (board[i] != EMPTY_SQUARE) {
                counter++;
                if (board[startingSquare] >> 2 == board[i] >> 2) counter++;
            }

        }
        return !(counter <= 1);
    }
    private boolean capturedPieceExists(int startingSquare, int endingSquare) {
        int dif = abs(endingSquare - startingSquare);
        int numberOfSquaresToCheck;
        int firstSquare = startingSquare;
        int lastSquare = endingSquare;
        int counter = 0;
        if (endingSquare < startingSquare) {
            firstSquare = endingSquare;
            lastSquare = startingSquare;
        }
        int diagonalConstant = (BOARD_SIDE + 1);
        if (dif % (BOARD_SIDE - 1) == 0) {
            diagonalConstant = (BOARD_SIDE - 1);
        }
        for (int i = firstSquare + diagonalConstant; i < lastSquare; i += diagonalConstant) {
            if (board[i] != EMPTY_SQUARE) {
                counter++;
                if (board[startingSquare] >> 2 == board[i] >> 2) {
                    counter++;
                }
            }

        }
        return (counter == 1);
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
        boolean king = false;
        emptyBoard();
        for (char c: FENArray) {
            if (counter == 0) {
                if (c == 'W') blacksMove = false;
                else if (c == 'B') blacksMove = true;
            }
            else if (c == 'W') {
                for (int j = counter + 1; FENArray[j] != ':'; j++) {
                    if (FENArray[j] == ',') continue;
                    if (FENArray[j] == 'K') {
                        king = true;
                        continue;
                    }
                    square[1] = '/';
                    for (i = j; FENArray[i] != ',' && FENArray[i] != ':'; i++) {
                        square[i - j] = FENArray[i];
                    }
                    s = interpretSquare(square);
                    if (king) {
                        board[s] = WHITE_QUEEN;
                        king = false;
                    }
                    else {
                        board[s] = WHITE_CHECKER;
                    }
                    j = i - 1;
                }
            }
            else if (c == 'B') {
                for (int j = counter + 1; FENArray[j] != ':'; j++) {
                    if (FENArray[j] == ',') continue;
                    if (FENArray[j] == 'K') {
                        king = true;
                        continue;
                    }
                    square[1] = '/';
                    for (i = j; FENArray[i] != ',' && FENArray[i] != ':'; i++) {
                        square[i - j] = FENArray[i];
                    }
                    s = interpretSquare(square);
                    if (king) {
                        board[s] = BLACK_QUEEN;
                        king = false;
                    }
                    else {
                        board[s] = BLACK_CHECKER;
                    }
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
            else if (board[i] == WHITE_QUEEN) {
                s.append('K');
                s.append(interpretSquare(i));
                s.append(',');
            }
        }
        if (s.lastIndexOf(",") != -1) s.deleteCharAt(s.lastIndexOf(","));
        s.append(":B");
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == BLACK_CHECKER) {s.append(interpretSquare(i)); s.append(',');
            }
            else if (board[i] == BLACK_QUEEN) {
                s.append('K');
                s.append(interpretSquare(i));
                s.append(',');
            }
        }
        if (s.lastIndexOf(",") != -1) s.deleteCharAt(s.lastIndexOf(","));
        s.append(':');
        FEN = s.toString();
    }
    private int interpretSquare(int square) {
        if ((square / BOARD_SIDE) % 2 == 1) square++;
        square = (square + 1) / 2;
        return square;
    }
    private int interpretSquare(char[] square) {
        int s = 0;
        if (square[0] - 48 == -1) return -1;
        if (square[1] - 48 != -1) s += (square[0] - 48) * 10 + (square[1] - 48);
        else s += square[0] - 48;
        s = (2 * s) - 1;
        if ((s / BOARD_SIDE) % 2 == 1) s--;
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
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] != EMPTY_SQUARE && board[i] >> 2 == (blacksMove ? 1 : 0)) {
                move.startingSquare = i;
                if ((board[i] & 1) == 1) {
                    for (int diagonalConstant : allowableDifferences) {
                        move.endingSquare = i + diagonalConstant;
                        if (ruleCheck(move)) {
                            moves[counter] = move.clone();
                            counter++;
                        }
                    }
                }
                else if ((board[i] & 2) == 2) {
                    for (int diagonalConstant : diagonalMovements) {
                        for (int j = 1; j < BOARD_SIDE - 1; j++) {
                            move.endingSquare = i + j * diagonalConstant;
                            if (ruleCheck(move)) {
                                moves[counter] = move.clone();
                                counter++;
                            }
                        }
                    }
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
        if (captureAvailabilityAllCheck()) moves = getLegalMoves();
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