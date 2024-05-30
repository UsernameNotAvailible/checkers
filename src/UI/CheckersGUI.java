package UI;

import Core.CheckersComputerPlayer;
import Core.CheckersLogic;
import Core.Move;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * A class providing an interface to play the game. It makes a board appear on the screen.
 * @author Artem Tarnavskyi
 * @version 0.1
 */
public class CheckersGUI extends JFrame {
    private CheckersLogic game;
    private CheckersComputerPlayer engine;
    private static Container checkersBoard = new JPanel();
    private JButton[] squares = new JButton[64];
    private JPanel buttonPanel = new JPanel();
    private JButton[] bottomButtons = new JButton[2];
    private Color colour = Color.DARK_GRAY;
    private int clickCounter = 0;
    private Move move = new Move();
    private boolean engineEnabled = false;
    private boolean colourOfThePlayer = false;
    private boolean boardTurnedAround = false;
    /** Default constructor. It sets up the board in a default position. Takes no arguments.*/
    public CheckersGUI() {
        super("Checkers Board");
        game = new CheckersLogic();
        setLayout(new BorderLayout());
        checkersBoard.setLayout(new GridLayout(8,8));
        setButtons();
        add(checkersBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(800, 820);
        checkersBoard.setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPieces(game.getBoard());
        setVisible(true);
    }
    /**
     * Starts a game against the computer. Chooses the colour of the player.
     * @param colourOfThePlayer boolean colour of the player. True = black, False = white.
     * */
    public CheckersGUI(boolean colourOfThePlayer) {
        super("Checkers Board");
        this.engineEnabled = true;
        engine = new CheckersComputerPlayer();
        this.colourOfThePlayer = colourOfThePlayer;
        game = new CheckersLogic();
        setLayout(new BorderLayout());
        checkersBoard.setLayout(new GridLayout(8,8));
        setButtons();
        add(checkersBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(800, 820);
        checkersBoard.setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPieces(game.getBoard());
        setVisible(true);
    }
    /**
     * Takes a FEN string and arranges the starting position accordingly.
     * @param FEN FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * */
    public CheckersGUI(String FEN) {
        super("Checkers Board");
        game = new CheckersLogic(FEN);
        setLayout(new BorderLayout());
        checkersBoard.setLayout(new GridLayout(8,8));
        setButtons();
        add(checkersBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(800, 820);
        checkersBoard.setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPieces(game.getBoard());
        setVisible(true);
    }
    /**
     * Takes the colour of the player and a FEN string.
     * @param FEN FEN string. Format [Colour]:[W][square],[square]...,[square]:[B][square],[square],...,[square]:. Does not include the number of moves since the start of the game.
     * @param colourOfThePlayer boolean representing the colour of the player. True = black. False = white.
     * */
    public CheckersGUI(String FEN, boolean colourOfThePlayer) {
        super("Checkers Board");
        this.engineEnabled = true;
        this.colourOfThePlayer = colourOfThePlayer;
        engine = new CheckersComputerPlayer(FEN);
        game = new CheckersLogic(FEN);
        setLayout(new BorderLayout());
        checkersBoard.setLayout(new GridLayout(8,8));
        setButtons();
        add(checkersBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setSize(800, 820);
        checkersBoard.setSize(800, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPieces(game.getBoard());
        setVisible(true);
    }
    private void setButtons() {
        ButtonHandler buttonHandler = new ButtonHandler();
        for (int i = 0; i < 64; i++) {
            squares[i] = new JButton();
            if (((i % 2 == 1) && ((i / 8) % 2 == 0)) || (i % 2 == 0) && ((i / 8) % 2 == 1)) {
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
        for (int i = 0; i < 2; i++) {
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
            for (int i = 63; i > -1; i--) {
                if (source == squares[i]) {
                    //System.out.print("square = " + i + "\n");
                    clickCounter = (clickCounter + 1) % 2;
                    if (clickCounter == 1) {
                        if (!boardTurnedAround) move.startingSquare = i;
                        else move.startingSquare = 63 - i;
                        if (game.checkChecker(move.startingSquare) == CheckersLogic.EMPTY_SQUARE) clickCounter = (clickCounter + 1) % 2;
                        else if (game.checkChecker(move.startingSquare) == CheckersLogic.WHITE_CHECKER && game.isBlacksMove()) clickCounter = (clickCounter + 1) % 2;
                        else if (game.checkChecker(move.startingSquare) == CheckersLogic.BLACK_CHECKER && !game.isBlacksMove()) clickCounter = (clickCounter + 1) % 2;
                    } else {
                        if (!boardTurnedAround) move.endingSquare = i;
                        else move.endingSquare = 63 - i;
                        if (game.checkChecker(move.startingSquare) == CheckersLogic.BLACK_CHECKER) move.colour = CheckersLogic.BLACKS_MOVE;
                        else if (game.checkChecker(move.startingSquare) == CheckersLogic.WHITE_CHECKER) move.colour = CheckersLogic.WHITES_MOVE;
                        makeMove();
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
                engine.makeMove(move);
                makeEngineMove();
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
        move = engine.returnMove();
        game.makeMove(move);
        engine.makeMove(move);
        setPieces(game.getBoard());
        if (game.isBlacksMove() != colourOfThePlayer) makeEngineMove();
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
            }
            if (source == bottomButtons[1]) {
                boardTurnedAround = !boardTurnedAround;
                setPieces(game.getBoard());
            }
        }
    }
    private void setPieces(int[] board) {
        for (int i = 0; i < 64; i++) {
            squares[i].setIcon(Icons.emptySquare);
        }
        if (!boardTurnedAround) {
            for (int i = 0; i < 64; i++) {
                if (board[i] == CheckersLogic.WHITE_CHECKER) squares[i].setIcon(Icons.whitePiece);
                else if (board[i] == CheckersLogic.BLACK_CHECKER) squares[i].setIcon(Icons.blackPiece);
            }
        }
        else {
            for (int i = 0; i < 64; i++) {
                if (board[i] == CheckersLogic.WHITE_CHECKER) squares[63 - i].setIcon(Icons.whitePiece);
                else if (board[i] == CheckersLogic.BLACK_CHECKER) squares[63 - i].setIcon(Icons.blackPiece);
            }
        }
    }
}