package model;

import java.util.stream.Collectors;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class GameState implements Serializable {
    private final Board board;
    private Player currentPlayer;
    private boolean isGameOver;
    private Player winner;
    private List<Move> moveHistory = new LinkedList<>();
    private static final int MAX_UNDO_STEPS = 3;


    public GameState() {
        this.board = new Board();
        this.currentPlayer = Player.P1; // P1 starts first
        this.isGameOver = false;
        this.winner = null;
    }
    // method to reset the game
    public void resetGame() {
        this.board.initPieces();
        this.currentPlayer = Player.P1;
        this.isGameOver = false;
        this.winner = null;
        this.moveHistory.clear();
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

        // Record state before move
        Position oldPos = piece.getPosition();
        Piece capturedPiece = board.getPiecePositions().get(newPos);

        // Execute the move
        board.getPiecePositions().remove(oldPos); // Remove from old position

        // Handle capture if new position has an opponent's piece
        if (capturedPiece != null) {
            capturedPiece.setAlive(false);
            board.getPiecePositions().remove(newPos);
        }

        // Update the moving piece's position
        piece.moveTo(newPos);
        board.getPiecePositions().put(newPos, piece);

        // Record the move
        moveHistory.add(new Move(piece, oldPos, newPos, capturedPiece));
        if (moveHistory.size() > MAX_UNDO_STEPS) {
            moveHistory.remove(0);
        }

        // Check if the game is over after the move
        checkGameOver();

        // Switch turn if game continues
        if (!isGameOver) {
            switchTurn();
        }

        return true;
    }
    // method to undo a move
    public boolean undoMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }

        Move lastMove = moveHistory.remove(moveHistory.size() - 1);
        Piece piece = lastMove.getPiece();
        Position fromPos = lastMove.getFromPos();
        Position toPos = lastMove.getToPos();
        Piece capturedPiece = lastMove.getCapturedPiece();

        // Move piece back to original position
        board.getPiecePositions().remove(toPos);
        piece.moveTo(fromPos);
        board.getPiecePositions().put(fromPos, piece);

        // Restore captured piece if any
        if (capturedPiece != null) {
            capturedPiece.setAlive(true);
            board.getPiecePositions().put(toPos, capturedPiece);
        }

        // Switch turn back
        switchTurn();

        // Reset game over state
        this.isGameOver = false;
        this.winner = null;

        return true;
    }
    // getter method
    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
