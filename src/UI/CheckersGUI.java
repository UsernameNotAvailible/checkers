package UI;

import Core.CheckersComputerPlayer;
import Core.CheckersLogic;
import Core.Move;
import Core.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * A class providing an interface to play the game. It makes a board appear on the screen.
 * @author Artem Tarnavskyi
 * @version 0.1
 */
public class CheckersGUI extends JFrame implements Size {
    private CheckersLogic game;
    private CheckersComputerPlayer engine;
    private static Container checkersBoard = new JPanel();
    private JButton[] squares = new JButton[BOARD_SIZE];
    private JPanel buttonPanel = new JPanel();
    private JButton[] bottomButtons = new JButton[3];
    private Color colour = Color.DARK_GRAY;
    private int clickCounter = 0;
    private Move move = new Move();
    private boolean engineEnabled = false;
    private boolean colourOfThePlayer = false;
    private boolean boardTurnedAround = false;
    private boolean newMoveAvailable = false;
    private boolean restartingTheEngine = false;
    final Thread thread = new Thread(new BoardUpdater());
    /** Default constructor. It sets up the board in a default position. Takes no arguments.*/
    public CheckersGUI() {
        super("Checkers Board");
        game = new CheckersLogic();
        setBoard();
        thread.start();
    }
    /**
     * Starts a game against the computer. Chooses the colour of the player.
     * @param colourOfThePlayer boolean colour of the player. True = black, False = white.
     * */
    public CheckersGUI(boolean colourOfThePlayer) {
        super("Checkers Board");
        boardTurnedAround = colourOfThePlayer;
        this.engineEnabled = true;
        engine = new CheckersComputerPlayer();
        this.colourOfThePlayer = colourOfThePlayer;
        game = new CheckersLogic();
        setBoard();
        if (colourOfThePlayer) {
            try {
                Thread.sleep(1000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            makeEngineMove();
        }
        thread.start();
    }
    /**
     * Takes a FEN string and arranges the starting position accordingly.
     * @param FEN FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * */
    public CheckersGUI(String FEN) {
        super("Checkers Board");
        game = new CheckersLogic(FEN);
        setBoard();
        thread.start();
    }
    /**
     * Takes the colour of the player and a FEN string.
     * @param FEN FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * @param colourOfThePlayer boolean representing the colour of the player. True = black. False = white.
     * */
    public CheckersGUI(String FEN, boolean colourOfThePlayer) {
        super("Checkers Board");
        boardTurnedAround = colourOfThePlayer;
        this.engineEnabled = true;
        this.colourOfThePlayer = colourOfThePlayer;
        engine = new CheckersComputerPlayer(FEN);
        game = new CheckersLogic(FEN);
        setBoard();
        if (colourOfThePlayer != game.isBlacksMove()) {
            makeEngineMove();
        }
        thread.start();
    }
    private void setBoard() {
        setLayout(new BorderLayout());
        checkersBoard.setLayout(new GridLayout(BOARD_SIDE,BOARD_SIDE));
        setButtons();
        add(checkersBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(BOARD_SIDE * 100, BOARD_SIDE * 100 + 20);
        checkersBoard.setSize(BOARD_SIDE * 100, BOARD_SIDE * 100);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPieces(game.getBoard());
        setVisible(true);
    }
    private void setButtons() {
        ButtonHandler buttonHandler = new ButtonHandler();
        for (int i = 0; i < BOARD_SIZE; i++) {
            squares[i] = new JButton();
            if (i % 2 != (i / BOARD_SIDE) % 2) {
                squares[i].setBackground(colour);
            }
            else {
                squares[i].setBackground(Color.WHITE);
            }
            squares[i].setOpaque(true);
            squares[i].setBorderPainted(false);
            checkersBoard.add(squares[i]);
            //add(squares[i][j]);
            squares[i].addActionListener(buttonHandler);

        }
        BottomButtonHandler bottomButtonHandler = new BottomButtonHandler();
        for (int i = 0; i < 3; i++) {
            bottomButtons[i] = new JButton();
            bottomButtons[i].addActionListener(bottomButtonHandler);
            buttonPanel.add(bottomButtons[i]);
        }
        bottomButtons[0].setIcon(Icons.backArrow);
        bottomButtons[1].setIcon(Icons.turnAround);
    }
    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e == null) {
                return;
            }
            Object source = e.getSource();
            for (int i = BOARD_SIZE - 1; i > -1; i--) {
                if (source == squares[i]) {
                    //System.out.print("square = " + i + "\n");
                    clickCounter = (clickCounter + 1) % 2;
                    if (clickCounter == 1) {
                        if (!boardTurnedAround) move.startingSquare = i;
                        else move.startingSquare = BOARD_SIZE - 1 - i;
                        if (game.checkChecker(move.startingSquare) == CheckersLogic.EMPTY_SQUARE) clickCounter = (clickCounter + 1) % 2;
                        else if (game.checkChecker(move.startingSquare) >> 2 == 0 && game.isBlacksMove()) clickCounter = (clickCounter + 1) % 2;
                        else if (game.checkChecker(move.startingSquare) >> 2 == 1 && !game.isBlacksMove()) clickCounter = (clickCounter + 1) % 2;
                    } else {
                        if (!boardTurnedAround) move.endingSquare = i;
                        else move.endingSquare = BOARD_SIZE - 1 - i;
                        if (game.checkChecker(move.startingSquare) >> 2 == 1) move.colour = CheckersLogic.BLACKS_MOVE;
                        else if (game.checkChecker(move.startingSquare) >> 2 == 0) move.colour = CheckersLogic.WHITES_MOVE;
                        //makeMove();
                        //Thread thread = new Thread(new BoardUpdater());
                        newMoveAvailable = true;
                        synchronized (thread) {
                            thread.notify();
                        }
                        return;
                    }
                }
            }
        }
    }
    private void makeMove() {
        System.out.println("Move is " + move);
        game.debugPrint();
        if (!engineEnabled) {
            if (game.ruleCheck(move)) {
                game.makeMove(move);
                setPieces(game.getBoard());
            }
            else {
                System.out.println("illegal move");
            }
            if (game.getGameState() == 1) {
                JOptionPane.showMessageDialog(this, "Black won");
            } else if (game.getGameState() == -1) {
                JOptionPane.showMessageDialog(this, "White won");
            }
        }
        else if (move.colour == colourOfThePlayer) {
            if (game.ruleCheck(move)) {
                game.makeMove(move);
                setPieces(game.getBoard());
                if (game.getGameState() == 0) {
                    engine.makeMove(move);
                    if (!game.isOnTheStreak()) {
                        makeEngineMove();
                    }
                }
            }
            else {
                System.out.println("illegal move");
            }
            if (game.getGameState() == 1) {
                JOptionPane.showMessageDialog(this, "Black won");
            }
            else if (game.getGameState() == -1) {
                JOptionPane.showMessageDialog(this, "White won");
            }
        }
    }
    private void makeEngineMove() {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        move = engine.returnMove();
        System.out.println("Move is " + move);
        game.debugPrint();
        System.out.println("Engine's debug print");
        System.out.println("Move is: " + move);
        engine.debugPrint();
        game.makeMove(move);
        engine.makeMove(move);
        setPieces(game.getBoard());
        //new Thread(new BoardUpdater()).start();
        if (game.isBlacksMove() != colourOfThePlayer) {
            makeEngineMove();
        }
    }
    private class BottomButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e == null) {
                return;
            }
            Object source = e.getSource();
            if (source == bottomButtons[0]) {
                game.unmakeMove();
                setPieces(game.getBoard());
                if (engineEnabled) {
                    engine.unmakeMove();
                    engine.debugPrint();
                }
            }
            if (source == bottomButtons[1]) {
                boardTurnedAround = !boardTurnedAround;
                setPieces(game.getBoard());
            }
            if (source == bottomButtons[2]) {
                if (game.isBlacksMove()!= colourOfThePlayer && engineEnabled) {
                    synchronized (thread) {
                        restartingTheEngine = true;
                        thread.notify();
                    }
                }
            }
        }
    }
    private void setPieces(int[] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            squares[i].setIcon(Icons.emptySquare);
        }
        if (!boardTurnedAround) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == CheckersLogic.WHITE_CHECKER) squares[i].setIcon(Icons.whitePiece);
                else if (board[i] == CheckersLogic.BLACK_CHECKER) squares[i].setIcon(Icons.blackPiece);
                else if (board[i] == CheckersLogic.BLACK_QUEEN) squares[i].setIcon(Icons.blackQueen);
                else if (board[i] == CheckersLogic.WHITE_QUEEN) squares[i].setIcon(Icons.whiteQueen);
            }
        }
        else {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == CheckersLogic.WHITE_CHECKER) squares[BOARD_SIZE - 1 - i].setIcon(Icons.whitePiece);
                else if (board[i] == CheckersLogic.BLACK_CHECKER) squares[BOARD_SIZE - 1 - i].setIcon(Icons.blackPiece);
                else if (board[i] == CheckersLogic.BLACK_QUEEN) squares[BOARD_SIZE - 1 - i].setIcon(Icons.blackQueen);
                else if (board[i] == CheckersLogic.WHITE_QUEEN) squares[BOARD_SIZE - 1 - i].setIcon(Icons.whiteQueen);
            }
        }
    }
    private class BoardUpdater implements Runnable {
        @Override
        public void run() {
            //setPieces(game.getBoard());
            while (true) {
                synchronized (thread) {
                    try {
                        thread.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (newMoveAvailable) {
                    makeMove();
                    newMoveAvailable = false;
                } else if (restartingTheEngine) {
                    makeEngineMove();
                }
            }
        }
    }
}