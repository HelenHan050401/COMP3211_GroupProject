package util;

import model.GameState;
import model.Move;
import model.Position;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameRecorder {
    public static boolean saveRecord(GameState gameState, String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            // Record player names
            out.println("P1:" + model.Player.P1.getPlayerName());
            out.println("P2:" + model.Player.P2.getPlayerName());

            // Record move history
            out.println("MOVES:");
            for (Move move : gameState.getMoveHistory()) {
                Position from = move.getFromPos();
                Position to = move.getToPos();
                out.println(from.getRow() + " " + from.getCol() + " " +
                        to.getRow() + " " + to.getCol());
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> loadRecord(String filename) {
        List<String> moves = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            // Skip player name lines
            while ((line = br.readLine()) != null) {
                if (line.equals("MOVES:")) {
                    break;
                }
            }
            // Read move records
            while ((line = br.readLine()) != null) {
                moves.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moves;
    }
}