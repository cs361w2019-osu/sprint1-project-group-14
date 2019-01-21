package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;


public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
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
		if (x < 1 || x > 10 || y < 'A' || y > 'K') {
			return false;
		}

		// Ship cannot go off grid
		if ((isVertical && x + shipLength > 10) ||
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

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		//TODO Implement
		return null;
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


	public List<Result> getAttacks() {
		//TODO implement
		return null;
	}

	public void setAttacks(List<Result> attacks) {
		//TODO implement
	}
}
