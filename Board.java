package model;

import java.util.*;

/**
 * 斗兽棋 7×9 棋盘，兼容现有 Position（使用 getRow()/getCol()）。
 */
public class Board {

    public static final int ROWS = 7;
    public static final int COLS = 9;

    public enum Terrain {
        NORMAL, RIVER,
        RED_TRAP, BLUE_TRAP,
        RED_DEN, BLUE_DEN
    }

    private final Piece[][] grid = new Piece[ROWS][COLS];
    private final Terrain[][] terrain = new Terrain[ROWS][COLS];

    public Board() {
        initTerrain();
    }

    /* ---------- 地形初始化 ---------- */
    private void initTerrain() {
        for (Terrain[] row : terrain) Arrays.fill(row, Terrain.NORMAL);

        // 河流
        for (int r = 3; r <= 5; r++) {
            terrain[r][1] = terrain[r][2] = Terrain.RIVER;
            terrain[r][4] = terrain[r][5] = Terrain.RIVER;
            terrain[r][7] = terrain[r][8] = Terrain.RIVER;
        }
        // 陷阱
        terrain[0][2] = terrain[0][4] = terrain[0][6] = Terrain.RED_TRAP;
        terrain[6][2] = terrain[6][4] = terrain[6][6] = Terrain.BLUE_TRAP;
        // 兽穴
        terrain[0][4] = Terrain.RED_DEN;
        terrain[6][4] = Terrain.BLUE_DEN;
    }

    /* ---------- 棋子初始化 ---------- */
    public void initPieces(List<Piece> redPieces, List<Piece> bluePieces) {
        redPieces.forEach(this::placeInitialPiece);
        bluePieces.forEach(this::placeInitialPiece);
    }

    private void placeInitialPiece(Piece p) {
        setPiece(p.getPosition(), p);
    }

    /* ---------- 基本查询 ---------- */
    public Optional<Piece> getPiece(Position p) {
        if (!inBounds(p)) return Optional.empty();
        return Optional.ofNullable(grid[p.getRow()][p.getCol()]);
    }

    public Terrain getTerrain(Position p) {
        if (!inBounds(p)) return null;
        return terrain[p.getRow()][p.getCol()];
    }

    public boolean isRiver(Position p) {
        return getTerrain(p) == Terrain.RIVER;
    }

    public boolean isTrap(Position p, boolean red) {
        return getTerrain(p) == (red ? Terrain.RED_TRAP : Terrain.BLUE_TRAP);
    }

    public boolean isDen(Position p, boolean red) {
        return getTerrain(p) == (red ? Terrain.RED_DEN : Terrain.BLUE_DEN);
    }

    public static boolean inBounds(Position p) {
        int r = p.getRow(), c = p.getCol();
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    /* ---------- 修改 ---------- */
    public void setPiece(Position p, Piece pc) {
        if (!inBounds(p)) return;
        grid[p.getRow()][p.getCol()] = pc;
        if (pc != null) pc.moveTo(p); // 同步 Piece 内部 position
    }

    public Optional<Piece> removePiece(Position p) {
        Optional<Piece> op = getPiece(p);
        op.ifPresent(pc -> pc.moveTo(null));
        grid[p.getRow()][p.getCol()] = null;
        return op;
    }

    /* ---------- 基础合法性（越界、己方兽穴、同色互吃） ---------- */
    public boolean isValidMove(Position from, Position to) {
        if (!inBounds(from) || !inBounds(to) || from.equals(to)) return false;
        Optional<Piece> opFrom = getPiece(from);
        if (opFrom.isEmpty()) return false;
        Piece attacker = opFrom.get();
        boolean red = attacker.isRed();
        if (isDen(to, red)) return false;                 // 不能进己方兽穴
        Optional<Piece> opTo = getPiece(to);
        return opTo.isEmpty() || opTo.get().isRed() != red; // 不能吃同色
    }

    /* ---------- 调试打印 ---------- */
    public void printBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Position p = new Position(r, c);
                Optional<Piece> op = getPiece(p);
                if (op.isPresent()) {
                    Piece pc = op.get();
                    String name = pc.getWeight().getName().substring(0, 1);
                    System.out.print(pc.isRed() ? "[" + name + "]" : "<" + name + ">");
                } else {
                    System.out.print(switch (getTerrain(p)) {
                        case RIVER -> "~~~";
                        case RED_TRAP -> "[阱]";
                        case BLUE_TRAP -> "<阱>";
                        case RED_DEN -> "[穴]";
                        case BLUE_DEN -> "<穴>";
                        default -> "   ";
                    });
                }
            }
            System.out.println();
        }
    }
}
