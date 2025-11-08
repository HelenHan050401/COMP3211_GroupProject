package util;

import model.GameState;
import java.io.*;

public class GamePersistence {
    public static boolean saveGame(GameState gameState, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(gameState);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GameState loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}