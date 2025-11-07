package model;

import java.util.*;

public class Board {

    public static final int ROWS = 9;
    public static final int COLS = 7;

    public enum Terrain {
        NORMAL, RIVER,
        RED_TRAP, BLUE_TRAP,
        RED_DEN, BLUE_DEN
    }

//    private final Piece[][] grid = new Piece[ROWS][COLS]; // ?


    private final Terrain[][] terrain;
    private final Map<Position, Piece> piecePositions;

    public Board() {
        this.terrain = new Terrain[ROWS][COLS];
        this.piecePositions = new HashMap<>();

        initTerrain();
        initPieces();
    }

    /* ---------- Initialize terrain types ---------- */
    private void initTerrain() {
        // initialize all
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                terrain[row][col] = Terrain.NORMAL;
            }
        }

        // river
        for (int r = 3; r <= 5; r++) {
            terrain[r][1] = terrain[r][2] = Terrain.RIVER;
            terrain[r][4] = terrain[r][5] = Terrain.RIVER;
        }
        // trap
        terrain[0][2] = terrain[0][4] = terrain[1][3] = Terrain.RED_TRAP;
        terrain[8][2] = terrain[8][4] = terrain[7][3] = Terrain.BLUE_TRAP;
        // Den
        terrain[0][3] = Terrain.RED_DEN; // red from smaller side
        terrain[8][3] = Terrain.BLUE_DEN;
    }


    /* ---------- Initialize pieces' position  ---------- */
    public void initPieces() {
        // red pieces (P1): from the smaller row number
        addPiece(AnimalsWeight.ELEPHANT, Player.P1, new Position(2, 6));
        addPiece(AnimalsWeight.LION, Player.P1, new Position(0, 0));
        addPiece(AnimalsWeight.TIGER, Player.P1, new Position(0, 6));
        addPiece(AnimalsWeight.LEOPARD, Player.P1, new Position(2, 2));
        addPiece(AnimalsWeight.WOLF, Player.P1, new Position(2, 4));
        addPiece(AnimalsWeight.DOG, Player.P1, new Position(1, 1));
        addPiece(AnimalsWeight.CAT, Player.P1, new Position(1, 5));
        addPiece(AnimalsWeight.RAT, Player.P1, new Position(2, 0));

        // blue pieces (P2): from the larger row number
        addPiece(AnimalsWeight.ELEPHANT, Player.P2, new Position(6, 0));
        addPiece(AnimalsWeight.LION, Player.P2, new Position(8, 6));
        addPiece(AnimalsWeight.TIGER, Player.P2, new Position(8, 0));
        addPiece(AnimalsWeight.LEOPARD, Player.P2, new Position(6, 4));
        addPiece(AnimalsWeight.WOLF, Player.P2, new Position(6, 2));
        addPiece(AnimalsWeight.DOG, Player.P2, new Position(7, 5));
        addPiece(AnimalsWeight.CAT, Player.P2, new Position(7, 1));
        addPiece(AnimalsWeight.RAT, Player.P2, new Position(6, 6));
    }

    // Add a piece to the board and update the position map.
    private void addPiece(AnimalsWeight weight, Player owner, Position position) {
        Piece piece = new Piece(weight, owner, position, true);
        piecePositions.put(position, piece);
    }





//
//    /* ====== 以下全部改为 static，供 GameRule 静态调用 ====== */
//    public static Terrain getTerrain(Board board, Position p) {
//        if (!inBounds(p)) return null;
//        return board.terrain[p.getRow()][p.getCol()];
//    }
//
//    public static boolean isRiver(Board board, Position p) {
//        return getTerrain(board, p) == Terrain.RIVER;
//    }
//
//    public static boolean isTrap(Board board, Position p, boolean red) {
//        return getTerrain(board, p) == (red ? Terrain.RED_TRAP : Terrain.BLUE_TRAP);
//    }
//
//    public static boolean isDen(Board board, Position p, boolean red) {
//        return getTerrain(board, p) == (red ? Terrain.RED_DEN : Terrain.BLUE_DEN);
//    }
//
//    public static boolean inBounds(Position p) {
//        int r = p.getRow(), c = p.getCol();
//        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
//    }
//
//    /* ---------- 修改 ---------- */
//    public void setPiece(Position p, Piece pc) {
//        if (!inBounds(p)) return;
//        grid[p.getRow()][p.getCol()] = pc;
//        if (pc != null) pc.moveTo(p); // 同步 Piece 内部 position
//    }
//
//    public Optional<Piece> removePiece(Position p) {
//        Optional<Piece> op = getPiece(p);
//        op.ifPresent(pc -> pc.moveTo(null));
//        grid[p.getRow()][p.getCol()] = null;
//        return op;
//    }
//
//    /* ---------- 基础合法性（越界、己方兽穴、同色互吃） ---------- */
//    public boolean isValidMove(Position from, Position to) {
//        if (!inBounds(from) || !inBounds(to) || from.equals(to)) return false;
//        Optional<Piece> opFrom = getPiece(from);
//        if (opFrom.isEmpty()) return false;
//        Piece attacker = opFrom.get();
//        boolean red = attacker.isRed();
//        if (isDen(this, to, red)) return false;                 // 不能进己方兽穴
//        Optional<Piece> opTo = getPiece(to);
//        return opTo.isEmpty() || opTo.get().isRed() != red; // 不能吃同色
//    }
//
//    /* ---------- 调试打印 ---------- */
//    public void printBoard() {
//        for (int r = 0; r < ROWS; r++) {
//            for (int c = 0; c < COLS; c++) {
//                Position p = new Position(r, c);
//                Optional<Piece> op = getPiece(p);
//                if (op.isPresent()) {
//                    Piece pc = op.get();
//                    String name = pc.getWeight().name().substring(0, 1);
//                    System.out.print(pc.isRed() ? "[" + name + "]" : "<" + name + ">");
//                } else {
//                    System.out.print(switch (getTerrain(this, p)) {
//                        case RIVER -> "~~~";
//                        case RED_TRAP -> "[阱]";
//                        case BLUE_TRAP -> "<阱>";
//                        case RED_DEN -> "[穴]";
//                        case BLUE_DEN -> "<穴>";
//                        default -> "   ";
//                    });
//                }
//            }
//            System.out.println();
//        }
//    }
}
