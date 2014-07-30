package connect.four.game;

import static java.awt.Color.BLACK;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;

import java.awt.Color;

public enum EPlayer {
	NO_ONE("No one", LIGHT_GRAY),
	HUMAN("Human player", WHITE),
	COMPUTER("Computer", BLACK);
	
	private final Color color; 
	private final String description;
	
	private EPlayer(String value, Color color) {
		this.color = color;
		this.description = value;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getDescription() {
		return description;
	}
}