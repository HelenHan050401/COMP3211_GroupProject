package model;


public enum Player {
    P1("Player1"), P2("Player2");

    private final String playerName;

    Player(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getOpponent() {
        if (this == P1) return P2;
        else return P1;
    }
}
