package model;

public enum AnimalsWeight {
    RAT(1, "rat"),
    CAT(2, "cat"),
    DOG(3, "dog"),
    WOLF(4, "wolf"),
    LEOPARD(5, "leopard"),
    TIGER(6, "tiger"),
    LION(7, "lion"),
    ELEPHANT(8, "elephant");

    private final int weight;
    private final String name;

    AnimalsWeight(int weight, String name) {
        this.weight = weight;
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }
}
