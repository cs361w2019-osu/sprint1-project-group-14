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
		@JsonSubTypes.Type(value=Submarine.class, name="SUBMARINE"),
})
public abstract class Ship {
	/**
	 * Creates the occupied squares based on starting square
	 * and whether the ship was placed vertically.
	 *
	 * Ship needs to have width of 1.
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
	protected List<Square> getNewShipPosition1D(int r, char c, boolean isVert, int len) {
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

    /**
     * Updates the occupied squares based on starting square
     * and whether the ship was placed vertically for 2D ships based
     * on horizontal template.
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
	protected void placeNewShipPosition2D(int r, char c, boolean isVert, int length, List<Square> occupied) {
        // Rotate template
        if (isVert) {
            for (Square sq : occupied) {
            	int row = sq.getRow();
            	sq.setRow(sq.getColumn()+1-'A');
            	sq.setColumn((char) (length-row-2+'A'));
            }
        }

        // Move ship
        for (Square sq : occupied) {
            sq.setRow(sq.getRow()+r-1);
            sq.setColumn((char) (sq.getColumn()+c-'A'));
        }
    }

	/**
	 * Check to see if this ship is not colliding with other ship.
	 * @param other ship to compare against
	 * @return boolean if collision did not occurred.
	 */
	protected boolean checkNoCollision(Ship other) {
		List<Square> thisOccupied = this.getOccupiedSquares();
		List<Square> otherOccupied = other.getOccupiedSquares();
		for (Square sq : otherOccupied) {
			if (thisOccupied.contains(sq)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Decrements the health of a ship part given a health array.
	 *
	 * @param health int[] array of ship part health
	 * @param occupiedSquares List<Square> squares the ship occupies
	 * @param s Square ship part health to decrement
	 * @return
	 */
	protected boolean decrementHealth(int[] health, List<Square> occupiedSquares, Square s) {
		int square = occupiedSquares.indexOf(s);
		if (square < 0) return false;

		if (health[square] > 0) {
			health[square]--;
		}
		return health[square] <= 0;
	}

	/**
	 * Perform initialization code (e.g. ship placement).
	 * @param r
	 * @param c
	 * @param isVert
	 */
	public abstract void initialize(int r, char c, boolean isVert);

	/**
	 * Register an attack to the ship.
	 * @param s Square to damage.
	 * @return boolean whether the attack killed the body part
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
	 * @return int ship width.
	 */
	public abstract int getWidth();

	/**
	 * @return List<Square> squares the ship occupies.
	 */
	public abstract List<Square> getOccupiedSquares();

	/**
	 * @return bool whether the ship is sunk.
	 */
	public abstract boolean isSunk();
}