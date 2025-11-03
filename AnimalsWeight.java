package model;

public enum AnimalsWeight {
    RAT(1),
    CAT(2),
    DOG(3),
    WOLF(4),
    LEOPARD(5),
    TIGER(6),
    LION(7),
    ELEPHANT(8);

    private final int weight;

    AnimalsWeight(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
