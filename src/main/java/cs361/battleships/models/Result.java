package cs361.battleships.models;

public class Result {
	private  AtackStatus Status;
	private Ship ship;
	private Square location;
	public AtackStatus getResult() {
		//TODO implement
		return null;
	}

	public void setResult(AtackStatus result) {
		//TODO implement
	}

	public Ship getShip() {
		return this.ship;
	}

	public void setShip(Ship ship) {
		this.ship=ship;
	}

	public Square getLocation() {
		return this.location;
	}

	public void setLocation(Square square) {
		this.location=square;
	}
}
