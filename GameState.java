package model;

import java.util.stream.Collectors;

public class GameState {
    private final Board board;
    private Player currentPlayer;
    private boolean isGameOver;
    private Player winner;

    public GameState() {
        this.board = new Board();
        this.currentPlayer = Player.P1; // P1 starts first
        this.isGameOver = false;
        this.winner = null;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public Player getWinner() {
        return winner;
    }

    // Switch to the opponent's turn
    public void switchTurn() {
        currentPlayer = currentPlayer.getOpponent();
    }

    // Check if the game has ended (den captured or all opponent pieces eliminated)
    private void checkGameOver() {
        // Check if opponent's den is occupied
        Position redDenPos = new Position(0, 3); // RED_DEN position
        Position blueDenPos = new Position(8, 3); // BLUE_DEN position

        Piece redDenOccupant = board.getPiecePositions().get(redDenPos);
        if (redDenOccupant != null && redDenOccupant.getOwner() == Player.P2) {
            isGameOver = true;
            winner = Player.P2;
            return;
        }

        Piece blueDenOccupant = board.getPiecePositions().get(blueDenPos);
        if (blueDenOccupant != null && blueDenOccupant.getOwner() == Player.P1) {
            isGameOver = true;
            winner = Player.P1;
            return;
        }

        // Check if all opponent's pieces are eliminated
        Player opponent = currentPlayer.getOpponent();
        long aliveOpponentPieces = board.getPiecePositions().values().stream()
                .filter(piece -> piece.getOwner() == opponent && piece.isAlive())
                .count();

        if (aliveOpponentPieces == 0) {
            isGameOver = true;
            winner = currentPlayer;
        }
    }

    // Attempt to make a move; returns true if successful
    public boolean makeMove(Piece piece, Position newPos) {
        if (isGameOver) {
            return false;
        }

        // Check if the piece belongs to the current player
        if (piece.getOwner() != currentPlayer) {
            return false;
        }

        // Validate move using game rules
        if (!GameRule.isMoveValid(board, piece, newPos)) {
            return false;
        }

        // Execute the move
        Position oldPos = piece.getPosition();
        board.getPiecePositions().remove(oldPos); // Remove from old position

        // Handle capture if new position has an opponent's piece
        Piece capturedPiece = board.getPiecePositions().get(newPos);
        if (capturedPiece != null) {
            capturedPiece.setAlive(false);
            board.getPiecePositions().remove(newPos);
        }

        // Update the moving piece's position
        piece.moveTo(newPos);
        board.getPiecePositions().put(newPos, piece);

        // Check if the game is over after the move
        checkGameOver();

        // Switch turn if game continues
        if (!isGameOver) {
            switchTurn();
        }

        return true;
    }
}
