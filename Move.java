package model;

import java.io.Serializable;

public class Move implements Serializable {
    private Piece piece;
    private Position fromPos;
    private Position toPos;
    private Piece capturedPiece;

    public Move(Piece piece, Position fromPos, Position toPos, Piece capturedPiece) {
        this.piece = piece;
        this.fromPos = fromPos;
        this.toPos = toPos;
        this.capturedPiece = capturedPiece;
    }

    // Getters
    public Piece getPiece() { return piece; }
    public Position getFromPos() { return fromPos; }
    public Position getToPos() { return toPos; }
    public Piece getCapturedPiece() { return capturedPiece; }
}