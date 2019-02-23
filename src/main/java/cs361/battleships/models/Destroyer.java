package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("destroyer")
public class Destroyer extends Ship {
    private final int LENGTH = 3;
    private final String NAME = "DESTROYER";
    private final int CAPTAIN_INDEX = 1;

    @JsonProperty
    private List<Square> occupiedSquares;

    @JsonProperty
    private int[] health = {1, 2, 1};

    public Destroyer() {
    }

    public void initialize(int r, char c, boolean isVert) {
        occupiedSquares = getNewShipPosition(r, c, isVert, LENGTH);
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

    public List<Square> getOccupiedSquares() {
        return occupiedSquares;
    }

    @JsonIgnore
    public boolean isSunk() {
        // Ship is sunk if captain quarter is destroyed
        return health[CAPTAIN_INDEX] == 0;
    }
}
