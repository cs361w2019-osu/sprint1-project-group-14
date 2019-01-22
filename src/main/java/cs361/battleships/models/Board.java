package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;

public class Board {
	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> results;
	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		this.ships = new ArrayList<>();
		this.results =new ArrayList<>();
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		// TODO Implement
		char c_occ='A';
		int c_acs=0;
		int len=ship.getLength();
		if(isVertical == True){
			if((int)y+len<75){
				for(int i=0;i<len;i++){
					c_acs=(int)y+len;
					c_occ=(char)c_acs;
					ship.AddOccupied(x,c_occ);
				}
			}
		}else{
			if(x+len<10){
				for(int i=0;i<len;i++){
					ship.AddOccupied(x+len,y);
				}
			}
		}
		return true;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {

		return null;
	}

	public List<Ship> getShips() {
		return this.ships;
	}

	public void setShips(List<Ship> ships) {
		this.ships=ships;
	}

	public List<Result> getAttacks() {
		return this.results;
	}

	public void setAttacks(List<Result> attacks) {
		this.results=attacks;
	}
}
