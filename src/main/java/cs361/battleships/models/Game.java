package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

import static cs361.battleships.models.AtackStatus.INVALID;
import static cs361.battleships.models.AtackStatus.SUNK;

public class Game {

    @JsonProperty private Board playersBoard = new Board();
    @JsonProperty private Board opponentsBoard = new Board();
    @JsonProperty private int playerMoveCount = -2;
    @JsonProperty private int opponentMoveCount = -2;

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean placeShip(Ship ship, int x, char y, boolean isVertical, int depth) {
        boolean successful = playersBoard.placeShip(ship, x, y, isVertical, depth);
        if (!successful)
            return false;

        boolean opponentPlacedSuccessfully;
        do {
            // AI places random ships, so it might try and place overlapping ships
            // let it try until it gets it right
            opponentPlacedSuccessfully = opponentsBoard.placeShip(ShipFactory.createShip(ship.getShipName()), randRow(), randCol(), randVertical(), randDepth());
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
        } else if (playerAttack.getResult() == SUNK) {
            // If two ships are sunk, give movement
            if (playerMoveCount == -2) {
                playerMoveCount = -1;
            } else if (playerMoveCount == -1) {
                playerMoveCount = 2;
            }
        }

        Result opponentAttackResult;

        do {
            // AI does random attacks, so it might attack the same spot twice
            // let it try until it gets it right
            if (randRow() == 10)
                opponentAttackResult = playersBoard.sonar(randRow(), randCol());
            else
                opponentAttackResult = playersBoard.attack(randRow(), randCol());
        } while(opponentAttackResult.getResult() == INVALID);

        return true;
    }
    public boolean sonar(int actionRow, char actionColumn) {
        Result playerSonar = opponentsBoard.sonar(actionRow, actionColumn);
        if (playerSonar.getResult() == INVALID) {
            return false;
        }
        return true;
    }

    public boolean move(Direction dir, String player) {
        Result playerMove;
        if (player.equals("player")) {
            playerMove = playersBoard.move(dir, playerMoveCount);
        } else {
            playerMove = opponentsBoard.move(dir, opponentMoveCount);
        }

        if (playerMove.getResult() == INVALID) {
            return false;
        }

        if (player.equals("player")) {
            playerMoveCount--;
        } else {
            opponentMoveCount--;
        }

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
     * @return int random depth
     */
    private int randDepth() {
        return new Random().nextInt(1);
    }

    /**
     * @return boolean random true and false.
     */
    private boolean randVertical() {
        return new Random().nextInt(2) > 0.5;
    }
}
