package cs361.battleships.models;

public class Square {

	private int row;
	private char column;

	public Square() {
	}

	public Square(int row, char column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * Override equals to allow easy comparison of coordinates
	 * @param other other object
	 * @return boolean whether the squares are at the same location
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof Square)) {
			return false;
		}

		Square o = (Square) other;

		return (o.row == this.row) &&
				(o.column == this.column);
	}

	/**
	 * Override hashCode to allow use of comparison in collection
	 * @return int unique hash
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = 31 * hash + row;
		hash = 31 * hash + column;
		return hash;
	}

	public char getColumn() {
		return column;
	}

	public void setColumn(char column) {
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
}
