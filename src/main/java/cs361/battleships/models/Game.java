package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

import static cs361.battleships.models.AtackStatus.INVALID;

public class Game {

    @JsonProperty private Board playersBoard = new Board();
    @JsonProperty private Board opponentsBoard = new Board();

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
        boolean successful = playersBoard.placeShip(ship, x, y, isVertical);
        if (!successful)
            return false;

        boolean opponentPlacedSuccessfully;
        do {
            // AI places random ships, so it might try and place overlapping ships
            // let it try until it gets it right
            opponentPlacedSuccessfully = opponentsBoard.placeShip(new Ship(ship.getShipName()), randRow(), randCol(), randVertical());
        } while (!opponentPlacedSuccessfully);

        return true;
    }

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean attack(int x, char  y) {
        Result playerAttack = opponentsBoard.attack(x, y);
        if (playerAttack.getResult() == INVALID) {
            return false;
        }

        Result opponentAttackResult;
        do {
            // AI does random attacks, so it might attack the same spot twice
            // let it try until it gets it right
            opponentAttackResult = playersBoard.attack(randRow(), randCol());
        } while(opponentAttackResult.getResult() == INVALID);

        return true;
    }

    /**
     * @return char random Column from 'A' to 'J'.
     */
    char randCol() {
        return (char)(new Random().nextInt(10) + 65);
    }

    /**
     * @return int random row from 1 to 10.
     */
    int randRow() {
        return new Random().nextInt(10) + 1;
    }

    /**
     * @return boolean random true and false.
     */
    private boolean randVertical() {
        return new Random().nextInt(2) > 0.5;
    }
}
