package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Submarine extends Ship {
    private final int LENGTH = 4;
    private final int WIDTH = 2;
    private final String NAME = "SUBMARINE";
    private final int CAPTAIN_INDEX = 3;

    @JsonProperty
    private List<Square> occupiedSquares;
    @JsonProperty
    private int[] health = {1, 1, 1, 2, 1};

    public Submarine() {
    }

    public void initialize(int r, char c, boolean isVert) {
        occupiedSquares = new ArrayList<>();

        // Horizontal template
        occupiedSquares.add(new Square(2, 'A'));
        occupiedSquares.add(new Square(2, 'B'));
        occupiedSquares.add(new Square(2, 'C'));
        occupiedSquares.add(new Square(2, 'D'));
        occupiedSquares.add(new Square(1, 'C'));

        placeNewShipPosition2D(r, c, isVert, LENGTH, occupiedSquares);
    }

    public boolean registerAttack(Square s, Weapon w) {
        return decrementHealth(health, occupiedSquares, s);
    }

    @JsonIgnore
    public String getShipName() {
        return NAME;
    }

    @JsonIgnore
    public int getLength() {
        return LENGTH;
    }

    @JsonIgnore
    public int getWidth() {
        return WIDTH;
    }

    public List<Square> getOccupiedSquares() {
        return occupiedSquares;
    }

    @JsonIgnore
    public boolean isSunk() {
        // Ship is sunk if captain quarter is destroyed
        return health[CAPTAIN_INDEX] == 0;
    }
}
