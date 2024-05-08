package Core;

public class Move {
    public int startingSquare;
    public int endingSquare;
    public boolean colour;
    public Move() {
        startingSquare = 0;
        endingSquare = 0;
        colour = false;
    }

    @Override
    public String toString() {
        String colour;
        if (this.colour) colour = "black";
        else colour = "white";
        return startingSquare + " " + endingSquare + " " + colour;
    }
}
