package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.EnumMap;


import static cs361.battleships.models.AtackStatus.*;
import static cs361.battleships.models.Direction.*;

public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;
	@JsonProperty private List<Ship> sunkShips;
	@JsonProperty private int sonarCount;
	@JsonProperty private Weapon currentWeapon;

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
		sunkShips = new ArrayList<>();
		sonarCount = -1;
		currentWeapon = Weapon.BOMB;
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
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical, int depth) {
		// If ship doesn't exist, something went wrong with ship creation.
		if (ship == null) {
			return false;
		}

		int shipLength = ship.getLength();
		int shipWidth = ship.getWidth();

		// Length and width are swapped if placed vertically
		if (isVertical) {
			int temp = shipLength;
			shipLength = shipWidth;
			shipWidth = temp;
		}

		// User cannot place outside of grid
		if (x < 1 || x > 10 || y < 'A' || y > 'J') {
			return false;
		}

		// Ship cannot go off grid
		if (x + shipWidth - 1 > 10 || y + shipLength - 1 > 'J') {
			return false;
		}

		// Make sure the ship hasn't already been placed.
		for (Ship s : ships) {
			if (s.getShipName().equals(ship.getShipName())) {
				return false;
			}
		}

		// Make sure the ship doesn't overlap with other ships
		ship.initialize(x, y, isVertical, depth);
		List<Square> thisOccupied = ship.getOccupiedSquares();

		for (Ship s : ships) {
			if (!ship.checkNoCollision(s)) {
				return false;
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
		List<Ship> attackedShips = new ArrayList<>();
		List<Result> outcomes = new ArrayList<>();

		// Check if attack in bounds of Board.
		if (x < 1 || x > 10 || y < 'A' || y > 'J') {
			Result outcome = new Result();
			outcome.setLocation(new Square(x, y));
			outcome.setResult(INVALID);
			return outcome;
		}

		// Check if shot hits a ship that hasn't been sunk.
		for (Ship s : ships) {
			Square sq = new Square(x, y);
			if (s.getOccupiedSquares().contains(sq) && !s.isSunk()) {
				attackedShips.add(s);
			}
		}

		for (Ship s : attackedShips) {
			Result outcome = new Result();
			outcome.setLocation(new Square(x, y));

			outcome.setShip(s);
			// It is a hit when the ship part runs out of HP
			if (s.registerAttack(outcome.getLocation(), currentWeapon)) {
				outcome.setResult(HIT);
			} else {
				outcome.setResult(MISS);
			}

			outcomes.add(outcome);
		}

		for (Result outcome : outcomes) {
			// If the ship is sunk
			if (outcome.getShip().isSunk()) {
				outcome.setResult(SUNK);
				setSunkShipStatus(outcome.getShip());
				sunkShips.add(outcome.getShip());

				// If first ship is sunk, give sonars, upgrade weapons
				if (sonarCount == -1) {
					sonarCount = 2;
					weaponUp();
				}
			}

			// If all ships were sunk trigger surrender.
			if (sunkShips.size() >= ships.size()) {
				outcome.setResult(SURRENDER);
			}

			// Attack was valid; add it to the list.
			attacks.add(outcome);
		}

		// Track highestPriority result, by default a miss
		Result highestResult = null;
		int highestPriority = 0;
		for (Result outcome : outcomes) {
			int currPriority = 0;
			if (outcome.getResult() == SURRENDER) {
				highestResult = outcome;
				break;
			}

			if (outcome.getResult() == SUNK) {
				currPriority = 2;
			} else if (outcome.getResult() == HIT) {
				currPriority = 1;
			}

			if (currPriority > highestPriority) {
				highestPriority = currPriority;
				highestResult = outcome;
			}
		}

		// Attack was a miss
		if (highestResult == null) {
			highestResult = new Result();
			highestResult.setLocation(new Square(x, y));
			highestResult.setResult(MISS);
			attacks.add(highestResult);
		}

		// Return highest priority outcome
        return highestResult;
	}
	/**
	 * Sets the location that the sonar is hit
	 * Stores the action in the history
	 * @param x set X coordinate of the sonar square.
	 * @param y set Y coordinate of the sonar square.
	 * @return Result the outcome of the sonar.
	 */
	public Result sonar(int x, char y) {
		Result outcome = new Result();
		if (sonarCount <= 0) {
			outcome.setResult(INVALID);
			return outcome;
		}
		outcome.setLocation(new Square(x, y));
		outcome.setResult(SONAR);

		sonarCount--;
		attacks.add(outcome);
		return outcome;
	}

	/**
	 * Handle the move fleet feature.
	 * @param dir Direction to move the fleet.
	 * @return Result the outcome of the move.
	 */
	public Result move(Direction dir, int moveCount) {
		Result outcome = new Result();
		if (moveCount <= 0) {
			outcome.setResult(INVALID);
			return outcome;
		}
		outcome.setResult(MOVE);

		moveAll(dir);
		fixCollisions(dir);

		attacks.add(outcome);
		return outcome;
	}

	/**
	 * Move an individual ship in the given direction
	 * Does NOT handle collisions
	 * @param dir direction to move ship
	 * @param i ship to move represented by index
	 */
	private void moveShip(Direction dir, int i) {
		for (int j = 0; j < ships.get(i).getOccupiedSquares().size(); j++) {
			Square tempSquare = ships.get(i).getOccupiedSquares().get(j);
			switch (dir) {
				case EAST:
					ships.get(i).getOccupiedSquares().get(j).setColumn((char)(tempSquare.getColumn() + 1));
					break;
				case WEST:
					ships.get(i).getOccupiedSquares().get(j).setColumn((char)(tempSquare.getColumn() - 1));
					break;
				case NORTH:
					ships.get(i).getOccupiedSquares().get(j).setRow(tempSquare.getRow() - 1);
					break;
				case SOUTH:
					ships.get(i).getOccupiedSquares().get(j).setRow(tempSquare.getRow() + 1);
					break;
			}
		}
	}

	/**
	 * Move the entire fleet in the given direction
	 * Does NOT handle collisions
	 * @param dir direction to move ship
	 */
	private void moveAll(Direction dir) {
		for (int i = 0; i < ships.size(); i++) {
			if (!ships.get(i).isSunk()) {
				moveShip(dir, i);
			}
		}
	}

	/**
	 * Finds and fixes all out of bounds and collisions caused by fleet movement
	 * @param dir direction the ships were originally moved
	 */
	private void fixCollisions(Direction dir) {
		Queue<Integer> OOBShips = new LinkedList<>();
		List<Integer> moved = new ArrayList<>();
		int x;
		char y;
		for (int i = 0; i < ships.size(); i++) {
			for (Square s: ships.get(i).getOccupiedSquares()) {
				x = s.getRow();
				y = s.getColumn();
				if (x < 1 || x > 10 || y < 'A' || y > 'J') {
					OOBShips.add(i);
					moved.add(i);
					break;
				}
			}
		}
		while (!OOBShips.isEmpty()) {
			int index = OOBShips.remove();

			EnumMap<Direction,Direction> rev = new EnumMap<Direction, Direction>(Direction.class);
			rev.put(NORTH, SOUTH);
			rev.put(SOUTH, NORTH);
			rev.put(WEST, EAST);
			rev.put(EAST, WEST);
			if (!ships.get(index).isSunk())
				moveShip(rev.get(dir), index);
				moved.add(index);

			for (int i = 0; i < ships.size(); i++) {
				if (moved.indexOf(i) == -1 && !ships.get(index).checkNoCollision(ships.get(i))) {
					OOBShips.add(i);
				}
			}
		}
	}
	public List<Ship> getShips() {
		return ships;
	}

	private void weaponUp() {
		if (this.currentWeapon != Weapon.LASER)
			currentWeapon = Weapon.LASER;
	}

	private void setSunkShipStatus(Ship s) {
		boolean inAttacked = false;
		for (Square sq : s.getOccupiedSquares()) {
			for (Result r : attacks) {
				if (r.getLocation().equals(sq)) {
					r.setResult(SUNK);
					inAttacked = true;
					break;
				}
			}

			if (!inAttacked) {
				Result r = new Result();
				r.setResult(SUNK);
				r.setShip(s);
				r.setLocation(sq);
				attacks.add(r);
			}

			inAttacked = false;
		}
	}
}
