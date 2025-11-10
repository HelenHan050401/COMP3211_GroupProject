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

    // Check if a piece can enter river terrain
    public static boolean canEnterRiver(Piece piece) {
        AnimalsWeight weight = piece.getWeight();
        return weight == AnimalsWeight.RAT ||  // Rats can enter rivers
                weight == AnimalsWeight.LION ||  // Lions can cross rivers
                weight == AnimalsWeight.TIGER;   // Tigers can cross rivers
    }

    // Check if the position is a trap for the given player
    public static boolean isTrapForPlayer(Position pos, Player player, Board board) {
        Terrain terrain = board.getTerrain()[pos.getRow()][pos.getCol()];
        if (player == Player.P1) {
            return terrain == Terrain.BLUE_TRAP;
        } else {
            return terrain == Terrain.RED_TRAP;
        }
    }

    // Check if the attacker can capture the defender
    public static boolean canCapture(Piece attacker, Piece defender, Board board) {
        // Handle special capture rules for rats
        if (attacker.getWeight() == AnimalsWeight.RAT || defender.getWeight() == AnimalsWeight.RAT) {
            return canRatCapture(attacker, defender, board);
        }

        // Defenders in their own traps can be captured by any attacker
        if (isTrapForPlayer(defender.getPosition(), defender.getOwner(), board)) {
            return true;
        }

        AnimalsWeight aWeight = attacker.getWeight();
        AnimalsWeight dWeight = defender.getWeight();

        // Elephants cannot capture rats
        if (aWeight == AnimalsWeight.ELEPHANT && dWeight == AnimalsWeight.RAT) {
            return false;
        }

        // Normal case: Higher weight captures lower weight
        return aWeight.getWeight() > dWeight.getWeight();
    }

    // Special capture rules for rats
    private static boolean canRatCapture(Piece attacker, Piece defender, Board board) {
        // Check if both attacker and defender are in water or both on land
        boolean attackerInWater = isInWater(attacker.getPosition(), board);
        boolean defenderInWater = isInWater(defender.getPosition(), board);

        // Must be in the same terrain type (both water or both land)
        if (attackerInWater != defenderInWater) {
            return false;
        }

        // Rats cannot capture elephants
        if (defender.getWeight() == AnimalsWeight.ELEPHANT) {
            return false;
        }

        // Rats can capture other rats
        if (attacker.getWeight() == AnimalsWeight.RAT && defender.getWeight() == AnimalsWeight.RAT) {
            return true;
        }

        // Other cases: Rat can be captured by heavier non-elephant pieces
        return attacker.getWeight().getWeight() > defender.getWeight().getWeight();
    }

    // Check if a position is in water
    private static boolean isInWater(Position pos, Board board) {
        return board.getTerrain()[pos.getRow()][pos.getCol()] == Terrain.RIVER;
    }

    // Method to check for rats in the entire river crossing path (both horizontal and vertical)
    private static boolean hasRatInRiverPath(Position from, Position to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Check horizontal river crossing (same row)
        if (rowDiff == 0 && colDiff > 1) {
            int row = from.getRow();
            int startCol = Math.min(from.getCol(), to.getCol());
            int endCol = Math.max(from.getCol(), to.getCol());

            // Inspect all positions between start and end columns
            for (int col = startCol + 1; col < endCol; col++) {
                Position pos = new Position(row, col);
                // Block crossing if any rat exists in the path
                Piece piece = board.getPiecePositions().get(pos);
                if (piece != null && piece.getWeight() == AnimalsWeight.RAT) {
                    return true;
                }
            }
        }
        // Check vertical river crossing (same column)
        else if (colDiff == 0 && rowDiff > 1) {
            int col = from.getCol();
            int startRow = Math.min(from.getRow(), to.getRow());
            int endRow = Math.max(from.getRow(), to.getRow());

            // Inspect all positions between start and end rows
            for (int row = startRow + 1; row < endRow; row++) {
                Position pos = new Position(row, col);
                // Block crossing if any rat exists in the path
                Piece piece = board.getPiecePositions().get(pos);
                if (piece != null && piece.getWeight() == AnimalsWeight.RAT) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if lion/tiger can cross the river
    private static boolean isTigerLionCrossRiver(Piece piece, Position from, Position to, Board board) {
        AnimalsWeight weight = piece.getWeight();
        if (weight != AnimalsWeight.TIGER && weight != AnimalsWeight.LION) {
            return false;
        }

        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Must be straight horizontal or vertical crossing
        if (!((rowDiff == 0 && colDiff > 1) || (colDiff == 0 && rowDiff > 1))) {
            return false;
        }

        // Check for rats in the river path
        if (hasRatInRiverPath(from, to, board)) {
            return false;
        }

        // Check if start and end positions are valid river banks
        if (rowDiff == 0) { // Horizontal crossing
            int row = from.getRow();
            if (row < 3 || row > 5) {
                return false;
            }
            return (isValidRiverBank(from, board) && isValidRiverBank(to, board));
        } else { // Vertical crossing
            int col = from.getCol();
            if (col < 1 || col > 5) {
                return false;
            }
            return (isValidRiverBank(from, board) && isValidRiverBank(to, board));
        }
    }

    // Check if position is a valid river bank (non-river terrain)
    private static boolean isValidRiverBank(Position pos, Board board) {
        return board.getTerrain()[pos.getRow()][pos.getCol()] != Terrain.RIVER;
    }

    // Main method to validate a move
    public static boolean isMoveValid(Board board, Piece piece, Position newPos) {
        // Check if piece is alive
        if (!piece.isAlive()) {
            return false;
        }

        // Check if new position is within bounds
        if (!isPositionValid(newPos)) {
            return false;
        }

        Position currentPos = piece.getPosition();

        // Check if moving to the same position
        if (currentPos.equals(newPos)) {
            return false;
        }

        // Check if movement is adjacent or lion/tiger river crossing
        boolean isTigerLionCross = isTigerLionCrossRiver(piece, currentPos, newPos, board);
        if (!isTigerLionCross && !isAdjacent(currentPos, newPos)) {
            return false;
        }

        // Check new position terrain
        Terrain newTerrain = board.getTerrain()[newPos.getRow()][newPos.getCol()];

        // Check river entry permission
        if (newTerrain == Terrain.RIVER && !canEnterRiver(piece)) {
            return false;
        }

        // Check den access (cannot enter own den)
        if (newTerrain == Terrain.RED_DEN && piece.getOwner() == Player.P1) {
            return false;
        }
        if (newTerrain == Terrain.BLUE_DEN && piece.getOwner() == Player.P2) {
            return false;
        }

        // Check for piece at new position
        Piece targetPiece = board.getPiecePositions().get(newPos);
        if (targetPiece != null) {
            // Cannot move to own piece
            if (targetPiece.getOwner() == piece.getOwner()) {
                return false;
            }
            // Check capture possibility
            return canCapture(piece, targetPiece, board);
        }

        // Valid move to empty position
        return true;
    }
}
