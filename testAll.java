import controller.GameController;
import main.Main;
import model.*;
import util.GamePersistence;
import util.GameRecorder;
import view.GameView;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class testAll {

    @Test
    public void testAnimalsWeightValues() {
        assertEquals(2, AnimalsWeight.CAT.getWeight());
        assertEquals("cat", AnimalsWeight.CAT.getName());
        assertEquals(3, AnimalsWeight.DOG.getWeight());
        assertEquals(4, AnimalsWeight.WOLF.getWeight());
        assertEquals(5, AnimalsWeight.LEOPARD.getWeight());
        assertEquals(6, AnimalsWeight.TIGER.getWeight());
        assertEquals(7, AnimalsWeight.LION.getWeight());
    }

    @Test
    public void testPositionHashCode() {
        Position p1 = new Position(1, 2);
        Position p2 = new Position(1, 2);
        Position p3 = new Position(2, 2);

        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    public void testPositionNotEquals() {
        Position p1 = new Position(1, 2);
        assertNotEquals(p1, null);
        assertNotEquals(p1, "not a position");
    }

    @Test
    public void testPlayerDefaultNames() {
        assertEquals("Player1", Player.P1.getPlayerName());
        assertEquals("Player2", Player.P2.getPlayerName());
    }

    @Test
    public void testPlayerOpponent() {
        assertEquals(Player.P1, Player.P2.getOpponent());
        assertEquals(Player.P2, Player.P1.getOpponent());
    }

    @Test
    public void testBoardConstants() {
        assertEquals(9, Board.ROWS);
        assertEquals(7, Board.COLS);
    }

    @Test
    public void testBoardTerrainInitialization() {
        Board board = new Board();
        Terrain[][] terrain = board.getTerrain();

        assertEquals(Terrain.RIVER, terrain[3][1]);
        assertEquals(Terrain.RIVER, terrain[3][2]);
        assertEquals(Terrain.RIVER, terrain[4][1]);
        assertEquals(Terrain.RIVER, terrain[4][2]);
        assertEquals(Terrain.RIVER, terrain[5][1]);
        assertEquals(Terrain.RIVER, terrain[5][2]);

        assertEquals(Terrain.RED_TRAP, terrain[0][2]);
        assertEquals(Terrain.RED_TRAP, terrain[0][4]);
        assertEquals(Terrain.RED_TRAP, terrain[1][3]);
        assertEquals(Terrain.BLUE_TRAP, terrain[8][2]);
        assertEquals(Terrain.BLUE_TRAP, terrain[8][4]);
        assertEquals(Terrain.BLUE_TRAP, terrain[7][3]);

        assertEquals(Terrain.RED_DEN, terrain[0][3]);
        assertEquals(Terrain.BLUE_DEN, terrain[8][3]);
    }

    @Test
    public void testPieceToString() {
        Piece piece = new Piece(AnimalsWeight.LION, Player.P1, new Position(0, 0));
        Player.P1.setPlayerName("TestPlayer");
        String result = piece.toString();
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("lion"));
        assertTrue(result.contains("(0, 0)"));
    }

    @Test
    public void testMoveGetters() {
        Piece rat = new Piece(AnimalsWeight.RAT, Player.P1, new Position(0, 0));
        Piece cat = new Piece(AnimalsWeight.CAT, Player.P2, new Position(0, 1));
        Move move = new Move(rat, new Position(0, 0), new Position(0, 1), cat);

        assertEquals(new Position(0, 0), move.getFromPos());
        assertEquals(new Position(0, 1), move.getToPos());
        assertEquals(cat, move.getCapturedPiece());
    }

    @Test
    public void testMoveWithoutCapture() {
        Piece rat = new Piece(AnimalsWeight.RAT, Player.P1, new Position(0, 0));
        Move move = new Move(rat, new Position(0, 0), new Position(0, 1), null);

        assertNull(move.getCapturedPiece());
    }

    @Test
    public void testCanEnterRiver() {
        Piece rat = new Piece(AnimalsWeight.RAT, Player.P1, new Position(0, 0));
        Piece lion = new Piece(AnimalsWeight.LION, Player.P1, new Position(0, 0));
        Piece tiger = new Piece(AnimalsWeight.TIGER, Player.P1, new Position(0, 0));
        Piece elephant = new Piece(AnimalsWeight.ELEPHANT, Player.P1, new Position(0, 0));

        assertTrue(GameRule.canEnterRiver(rat));
        assertTrue(GameRule.canEnterRiver(lion));
        assertTrue(GameRule.canEnterRiver(tiger));
        assertFalse(GameRule.canEnterRiver(elephant));
    }

    @Test
    public void testIsPositionValid() {
        assertTrue(GameRule.isPositionValid(new Position(0, 0)));
        assertTrue(GameRule.isPositionValid(new Position(4, 3)));
        assertTrue(GameRule.isPositionValid(new Position(8, 6)));
        assertFalse(GameRule.isPositionValid(new Position(-1, 0)));
        assertFalse(GameRule.isPositionValid(new Position(9, 0)));
        assertFalse(GameRule.isPositionValid(new Position(0, 7)));
        assertFalse(GameRule.isPositionValid(new Position(0, -1)));
    }

    @Test
    public void testIsAdjacent() {
        Position center = new Position(3, 3);

        assertTrue(GameRule.isAdjacent(center, new Position(3, 2)));
        assertTrue(GameRule.isAdjacent(center, new Position(3, 4)));
        assertTrue(GameRule.isAdjacent(center, new Position(2, 3)));
        assertTrue(GameRule.isAdjacent(center, new Position(4, 3)));

        assertFalse(GameRule.isAdjacent(center, new Position(3, 3)));
        assertFalse(GameRule.isAdjacent(center, new Position(2, 2)));
        assertFalse(GameRule.isAdjacent(center, new Position(3, 5)));
    }

    @Test
    public void testIsTrapForPlayer() {
        Board board = new Board();

        assertTrue(GameRule.isTrapForPlayer(new Position(0, 2), Player.P2, board));
        assertFalse(GameRule.isTrapForPlayer(new Position(0, 2), Player.P1, board));

        assertTrue(GameRule.isTrapForPlayer(new Position(8, 2), Player.P1, board));
        assertFalse(GameRule.isTrapForPlayer(new Position(8, 2), Player.P2, board));
    }

    @Test
    public void testRatCaptureSameTerrain() {
        Board board = new Board();

        Piece rat1 = new Piece(AnimalsWeight.RAT, Player.P1, new Position(3, 1));
        Piece rat2 = new Piece(AnimalsWeight.RAT, Player.P2, new Position(3, 2));
        board.getPiecePositions().put(rat1.getPosition(), rat1);
        board.getPiecePositions().put(rat2.getPosition(), rat2);

        assertTrue(GameRule.canCapture(rat1, rat2, board));
    }

    @Test
    public void testTrapCapture() {
        Board board = new Board();

        Piece weakPiece = new Piece(AnimalsWeight.RAT, Player.P2, new Position(8, 2));
        Piece strongPiece = new Piece(AnimalsWeight.ELEPHANT, Player.P1, new Position(7, 2));
        board.getPiecePositions().put(weakPiece.getPosition(), weakPiece);
        board.getPiecePositions().put(strongPiece.getPosition(), strongPiece);

        assertTrue(GameRule.canCapture(strongPiece, weakPiece, board));
    }

    @Test
    public void testRatInWaterCannotCaptureElephant() {
        Board board = new Board();

        board.getPiecePositions().clear();

        Piece rat = new Piece(AnimalsWeight.RAT, Player.P1, new Position(3, 1));
        Piece elephant = new Piece(AnimalsWeight.ELEPHANT, Player.P2, new Position(3, 2));

        board.getPiecePositions().put(rat.getPosition(), rat);
        board.getPiecePositions().put(elephant.getPosition(), elephant);

        assertFalse(GameRule.canCapture(rat, elephant, board));
    }

    @Test
    public void testGameStateInitialization() {
        GameState state = new GameState();

        assertNotNull(state.getBoard());
        assertEquals(Player.P1, state.getCurrentPlayer());
        assertFalse(state.isGameOver());
        assertNull(state.getWinner());
        assertTrue(state.getMoveHistory().isEmpty());
    }

    @Test
    public void testSwitchTurn() {
        GameState state = new GameState();

        assertEquals(Player.P1, state.getCurrentPlayer());
        state.switchTurn();
        assertEquals(Player.P2, state.getCurrentPlayer());
        state.switchTurn();
        assertEquals(Player.P1, state.getCurrentPlayer());
    }

    @Test
    public void testWinByCapturingDen() {
        GameState state = new GameState();

        Piece attacker = new Piece(AnimalsWeight.LION, Player.P2, new Position(1, 3));
        state.getBoard().getPiecePositions().put(new Position(0, 3), attacker);

        state.checkGameOver();
        assertTrue(state.isGameOver());
        assertEquals(Player.P2, state.getWinner());
    }

    @Test
    public void testMoveToOwnPiece() {
        GameState state = new GameState();

        Piece mover = state.getBoard().getPiecePositions().get(new Position(2, 0));
        Piece target = state.getBoard().getPiecePositions().get(new Position(2, 2));

        assertFalse(state.makeMove(mover, target.getPosition()));
    }

    @Test
    public void testMoveDeadPiece() {
        GameState state = new GameState();

        Piece piece = state.getBoard().getPiecePositions().get(new Position(2, 0));
        piece.setAlive(false);

        assertFalse(state.makeMove(piece, new Position(3, 0)));
    }

    @Test
    public void testMoveWrongPlayerPiece() {
        GameState state = new GameState();

        Piece p2Piece = state.getBoard().getPiecePositions().get(new Position(6, 6));

        assertFalse(state.makeMove(p2Piece, new Position(5, 6)));
    }

    @Test
    public void testUndoEmptyHistory() {
        GameState state = new GameState();

        assertFalse(state.undoMove());
    }

    @Test
    public void testMultipleUndo() {
        GameState state = new GameState();

        Piece rat1 = state.getBoard().getPiecePositions().get(new Position(2, 0));
        state.makeMove(rat1, new Position(3, 0));

        Piece rat2 = state.getBoard().getPiecePositions().get(new Position(6, 6));
        state.makeMove(rat2, new Position(5, 6));

        assertTrue(state.undoMove());
        assertTrue(state.undoMove());
        assertEquals(Player.P1, state.getCurrentPlayer());
    }

    @Test
    public void testResetGame() {
        GameState state = new GameState();

        Piece rat = state.getBoard().getPiecePositions().get(new Position(2, 0));
        state.makeMove(rat, new Position(3, 0));
        state.switchTurn();

        state.resetGame();

        assertEquals(Player.P1, state.getCurrentPlayer());
        assertFalse(state.isGameOver());
        assertNull(state.getWinner());
        assertTrue(state.getMoveHistory().isEmpty());

        assertNotNull(state.getBoard().getPiecePositions().get(new Position(2, 0)));
    }

    @Test
    public void testSaveAndLoadWithTempDir(@TempDir Path tempDir) {
        GameState state = new GameState();
        String filename = tempDir.resolve("test.jungle").toString();

        assertTrue(GamePersistence.saveGame(state, filename));

        GameState loaded = GamePersistence.loadGame(filename);
        assertNotNull(loaded);
        assertEquals(state.getCurrentPlayer(), loaded.getCurrentPlayer());
    }

    @Test
    public void testSaveAndLoadNonExistentFile() {
        GameState loaded = GamePersistence.loadGame("nonexistent.jungle");
        assertNull(loaded);
    }

    @Test
    public void testRecordAndLoadWithTempDir(@TempDir Path tempDir) {
        GameState state = new GameState();
        String filename = tempDir.resolve("test.record").toString();

        Piece rat = state.getBoard().getPiecePositions().get(new Position(2, 0));
        state.makeMove(rat, new Position(3, 0));

        assertTrue(GameRecorder.saveRecord(state, filename));

        List<String> moves = GameRecorder.loadRecord(filename);
        assertEquals(1, moves.size());
        assertEquals("2 0 3 0", moves.get(0));
    }

    @Test
    public void testLoadNonExistentRecord() {
        List<String> moves = GameRecorder.loadRecord("nonexistent.record");
        assertTrue(moves.isEmpty());
    }

    @Test
    public void testControllerInitialization() {
        GameState state = new GameState();
        GameView view = new GameView();
        GameController controller = new GameController(state, view);

        assertNotNull(controller);
    }

    @Test
    public void testProcessMoveInvalidNumberFormat() {
        GameState state = new GameState();
        GameView view = new GameView();
        GameController controller = new GameController(state, view);

        assertFalse(controller.processMove(state, "a b c d"));
        assertFalse(controller.processMove(state, "2 0 3"));
        assertFalse(controller.processMove(state, ""));
    }

    @Test
    public void testProcessMoveNoPieceAtPosition() {
        GameState state = new GameState();
        GameView view = new GameView();
        GameController controller = new GameController(state, view);

        assertFalse(controller.processMove(state, "4 4 4 5"));
    }

    @Test
    public void testTerrainValues() {
        Terrain[] terrains = Terrain.values();
        assertEquals(6, terrains.length);
        assertArrayEquals(new Terrain[]{
                Terrain.NORMAL, Terrain.RIVER,
                Terrain.RED_TRAP, Terrain.BLUE_TRAP,
                Terrain.RED_DEN, Terrain.BLUE_DEN
        }, terrains);
    }

    @Test
    public void testTerrainValueOf() {
        assertEquals(Terrain.NORMAL, Terrain.valueOf("NORMAL"));
        assertEquals(Terrain.RIVER, Terrain.valueOf("RIVER"));
        assertEquals(Terrain.RED_TRAP, Terrain.valueOf("RED_TRAP"));
        assertEquals(Terrain.BLUE_TRAP, Terrain.valueOf("BLUE_TRAP"));
        assertEquals(Terrain.RED_DEN, Terrain.valueOf("RED_DEN"));
        assertEquals(Terrain.BLUE_DEN, Terrain.valueOf("BLUE_DEN"));
    }

    @Test
    public void testPieceMovement() {
        Piece piece = new Piece(AnimalsWeight.RAT, Player.P1, new Position(0, 0));
        Position newPos = new Position(1, 1);

        piece.moveTo(newPos);
        assertEquals(newPos, piece.getPosition());
    }

    @Test
    public void testBoardPieceCount() {
        Board board = new Board();
        Map<Position, Piece> pieces = board.getPiecePositions();

        assertEquals(16, pieces.size());

        long p1Count = pieces.values().stream()
                .filter(p -> p.getOwner() == Player.P1)
                .count();
        long p2Count = pieces.values().stream()
                .filter(p -> p.getOwner() == Player.P2)
                .count();

        assertEquals(8, p1Count);
        assertEquals(8, p2Count);
    }

    @Test
    public void testMoveToSamePosition() {
        Board board = new Board();
        Piece rat = board.getPiecePositions().get(new Position(2, 0));

        assertFalse(GameRule.isMoveValid(board, rat, new Position(2, 0)));
    }

    @Test
    public void testCannotEnterOpponentDen() {
        Board board = new Board();

        Piece p1Piece = board.getPiecePositions().get(new Position(2, 0));
        assertFalse(GameRule.isMoveValid(board, p1Piece, new Position(8, 3)));

        Piece p2Piece = board.getPiecePositions().get(new Position(6, 6));
        assertFalse(GameRule.isMoveValid(board, p2Piece, new Position(0, 3)));
    }

    @Test
    public void testGameViewPieceSymbols() {
        GameView view = new GameView();

        Piece p1Rat = new Piece(AnimalsWeight.RAT, Player.P1, new Position(0, 0));
        Piece p2Rat = new Piece(AnimalsWeight.RAT, Player.P2, new Position(0, 0));
        Piece p1Lion = new Piece(AnimalsWeight.LION, Player.P1, new Position(0, 0));
        Piece p2Elephant = new Piece(AnimalsWeight.ELEPHANT, Player.P2, new Position(0, 0));

        GameState state = new GameState();
        view.printBoard(state);
    }

    @Test
    public void testGameViewTerrainSymbols() {
        GameView view = new GameView();
        GameState state = new GameState();

        view.printBoard(state);
    }

    @Test
    public void testMainWithEmptyNames() {
        ByteArrayInputStream in = new ByteArrayInputStream("\n\n2 0 3 0\nexit\n".getBytes());
        System.setIn(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Main.main(new String[]{});
        String output = out.toString();
        assertTrue(output.contains("Welcome to Jungle Game!"));
    }

    @Test
    public void testValidCapture() {
        GameState state = new GameState();

        state.getBoard().getPiecePositions().clear();

        Piece lion = new Piece(AnimalsWeight.LION, Player.P1, new Position(0, 0));
        Piece cat = new Piece(AnimalsWeight.CAT, Player.P2, new Position(0, 1));

        state.getBoard().getPiecePositions().put(lion.getPosition(), lion);
        state.getBoard().getPiecePositions().put(cat.getPosition(), cat);

        assertTrue(lion.isAlive());
        assertTrue(cat.isAlive());

        assertTrue(state.makeMove(lion, new Position(0, 1)));

        assertFalse(cat.isAlive());
        assertEquals(new Position(0, 1), lion.getPosition());
        assertEquals(lion, state.getBoard().getPiecePositions().get(new Position(0, 1)));
    }

    @Test
    public void testCaptureInTrap() {
        GameState state = new GameState();

        state.getBoard().getPiecePositions().clear();

        Piece rat = new Piece(AnimalsWeight.RAT, Player.P2, new Position(0, 2));
        Piece cat = new Piece(AnimalsWeight.CAT, Player.P1, new Position(0, 1));

        state.getBoard().getPiecePositions().put(rat.getPosition(), rat);
        state.getBoard().getPiecePositions().put(cat.getPosition(), cat);

        assertTrue(state.makeMove(cat, new Position(0, 2)));
        assertFalse(rat.isAlive());
    }

    @Test
    public void testLionRiverCrossing() {
        Board board = new Board();

        board.getPiecePositions().clear();

        Piece lion = new Piece(AnimalsWeight.LION, Player.P1, new Position(2, 1));
        board.getPiecePositions().put(lion.getPosition(), lion);

        assertTrue(GameRule.isMoveValid(board, lion, new Position(6, 1)));
    }

    @Test
    public void testTigerRiverCrossing() {
        Board board = new Board();

        board.getPiecePositions().clear();

        Piece tiger = new Piece(AnimalsWeight.TIGER, Player.P1, new Position(2, 1));
        board.getPiecePositions().put(tiger.getPosition(), tiger);

        assertTrue(GameRule.isMoveValid(board, tiger, new Position(6, 1)));
    }
}