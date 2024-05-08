package Core;

import java.util.Arrays;

public class CheckersLogic {
    private int[] board = new int[64];
    private static final int[] allowableDifferences = new int[]{7, -7, 9, -9, 14, -14, 18, -18};
    public static final int EMPTY_SQUARE = 0;
    public static final int WHITE_CHECKER = 1;
    public static final int BLACK_CHECKER = 2;
    private boolean blacksMove = false;
    private boolean capture = false;
    private boolean onTheStreak = false;
    private int gameState = 0;
    public CheckersLogic() {
        emptyBoard();
        setBlack();
        setWhite();
    }

    public void emptyBoard() {
        Arrays.fill(board, EMPTY_SQUARE);
    }
    public void setBlack() {
        boolean set = false;
        for (int i = 0; i < 24; i++) {
            if (set) board[i] = BLACK_CHECKER;
            set = !set;
            if (i % 8 == 7) set = !set;
        }
    }
    public void setWhite() {
        boolean set = false;
        for (int i = 63; i > 39; i--) {
            if (set) board[i] = WHITE_CHECKER;
            set = !set;
            if (i % 8 == 0) set = !set;
        }
    }
    public void printBoard() {
        int counter = 0;
        for (int i: board) {
            System.out.print(i + ", ");
            counter++;
            if(counter % 8 == 0) {
                System.out.println();
            }
        }
    }
    private boolean withinAllowableSquares(int dif) {
        for (int i: allowableDifferences) {
            if (i == dif) return true;
        }
        return false;
    }
    public void makeMove(Move move) {
        if (!ruleCheck(move)) return;
        board[move.startingSquare] = EMPTY_SQUARE;
        if (move.colour) board[move.endingSquare] = BLACK_CHECKER;
        else board[move.endingSquare] = WHITE_CHECKER;
        if (capture) {
            board[move.startingSquare + (move.endingSquare - move.startingSquare) / 2] = EMPTY_SQUARE;
            if (captureCheck(move.endingSquare)) {
                blacksMove = !blacksMove;
                onTheStreak = true;
            }
        }
        blacksMove = !blacksMove;
        if (isBlacksMove()) {
            if (loseCheck(CheckersLogic.BLACK_CHECKER)) gameState = -1;
        }
        else {
            if (loseCheck(CheckersLogic.WHITE_CHECKER)) gameState = 1;
        }
    }
    public boolean ruleCheck(Move move) {
        //boolean result = true;
        int dif = move.endingSquare - move.startingSquare;
        if (!withinAllowableSquares(dif)) return false;
        if (move.colour != blacksMove) return false;
        if (board[move.endingSquare] != EMPTY_SQUARE) return false;
        if (captureForwardCheck(move.startingSquare) && Math.abs(dif) <= 9) return false;
        if (move.startingSquare % 8 == 0 && (move.endingSquare % 8) > 2) return false;
        if (move.startingSquare % 8 == 7 && (move.endingSquare % 8) < 5) return false;
        if (move.colour) {
            if (!onTheStreak) {
                if (dif < 0) return false;
                if (dif > 9 && board[move.startingSquare + dif / 2] != WHITE_CHECKER) return false;
                else if (dif > 9) capture = true;
                else return board[move.endingSquare] == EMPTY_SQUARE;
            }
            else {
                if (!(Math.abs(dif) > 9)) return false;
                else capture = true;
                if (board[move.startingSquare + dif / 2] != WHITE_CHECKER) return false;
            }
        }
        else {
            if (!onTheStreak) {
                if (dif > 0) return false;
                if (dif < -9 && board[move.startingSquare + dif / 2] != BLACK_CHECKER) return false;
                else if (dif < -9) capture = true;
                else return board[move.endingSquare] == EMPTY_SQUARE;
            }
            else {
                if (!(Math.abs(dif) > 9)) return false;
                else capture = true;
                if (board[move.startingSquare + dif / 2] != BLACK_CHECKER) return false;
            }
        }
        return true;
    }
    public boolean loseCheck(int colour) {
        Move move = new Move();
        if (colour == WHITE_CHECKER) move.colour = false;
        else if (colour == BLACK_CHECKER) move.colour = true;
        for (int i = 0; i < 64; i++) {
            if (board[i] == colour) {
                move.startingSquare = i;
                if (move.colour) {
                    move.endingSquare = i + 7;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + 9;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + 14;
                    if (ruleCheck(move)) return false;
                    move.endingSquare = i + 18;
                }
                else {
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
        if (blacksMove) {
            return (board[square + 7] == WHITE_CHECKER && board[square + 14] == EMPTY_SQUARE) || (board[square + 9] == WHITE_CHECKER && board[square + 18] == EMPTY_SQUARE) || (board[square - 7] == WHITE_CHECKER && board[square - 14] == EMPTY_SQUARE) || (board[square - 9] == WHITE_CHECKER && board[square - 18] == EMPTY_SQUARE);
        }
        else {
            return (board[square + 7] == BLACK_CHECKER && board[square + 14] == EMPTY_SQUARE) || (board[square + 9] == BLACK_CHECKER && board[square + 18] == EMPTY_SQUARE) || (board[square - 7] == BLACK_CHECKER && board[square - 14] == EMPTY_SQUARE) || (board[square - 9] == BLACK_CHECKER && board[square - 18] == EMPTY_SQUARE);
        }
    }
    private boolean captureForwardCheck(int square) {
        if (blacksMove) return (board[square + 7] == WHITE_CHECKER && board[square + 14] == EMPTY_SQUARE) || (board[square + 9] == WHITE_CHECKER && board[square + 18] == EMPTY_SQUARE);
        else return (board[square - 7] == BLACK_CHECKER && board[square - 14] == EMPTY_SQUARE) || (board[square + 9] == BLACK_CHECKER && board[square - 18] == EMPTY_SQUARE);
    }
    public int checkChecker(int square) {return board[square];}

    public int[] getBoard() {
        return board;
    }

    public boolean isBlacksMove() {
        return blacksMove;
    }

    public void setBlacksMove(boolean blacksMove) {
        this.blacksMove = blacksMove;
    }

    public int getGameState() {
        return gameState;
    }

    public static void main(String[] args) {
        CheckersLogic c = new CheckersLogic();
        c.emptyBoard();
        c.setBlack();
        c.setWhite();
        c.printBoard();
    }

}
