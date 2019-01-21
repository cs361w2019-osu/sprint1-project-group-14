package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ship {

	@JsonProperty private List<Square> occupiedSquares;
	@JsonProperty private int length;
	@JsonProperty private String shipName;

	// Mapping from SHIP NAME |-> SHIP LENGTH
	// Let null |-> 0 so Jackson doesn't error
	private static final HashMap<String, Integer> SHIP_LENS = new HashMap<>();
	static {
		SHIP_LENS.put("BATTLESHIP", 4);
		SHIP_LENS.put("DESTROYER", 3);
		SHIP_LENS.put("MINESWEEPER", 2);
		SHIP_LENS.put(null, 0);
	}

	public Ship() {
		this(null);
	}

	public Ship(String kind) {
		occupiedSquares = new ArrayList<>();
		shipName = kind;
		// Null check
		Integer len_ = SHIP_LENS.get(kind);
		length = len_ != null ? len_ : 0;
	}

	/**
	 * Updates the occupied squares based on starting square
	 * and whether the ship was placed vertically.
	 *
	 * If the ship was placed horizontally, the left most square is the
	 * start. If the ship was placed vertically, the top most square
	 * is the start.
	 *
	 * Assuming unbounded grid. Check boundries before calling this
	 * method.
	 *
	 * @param r int starting row
	 * @param c char starting column
	 * @param isVert boolean whether the ship is placed vertically
	 */
	public void updateOccupiedSquares(int r, char c, boolean isVert) {
		int len = length;

		for (int i = 0; i < len; i++) {
			if (isVert) {
				occupiedSquares.add(new Square(r + i, c));
			} else {
				occupiedSquares.add(new Square(r, (char) (c + i)));
			}
		}
	}

	/**
	 * @return String ship name.
	 */
	public String getShipName() {
		return shipName;
	}

	/**
	 * @return int ship length.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return List<Square> squares the ship occupies.
	 */
	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}
}