package com.gameoflife.model;

import static java.lang.System.out;

public class PrintOnChangeAction implements GameChangeListener {
	@Override
	public void gameStarted(Board board) {}
	
	@Override
	public void gameStoped(Board board) {}
	
	@Override
	public void statusChanged(Board board) {
		out.println(board);
	}
	
	@Override
	public void roundChanged(Board board) {}
}
