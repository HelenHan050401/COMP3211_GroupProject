package model;

/*
 * 1. The properties of pieces
 * 2. The movements
 * 3. output toString
 * */
public class Piece {
    private final AnimalsWeight weight;
    private boolean isAlive;
    private boolean isRed;
    private final Player owner; // indicate the owner of the piece
    private Position position; // current position

    public Piece(AnimalsWeight weight, Player owner, Position startPoint, boolean isRed) {
        this.weight = weight;
        this.owner = owner;
        this.position = startPoint;
        this.isAlive = true;
        this.isRed = true; // suppose red = P1
    }

    public AnimalsWeight getWeight() {
        return weight;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void moveTo(Position newPosition) {
        this.position = newPosition;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isRed() {
        return isRed;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return owner.getPlayerName() + " - " + weight.getName() + ": " + position;
    }
}
