package controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs361.battleships.models.Direction;
import cs361.battleships.models.Game;

public class MoveGameAction {
	@JsonProperty private Game game;
	@JsonProperty private Direction dir;
	@JsonProperty private String player;

	public Game getGame() {
		return game;
	}
	public Direction getDir() {
		return dir;
	}
	public String getPlayer(){
		return player;
	}
}
