import model.*;

import java.util.Map;
import java.util.Scanner;

public class Main {
    // Simplified board renderer (shows pieces, terrain, and positions)
    private static void printBoard(Board board) {
        Terrain[][] terrain = board.getTerrain();
        Map<Position, Piece> pieces = board.getPiecePositions();

        // Debug: Verify if pieces are actually in the map (remove after testing)
        System.out.println("Debug: Number of pieces on board = " + pieces.size()); // Should be 16 (8 P1 + 8 P2)

        // Print column numbers (0-6)
        System.out.println("   0   1   2   3   4   5   6");
        System.out.println(" +---+---+---+---+---+---+---+");

        for (int row = 0; row < Board.ROWS; row++) {
            System.out.print(row + "|"); // Print row number

            for (int col = 0; col < Board.COLS; col++) {
                Position pos = new Position(row, col);
                Piece piece = pieces.get(pos);
                Terrain t = terrain[row][col];

                if (piece != null && piece.isAlive()) {
                    // Print piece: P1 = uppercase, P2 = lowercase (e.g., R=Rat-P1, r=Rat-P2)
                    char pieceChar = getPieceChar(piece);
                    System.out.print(" " + pieceChar + " |");
                } else {
                    // Print terrain (simplified symbols)
                    System.out.print(" " + getTerrainChar(t) + " |");
                }
            }

            System.out.println();
            System.out.println(" +---+---+---+---+---+---+---+");
        }
    }

    // Map piece to a single character (for readability)
    private static char getPieceChar(Piece piece) {
        AnimalsWeight weight = piece.getWeight();
        char baseChar = switch (weight) {
            case RAT -> 'R';
            case CAT -> 'C';
            case DOG -> 'D';
            case WOLF -> 'W';
            case LEOPARD -> 'L';
            case TIGER -> 'T';
            case LION -> 'I'; // Use 'I' for Lion (avoids conflict with Leopard 'L')
            case ELEPHANT -> 'E';
        };
        // P1 = uppercase, P2 = lowercase
        return piece.getOwner() == Player.P1 ? baseChar : Character.toLowerCase(baseChar);
    }

    // Map terrain to a single character (for readability)
    private static char getTerrainChar(Terrain terrain) {
        return switch (terrain) {
            case NORMAL -> ' ';    // Empty land
            case RIVER -> '~';   // River
            case RED_TRAP -> '#';// P1's trap (RED_TRAP)
            case BLUE_TRAP -> '#';// P2's trap (BLUE_TRAP)
            case RED_DEN -> '@'; // P1's den (RED_DEN)
            case BLUE_DEN -> '@';// P2's den (BLUE_DEN)
        };
    }

    // Parse user input (e.g., "2,3 3,3" → fromPos=(2,3), toPos=(3,3))
    private static Position[] parseMoveInput(String input) {
        try {
            String[] parts = input.trim().split(" ");
            if (parts.length != 2) {
                return null; // Invalid format (needs 2 positions)
            }

            // Parse "row,col" to Position
            Position from = parsePosition(parts[0]);
            Position to = parsePosition(parts[1]);
            return (from != null && to != null) ? new Position[]{from, to} : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Parse "row,col" to Position (e.g., "2,3" → Position(2,3))
    private static Position parsePosition(String posStr) {
        try {
            String[] coords = posStr.split(",");
            if (coords.length != 2) {
                return null;
            }
            int row = Integer.parseInt(coords[0].trim());
            int col = Integer.parseInt(coords[1].trim());
            return new Position(row, col);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameState gameState = new GameState();
        Board board = gameState.getBoard();

        // Welcome message
        System.out.println("=== Jungle Game Test (Model Package) ===");
        System.out.println("Rules: Move your pieces to capture the opponent's den (@) or eliminate all their pieces.");
        System.out.println("Valid moves: Enter two positions (from to), e.g., '2,3 3,3' (row,col row,col)");
        System.out.println("P1 = Uppercase (R=Rat, C=Cat, ..., E=Elephant)");
        System.out.println("P2 = Lowercase (r=rat, c=cat, ..., e=elephant)");
        System.out.println("Type 'exit' to quit.\n");

        // Game loop
        while (!gameState.isGameOver()) {
            // Print current state
            printBoard(board);
            System.out.println("\nCurrent Player: " + gameState.getCurrentPlayer() + " (P1=Uppercase, P2=Lowercase)");
            System.out.print("Enter your move (e.g., '2,3 3,3'): ");
            String input = scanner.nextLine().trim();

            // Handle exit
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Game exited.");
                scanner.close();
                return;
            }

            // Parse input
            Position[] positions = parseMoveInput(input);
            if (positions == null) {
                System.out.println("❌ Invalid input! Use format: 'row,col row,col' (e.g., '2,3 3,3'). Try again.\n");
                continue;
            }

            Position fromPos = positions[0];
            Position toPos = positions[1];

            // Get the piece to move (check if there's a piece at fromPos)
            Piece pieceToMove = board.getPiecePositions().get(fromPos);
            if (pieceToMove == null) {
                System.out.println("❌ No piece at position " + fromPos + ". Try again.\n");
                continue;
            }

            // Attempt to make the move
            boolean moveSuccess = gameState.makeMove(pieceToMove, toPos);
            if (moveSuccess) {
                System.out.println("✅ Move successful!\n");
            } else {
                System.out.println("❌ Invalid move! (e.g., wrong player, violates rules, out of bounds). Try again.\n");
            }
        }

        // Game over: Print result
        System.out.println("\n=== Game Over ===");
        printBoard(board);
        System.out.println("Winner: " + gameState.getWinner() + "! 🎉");
        scanner.close();
    }
}