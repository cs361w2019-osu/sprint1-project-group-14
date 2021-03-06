package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("battleship")
public class Battleship extends Ship {
    private final int LENGTH = 4;
    private final int WIDTH = 1;
    private final int DEPTH = 1;
    private final String NAME = "BATTLESHIP";
    private final int CAPTAIN_INDEX = 2;

    @JsonProperty
    private List<Square> occupiedSquares;
    @JsonProperty
    private int[] health = {1, 1, 2, 1};

    public Battleship() {
    }

    public void initialize(int r, char c, boolean isVert, int depth) {
        occupiedSquares = getNewShipPosition1D(r, c, isVert, LENGTH);
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

    @JsonIgnore
    public int getDepth() {
        return DEPTH;
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