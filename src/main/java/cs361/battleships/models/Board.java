package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

import static cs361.battleships.models.AtackStatus.*;


public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;
	@JsonProperty private List<Ship> sunkShips;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
		sunkShips = new ArrayList<>();
	}

	/**
	 * Places a ship on a 10x10 grid where the ships cannot
	 * overlap. Allows for one of each type of ship.
	 *
	 * DO NOT change the signature of this method. It is used by the grading scripts.
	 * @param ship user ship to be placed
	 * @param x starting row coords for ship
	 * @param y starting col coords for ship
	 * @param isVertical whether the ship will be placed vertically
	 * @return boolean success of ship placement
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		int shipLength = ship.getLength();

		// If ship len is 0, something went wrong with ship creation.
		if (shipLength == 0) {
			return false;
		}

		// User cannot place outside of grid
		if (x < 1 || x > 10 || y < 'A' || y > 'J') {
			return false;
		}

		// Ship cannot go off grid
		if ((isVertical && x + shipLength > 11) ||
				(!isVertical && (char) y + shipLength > 'K')) {
			return false;
		}

		// Make sure the ship hasn't already been placed.
		for (Ship s : ships) {
			if (s.getShipName().equals(ship.getShipName())) {
				return false;
			}
		}

		// Make sure the ship doesn't overlap with other ships
		ship.updateOccupiedSquares(x, y, isVertical);
		List<Square> thisOccupied = ship.getOccupiedSquares();

		for (Ship s : ships) {
			List<Square> otherOccupied = s.getOccupiedSquares();
			for (Square sq : otherOccupied) {
				if (thisOccupied.contains(sq)) {
					return false;
				}
			}
		}

		// Ship placement successful. Add to list.
		ships.add(ship);

		return true;
	}

	/**
	 * Computes the result of an attack on a specific square.
	 * Verified validity of attack and adds it to list of attacks.
	 *
	 * @param x set X coordinate of the attack square.
	 * @param y set Y coordinate of the attack square.
	 * @return Result the outcome of the attack.
	 */
	public Result attack(int x, char y) {
	    Result outcome = new Result();
	    outcome.setLocation(new Square(x, y));

	    // Check if attack in bounds of Board.
        if (x < 1 || x > 10 || y < 'A' || y > 'J') {
            outcome.setResult(INVALID);
            return outcome;
        }
        // Assume miss.
        outcome.setResult(MISS);

        // Check if shot hits a ship.
        for (Ship s : ships) {
            if (s.getOccupiedSquares().contains(outcome.getLocation())) {
                outcome.setShip(s);
                outcome.setResult(HIT);
                break;
            }
        }

        // hitCount represents the number of hits the given ship has already suffered.
        int hitCount = 0;
        for (Result r : attacks) {
            // Check if location was previously attacked.
            if (r.getLocation().equals(outcome.getLocation())) {
                outcome.setResult(INVALID);
                return outcome;
            }
            // Make sure we are not dealing with misses.
            if (r.getResult() == HIT && outcome.getResult() == HIT) {
                // Increment ship hit if the same ship was hit.
                if (r.getShip().getShipName().equals(outcome.getShip().getShipName())) {
                    hitCount++;
                }
            }
        }

        // If all squares of ship hit it is considered sunk.
        if (outcome.getResult() == HIT && hitCount == outcome.getShip().getLength() - 1) {
            outcome.setResult(SUNK);
			setSunkShipStatus(outcome.getShip());
            sunkShips.add(outcome.getShip());
        }

        // If all ships were sunk trigger surrender.
        if (sunkShips.size() >= ships.size()) {
            outcome.setResult(SURRENDER);
        }

        // Attack was valid; add it to the list.
        attacks.add(outcome);

        return outcome;
	}

	private void setSunkShipStatus(Ship s) {
		List<Square> occupied = s.getOccupiedSquares();

		for (Result r : attacks) {
			if (occupied.contains(r.getLocation())) {
				r.setResult(SUNK);
			}
		}
	}

	/**
	 * @return List<Ship> get array of ships placed.
	 */
	public List<Ship> getShips() {
		return ships;
	}

	/**
	 * @param ships set the ships placed list.
	 */
	public void setShips(List<Ship> ships) {
		this.ships = ships;
	}


	/**
	 * @return List<Result> get array of attacks made.
	 */
	public List<Result> getAttacks() {
		return attacks;
	}

	/**
	 * @param attacks set the attacks made list.
	 */
	public void setAttacks(List<Result> attacks) {
        this.attacks = attacks;
	}
}
