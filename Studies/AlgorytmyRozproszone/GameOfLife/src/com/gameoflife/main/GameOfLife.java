package com.gameoflife.main;

import com.gameoflife.gui.GameOfLifeGUI;
import com.gameoflife.model.Board;

public final class GameOfLife {
	public static void main(String[] args) {
		Board board = new Board(20, 15, 200);
//		board.addListener(new PrintOnChangeAction());
		new GameOfLifeGUI(board);
		board.startGame();
	}
}
