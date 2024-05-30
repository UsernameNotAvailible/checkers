package Core;

/**
 * A class representing a move on a board
 * @author Artem Tarnavskyi
 * @version 0.1
 */

public class Move {
    /** A square from which the piece moves */
    public int startingSquare;
    /** A square on which the piece moves */
    public int endingSquare;
    /** Colour of the piece moving */
    public boolean colour;
    /** Constructor Move(), takes no arguments, sets starting and ending squares to 0 by default, and colour to white */
    public Move() {
        startingSquare = 0;
        endingSquare = 0;
        colour = false;
    }

    /**
     * Constructor. Assigns fields
     * @param startingSquare assigns starting square field
     * @param endingSquare assigns ending square field
     * @param colour assigns colour field
     */
    public Move(int startingSquare, int endingSquare, boolean colour) {
        this.startingSquare = startingSquare;
        this.endingSquare = endingSquare;
        this.colour = colour;
    }

    /** toString() method, overriding the parent's one, for easier debugging. */
    @Override
    public String toString() {
        String colour;
        if (this.colour) colour = "black";
        else colour = "white";
        return startingSquare + " " + endingSquare + " " + colour;
    }
    @Override
    public Move clone() {
        try {
            return (Move) super.clone();
        }
        catch (CloneNotSupportedException e) {
            Move move = new Move();
            move.startingSquare = this.startingSquare;
            move.endingSquare = this.endingSquare;
            move.colour = this.colour;
            return move;
        }
    }
}