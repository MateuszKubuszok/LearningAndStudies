package connect.four.game;

import static connect.four.game.EPlayer.COMPUTER;
import static connect.four.game.EPlayer.NO_ONE;
import static connect.four.game.EPlayer.HUMAN;

import java.awt.Color;

class Dot {
	private EPlayer player;

	public Dot() {
		player = NO_ONE;
	}
	
	public Dot(EPlayer _player) {
		player = _player;
	}
	
	public void clear() {
		player = NO_ONE;
	}
	
	public Color getColor() {
		return player.getColor();
	}
	
	public EPlayer getPlayer() {
		return player;
	}
	
	public void setPlayer(EPlayer player) {
		this.player = player;
	}
	
	public boolean isEmpty() {
		return player.equals(NO_ONE);
	}
	
	public boolean isOccupiedByComputer() {
		return player.equals(COMPUTER);
	}

	public boolean isOccupiedByHuman() {
		return player.equals(HUMAN);
	}
	
	public boolean isOccupiedBy(EPlayer player) {
		return this.player.equals(player);
	}
}