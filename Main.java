import controller.GameController;
import model.GameState;
import view.GameView;

public class Main {
    public static void main(String[] args) {
        // Initialize Model
        GameState gameState = new GameState();
        // Initialize View
        GameView gameView = new GameView();
        // Initialize Controller
        GameController controller = new GameController(gameState, gameView);
        // Start the game
        controller.startGame();
    }
}
