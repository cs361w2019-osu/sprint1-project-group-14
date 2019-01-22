package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

		if (x < 1 || x > 10 || y < 'A' || y > 'J') {
            outcome.setResult(INVALID);
            return outcome;
		}
        for (Ship s : ships) {
			if (s.getOccupiedSquares().contains(outcome.getLocation())) {
				outcome.setShip(s);
				outcome.setResult(HIT);
				break;
			}
		}
        if (outcome.getResult() != HIT) {
			outcome.setResult(MISS);
			attacks.add(outcome);
			return outcome;
		}

		int hitCount = 0;
		for (Result r : attacks) {
			if (r.getLocation().equals(outcome.getLocation())) {
				outcome.setResult(INVALID);
				return outcome;
			} else if (r.getShip().getShipName().equals(outcome.getShip().getShipName())) {
				hitCount++;
			}
			if (hitCount == outcome.getShip().getLength() - 1) {
				outcome.setResult(SUNK);
				sunkShips.add(r.getShip());
			}
		}

		// If all ships were sunk trigger surrender
        if (sunkShips.size() == ships.size()) {
        	outcome.setResult(SURRENDER);
		}

        // Attack was valid, add it to the list
        attacks.add(outcome);

        return outcome;
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
