package model;

public class GameRule {

    // Check if the position is within the board's bounds
    public static boolean isPositionValid(Position pos) {
        return pos.getRow() >= 0 && pos.getRow() < Board.ROWS &&
                pos.getCol() >= 0 && pos.getCol() < Board.COLS;
    }

    // Check if two positions are adjacent (up, down, left, right)
    public static boolean isAdjacent(Position from, Position to) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    // Check if a piece can cross (enter) river terrain
    public static boolean canCrossRiver(Piece piece) {
        AnimalsWeight weight = piece.getWeight();
        return weight == AnimalsWeight.LION || weight == AnimalsWeight.TIGER;
    }

    // Check if the position is a trap for the given player (affects their pieces)
    public static boolean isTrapForPlayer(Position pos, Player player, Board board) {
        Terrain terrain = board.getTerrain()[pos.getRow()][pos.getCol()];
        if (player == Player.P1) {
            return terrain == Terrain.BLUE_TRAP; // P1's pieces are affected by BLUE_TRAP
        } else {
            return terrain == Terrain.RED_TRAP; // P2's pieces are affected by RED_TRAP
        }
    }

    // Check if the attacker can capture the defender
    public static boolean canCapture(Piece attacker, Piece defender, Board board) {
        // Defender in their own trap (affected by it) can be captured by any attacker
        if (isTrapForPlayer(defender.getPosition(), defender.getOwner(), board)) {
            return true;
        }

        AnimalsWeight aWeight = attacker.getWeight();
        AnimalsWeight dWeight = defender.getWeight();

        // Special case: Rat can capture Elephant, Elephant cannot capture Rat
        if (aWeight == AnimalsWeight.RAT && dWeight == AnimalsWeight.ELEPHANT) {
            return true;
        }
        if (aWeight == AnimalsWeight.ELEPHANT && dWeight == AnimalsWeight.RAT) {
            return false;
        }

        // Normal case: Higher weight captures lower weight
        return aWeight.getWeight() > dWeight.getWeight();
    }

    // Main method to validate a move
    public static boolean isMoveValid(Board board, Piece piece, Position newPos) {
        // Check if the piece is alive
        if (!piece.isAlive()) {
            return false;
        }

        // Check if new position is within board bounds
        if (!isPositionValid(newPos)) {
            return false;
        }

        Position currentPos = piece.getPosition();

        // Check if moving to the same position
        if (currentPos.equals(newPos)) {
            return false;
        }

        // Check if movement is adjacent
        if (!isAdjacent(currentPos, newPos)) {
            return false;
        }

        // Check terrain at new position
        Terrain newTerrain = board.getTerrain()[newPos.getRow()][newPos.getCol()];

        // Check river traversal permission
        if (newTerrain == Terrain.RIVER && !canCrossRiver(piece)) {
            return false;
        }

        // Check den access (cannot move into own den)
        if (newTerrain == Terrain.RED_DEN && piece.getOwner() == Player.P1) {
            return false; // P1 cannot enter own RED_DEN
        }
        if (newTerrain == Terrain.BLUE_DEN && piece.getOwner() == Player.P2) {
            return false; // P2 cannot enter own BLUE_DEN
        }

        // Check for piece at new position
        Piece targetPiece = board.getPiecePositions().get(newPos);
        if (targetPiece != null) {
            // Cannot move into own piece
            if (targetPiece.getOwner() == piece.getOwner()) {
                return false;
            }
            // Check capture possibility for opponent's piece
            return canCapture(piece, targetPiece, board);
        }

        // No piece at new position and all checks passed
        return true;
    }
}
