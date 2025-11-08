package view;

import model.Board;
import model.GameState;
import model.Piece;
import model.Position;
import model.Terrain;
import model.Player;
public class GameView {

    // Print board with coordinate and content
    public void printBoard(GameState gameState) {
        Board board = gameState.getBoard();
        Terrain[][] terrain = board.getTerrain();
        var pieces = board.getPiecePositions();

        // Print column labels with space after coordinate
        System.out.print("  "); // Padding for row labels
        for (int col = 0; col < Board.COLS; col++) {
            System.out.print(col + "  "); // Column label + space (e.g., "0  ", "1  ")
        }
        System.out.println();

        // Print each row with row label + space + grid content
        for (int row = 0; row < Board.ROWS; row++) {
            System.out.print(row + " "); // Row label + single space (e.g., "0 ")

            for (int col = 0; col < Board.COLS; col++) {
                Position pos = new Position(row, col);
                if (pieces.containsKey(pos)) {
                    Piece piece = pieces.get(pos);
                    // Piece symbol (2 chars) + space (total 3 chars per cell)
                    System.out.print(getPieceSymbol(piece) + " ");
                } else {
                    // Terrain symbol (1 char) + 2 spaces (total 3 chars per cell)
                    String terrainSymbol = getTerrainSymbol(terrain[row][col]);
                    System.out.print(terrainSymbol + "  ");
                }
            }
            System.out.println();
        }
    }
    // method to display game status
    public void printGameStatus(GameState gameState) {
        printCurrentPlayer(gameState.getCurrentPlayer());
        printRemainingPieces(gameState);
    }

    // method to display remaining pieces
    public void printRemainingPieces(GameState gameState) {
        Board board = gameState.getBoard();

        System.out.println("\nRemaining pieces:");
        System.out.println(Player.P1.getPlayerName() + ":");
        board.getPiecePositions().values().stream()
                .filter(p -> p.getOwner() == Player.P1 && p.isAlive())
                .forEach(p -> System.out.println("- " + p.getWeight().getName() + " at " + p.getPosition()));

        System.out.println(Player.P2.getPlayerName() + ":");
        board.getPiecePositions().values().stream()
                .filter(p -> p.getOwner() == Player.P2 && p.isAlive())
                .forEach(p -> System.out.println("- " + p.getWeight().getName() + " at " + p.getPosition()));
    }

    private String getPieceSymbol(Piece piece) {
        String ownerPrefix = piece.getOwner() == model.Player.P1 ? "R" : "B";
        return ownerPrefix + piece.getWeight().getName().substring(0, 1).toUpperCase();
    }

    private String getTerrainSymbol(Terrain terrain) {
        return switch (terrain) {
            case RIVER -> "~";
            case RED_TRAP, BLUE_TRAP -> "X";
            case RED_DEN, BLUE_DEN -> "D";
            default -> ".";
        };
    }
    // Display game messages
    public void printMessage(String message) {
        System.out.println(message);
    }

    // Show current player's turn
    public void printCurrentPlayer(model.Player player) {
        System.out.println("Current player: " + player.getPlayerName());
    }


}