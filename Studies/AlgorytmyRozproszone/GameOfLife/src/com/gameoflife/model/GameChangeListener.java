package com.gameoflife.model;

public interface GameChangeListener {
	public void gameStarted(Board board);
	public void gameStoped(Board board);
	public void statusChanged(Board board);
	public void roundChanged(Board board);
}
