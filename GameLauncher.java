package model;

import java.util.Scanner;
import java.util.Map;

public class GameLauncher {
    public static void main(String[] args) {
        // 初始化游戏状态
        GameState gameState = new GameState();
        Board board = gameState.getBoard();
        Scanner scanner = new Scanner(System.in);

        System.out.println("斗兽棋游戏开始！");
        System.out.println("玩家1（P1）先行，输入格式：当前位置行 列 目标位置行 列（例如：0 0 1 0）");

        while (!gameState.isGameOver()) {
            Player currentPlayer = gameState.getCurrentPlayer();
            System.out.println("\n" + currentPlayer.getPlayerName() + "的回合");

            // 打印当前棋盘（简化版）
            printBoard(board);

            // 读取用户输入
            System.out.print("请输入移动指令（行 列 目标行 目标列）：");
            int fromRow = scanner.nextInt();
            int fromCol = scanner.nextInt();
            int toRow = scanner.nextInt();
            int toCol = scanner.nextInt();

            // 解析位置和棋子
            Position fromPos = new Position(fromRow, fromCol);
            Position toPos = new Position(toRow, toCol);
            Piece piece = board.getPiecePositions().get(fromPos);

            // 执行移动
            if (piece == null) {
                System.out.println("该位置没有棋子，请重新输入！");
                continue;
            }

            boolean moveSuccess = gameState.makeMove(piece, toPos);
            if (!moveSuccess) {
                System.out.println("移动无效，请重新输入！");
            }
        }

        // 游戏结束，输出结果
        System.out.println("\n游戏结束！胜利者是：" + gameState.getWinner().getPlayerName());
        scanner.close();
    }

    // 简化的棋盘打印方法
    private static void printBoard(Board board) {
        Terrain[][] terrain = board.getTerrain();
        Map<Position, Piece> pieces = board.getPiecePositions();

        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                Position pos = new Position(row, col);
                Piece piece = pieces.get(pos);
                if (piece != null) {
                    // 打印棋子（P1用R开头，P2用B开头，后面跟动物首字母）
                    String owner = piece.getOwner() == Player.P1 ? "R" : "B";
                    String animal = piece.getWeight().getName().substring(0, 1).toUpperCase();
                    System.out.print(owner + animal + " ");
                } else {
                    // 打印地形（简化符号）
                    switch (terrain[row][col]) {
                        case RIVER:
                            System.out.print("~~ ");
                            break;
                        case RED_TRAP:
                        case BLUE_TRAP:
                            System.out.print("XX ");
                            break;
                        case RED_DEN:
                        case BLUE_DEN:
                            System.out.print("DD ");
                            break;
                        default:
                            System.out.print("-- ");
                    }
                }
            }
            System.out.println(); // 换行
        }
    }
}