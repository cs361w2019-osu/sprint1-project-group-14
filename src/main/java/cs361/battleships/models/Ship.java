package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
		      include=JsonTypeInfo.As.PROPERTY,
              property="shipName")
@JsonSubTypes({
		@JsonSubTypes.Type(value=Battleship.class, name="BATTLESHIP"),
		@JsonSubTypes.Type(value=Minesweeper.class, name="MINESWEEPER"),
		@JsonSubTypes.Type(value=Destroyer.class, name="DESTROYER"),
})
public abstract class Ship {
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
	protected List<Square> getNewShipPosition(int r, char c, boolean isVert, int len) {
		List<Square> occupiedSquares = new ArrayList<>();
		for (int i = 0; i < len; i++) {
			if (isVert) {
				occupiedSquares.add(new Square(r + i, c));
			} else {
				occupiedSquares.add(new Square(r, (char) (c + i)));
			}
		}
		return occupiedSquares;
	}

	protected boolean decrementHealth(int[] health, List<Square> occupiedSquares, Square s) {
		int square = occupiedSquares.indexOf(s);
		if (square < 0) return false;

		if (health[square] > 0) {
			health[square]--;
		}
		return health[square] <= 0;
	}

	/**
	 * Perform initialization code
	 * @param r
	 * @param c
	 * @param isVert
	 */
	public abstract void initialize(int r, char c, boolean isVert);

	/**
	 * Register an attack to the ship.
	 * @param s Square to damage.
	 * @return boolean whether the attack hit
	 */
	public abstract boolean registerAttack(Square s, Weapon w);

	/**
	 * @return String ship name.
	 */
	public abstract String getShipName();

	/**
	 * @return int ship length.
	 */
	public abstract int getLength();

	/**
	 * @return List<Square> squares the ship occupies.
	 */
	public abstract List<Square> getOccupiedSquares();

	/**
	 * @return bool whether the ship is sunk.
	 */
	public abstract boolean isSunk();
}