package connect.four.main;

import connect.four.game.Game;
import connect.four.gui.GameWindow;

public class Main {
	public static void main(String[] args) {
		Game game = new Game();
		new GameWindow(game);
	}
}