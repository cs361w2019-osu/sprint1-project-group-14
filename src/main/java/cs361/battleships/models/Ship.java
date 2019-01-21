package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Ship {

	@JsonProperty private List<Square> occupiedSquares;
	@JsonProperty private int shiplen;
	public Ship() {
		occupiedSquares = new ArrayList<>();
	}
	
	public Ship(String kind) {
		occupiedSquares = new ArrayList<>();
		shiplen=1;
		if(kind.equals("BATTLESHIP")){
			shiplen=4;
		}
		else{
			if(kind.equals("DESTROYER")){
				shiplen=3;
			}
			else{
				shiplen=2;
			}
		}
	}

	public List<Square> getOccupiedSquares() {
		return this.occupiedSquares;
	}

	public int getLength(){
		return this.shiplen;
	}

}
