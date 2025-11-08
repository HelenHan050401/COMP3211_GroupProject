package controller;

import model.GameState;
import model.Piece;
import model.Position;
import model.Player;
import view.GameView;
import util.GamePersistence;
import util.GameRecorder;
import java.util.Scanner;
import java.util.List;

public class GameController {
    private final GameState gameState;
    private final GameView gameView;
    private final Scanner scanner;

    public GameController(GameState gameState, GameView gameView) {
        this.gameState = gameState;
        this.gameView = gameView;
        this.scanner = new Scanner(System.in);
    }

    // startGame method
    public void startGame() {
        gameView.printMessage("Welcome to Jungle Game!");
        setupPlayerNames();

        showCommands();

        while (!gameState.isGameOver()) {
            gameView.printBoard(gameState);
            gameView.printGameStatus(gameState);
            gameView.printMessage("Enter your command:");

            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                endGame();
                return;
            } else if (input.equalsIgnoreCase("new")) {
                gameState.resetGame();
                gameView.printMessage("New game started!");
                continue;
            } else if (input.startsWith("save ")) {
                String filename = input.substring(5).trim();
                if (!filename.endsWith(".jungle")) {
                    filename += ".jungle";
                }
                if (saveGame(filename)) {
                    gameView.printMessage("Game saved to " + filename);
                } else {
                    gameView.printMessage("Failed to save game");
                }
                continue;
            } else if (input.startsWith("load ")) {
                String filename = input.substring(5).trim();
                if (loadGame(filename)) {
                    gameView.printMessage("Game loaded successfully");
                } else {
                    gameView.printMessage("Failed to load game");
                }
                continue;
            } else if (input.equalsIgnoreCase("undo")) {
                if (gameState.undoMove()) {
                    gameView.printMessage("Move undone");
                } else {
                    gameView.printMessage("Cannot undo - no more moves to undo");
                }
                continue;
            } else if (input.startsWith("record ")) {
                String filename = input.substring(7).trim();
                if (!filename.endsWith(".record")) {
                    filename += ".record";
                }
                if (GameRecorder.saveRecord(gameState, filename)) {
                    gameView.printMessage("Record saved to " + filename);
                } else {
                    gameView.printMessage("Failed to save record");
                }
                continue;
            } else if (input.startsWith("replay ")) {
                String filename = input.substring(7).trim();
                replayRecord(filename);
                continue;
            }

            if (processMove(input)) {
                if (gameState.isGameOver()) {
                    gameView.printBoard(gameState);
                    gameView.printMessage("Game over! Winner: " + gameState.getWinner().getPlayerName());
                }
            } else {
                gameView.printMessage("Invalid command. Try again.");
            }
        }
        scanner.close();
    }

    // method: setup player names
    private void setupPlayerNames() {
        gameView.printMessage("Enter name for Player 1 (leave empty for default):");
        String p1Name = scanner.nextLine().trim();
        if (!p1Name.isEmpty()) {
            Player.P1.setPlayerName(p1Name);
        }

        gameView.printMessage("Enter name for Player 2 (leave empty for default):");
        String p2Name = scanner.nextLine().trim();
        if (!p2Name.isEmpty()) {
            Player.P2.setPlayerName(p2Name);
        }
    }

    // method: show command list
    private void showCommands() {
        gameView.printMessage("\nAvailable commands:");
        gameView.printMessage("- [col row targetCol targetRow] - Make a move");
        gameView.printMessage("- undo - Undo last move (max 3)");
        gameView.printMessage("- new - Start new game");
        gameView.printMessage("- save [filename] - Save game to .jungle file");
        gameView.printMessage("- load [filename] - Load game from .jungle file");
        gameView.printMessage("- record [filename] - Save game record to .record file");
        gameView.printMessage("- replay [filename] - Replay game from .record file");
        gameView.printMessage("- exit - End game\n");
    }

    // method: end game
    public void endGame() {
        gameState.setGameOver(true);
        gameView.printMessage("Game ended by player.");
        scanner.close();
    }

    // method: save game
    public boolean saveGame(String filename) {
        return GamePersistence.saveGame(gameState, filename);
    }

    // method: load game
    public boolean loadGame(String filename) {
        GameState loadedState = GamePersistence.loadGame(filename);
        if (loadedState != null) {
            // Replace current game state
            this.gameState.resetGame();
            // More complex state copying logic may be needed in a real project
            return true;
        }
        return false;
    }

    // method: replay record
    public void replayRecord(String filename) {
        List<String> moves = GameRecorder.loadRecord(filename);
        if (moves.isEmpty()) {
            gameView.printMessage("No moves found in record");
            return;
        }

        GameState replayState = new GameState();
        GameView replayView = new GameView();

        gameView.printMessage("Starting replay...");
        for (String move : moves) {
            replayView.printBoard(replayState);
            replayView.printCurrentPlayer(replayState.getCurrentPlayer());
            replayView.printMessage("Executing: " + move);
            processMove(replayState, move);

            try {
                Thread.sleep(1000); // Pause for 1 second for viewing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        replayView.printBoard(replayState);
        gameView.printMessage("Replay finished");
    }

    // processMove method to support specified game state
    private boolean processMove(GameState state, String input) {
        try {
            String[] parts = input.split(" ");
            if (parts.length != 4) return false;

            int fromRow = Integer.parseInt(parts[0]);
            int fromCol = Integer.parseInt(parts[1]);
            int toRow = Integer.parseInt(parts[2]);
            int toCol = Integer.parseInt(parts[3]);

            Position fromPos = new Position(fromRow, fromCol);
            Position toPos = new Position(toRow, toCol);

            Piece piece = state.getBoard().getPiecePositions().get(fromPos);
            if (piece == null) return false;

            return state.makeMove(piece, toPos);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // processMove method
    private boolean processMove(String input) {
        return processMove(gameState, input);
    }
}