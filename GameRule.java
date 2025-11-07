package model;

public class GameRule {
    public static class MoveValidator {
        public static boolean isValidMove(Board board, Position from, Position to) {
            if (board == null || from == null || to == null) return false;
            if (!board.inBounds(from) || !board.inBounds(to)) return false;
            if (from.equals(to)) return false;

            Optional<Piece> opFrom = board.getPiece(from);
            if (opFrom.isEmpty()) return false;
            Piece attacker = opFrom.get();
            boolean red = attacker.isRed();

            // 基础规则
            if (!board.isValidMove(from, to)) return false;

            Optional<Piece> opTo = board.getPiece(to);
            boolean targetRiver = board.isRiver(to);
            boolean targetTrap  = board.isTrap(to, !red);
            boolean targetDen   = board.isDen(to, !red);

            // 老鼠规则
            if (attacker.getWeight() == AnimalsWeight.MOUSE) {
                return mouseRule(board, attacker, from, to, opTo, targetRiver);
            }
            // 狮虎跳河
            if (attacker.getWeight() == AnimalsWeight.LION ||
                attacker.getWeight() == AnimalsWeight.TIGER) {
                Optional<Boolean> jump = jumpRiverRule(board, attacker, from, to);
                if (jump.isPresent()) return jump.get();
            }
            // 普通 1 步
            if (!isAdjacentOneStep(from, to)) return false;
            if (targetRiver) return false; // 非鼠不能入河
            return canCapture(attacker, opTo, targetTrap);
        }

        /* ------------- 子规则 ------------- */
        private static boolean mouseRule(Board board, Piece mouse, Position from, Position to,
                                         Optional<Piece> opTo, boolean targetRiver) {
            boolean fromRiver = board.isRiver(from);
            if (fromRiver) { // 水中
                if (!isAdjacentOneStep(from, to)) return false;
                return opTo.map(pc -> pc.getWeight() == AnimalsWeight.MOUSE).orElse(true);
            }
            // 陆→水 or 陆→陆
            if (targetRiver) return isAdjacentOneStep(from, to);
            return canCapture(mouse, opTo, board.isTrap(to, !mouse.isRed()));
        }

        private static Optional<Boolean> jumpRiverRule(Board board, Piece beast, Position from, Position to) {
            int dr = to.getRow() - from.getRow();
            int dc = to.getCol() - from.getCol();
            if ((dr == 0 && Math.abs(dc) >= 2) || (dc == 0 && Math.abs(dr) >= 2)) {
                int stepR = Integer.signum(dr);
                int stepC = Integer.signum(dc);
                int r = from.getRow() + stepR;
                int c = from.getCol() + stepC;
                while (r != to.getRow() || c != to.getCol()) {
                    Position cur = new Position(r, c);
                    if (!board.isRiver(cur)) return Optional.empty();
                    if (board.getPiece(cur).isPresent()) return Optional.of(false);
                    r += stepR; c += stepC;
                }
                Optional<Piece> opTo = board.getPiece(to);
                boolean targetTrap = board.isTrap(to, !beast.isRed());
                return Optional.of(canCapture(beast, opTo, targetTrap));
            }
            return Optional.empty();
        }

        private static boolean isAdjacentOneStep(Position a, Position b) {
            int dr = Math.abs(a.getRow() - b.getRow());
            int dc = Math.abs(a.getCol() - b.getCol());
            return (dr == 1 && dc == 0) || (dr == 0 && dc == 1);
        }

        private static boolean canCapture(Piece attacker, Optional<Piece> defender, boolean defenderInTrap) {
            if (defender.isEmpty()) return true;
            Piece target = defender.get();
            if (target.isRed() == attacker.isRed()) return false;
            if (defenderInTrap) return true;
            if (attacker.getWeight() == AnimalsWeight.MOUSE &&
                target.getWeight() == AnimalsWeight.ELEPHANT) return true;
            if (attacker.getWeight() == AnimalsWeight.ELEPHANT &&
                target.getWeight() == AnimalsWeight.MOUSE) return false;
            return attacker.getWeight().getValue() >= target.getWeight().getValue();
        }
    }
}

