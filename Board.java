package model;

import java.util.*;


public class Board {

    public static final int ROWS = 9;
    public static final int COLS = 7;
    

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
        Piece piece = new Piece(weight, owner, position);
        piecePositions.put(position, piece);
    }


    public Terrain[][] getTerrain() {
        return terrain;
    }

    public Map<Position, Piece> getPiecePositions() {
        return piecePositions;
    }
}
