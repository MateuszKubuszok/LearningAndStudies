package com.gameoflife.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public final class Board {
	private final int width;
	private final int height;
	
	private final Cell[][] cells;
	
	private boolean isGameStarted;
	
	private List<GameChangeListener> listeners;
	
	public Board(final int width, final int height, final int aliveAtStart) {
		this.width	= width;
		this.height	= height;
		
		SecureRandom random = new SecureRandom();
		
		Cell[][] cellsTmp = new Cell[width][];
		for (int x = 0; x < width; x++) {
			cellsTmp[x] = new Cell[height];
			for (int y = 0; y < height; y++) {
				int waits = Settings.MinWaitTime + random.nextInt(Settings.MaxWaitTime - Settings.MinWaitTime);
				cellsTmp[x][y] = new Cell(this, x, y, waits);
			}
		}
		
		int alive = 0;
		while(alive < aliveAtStart) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			
			if (!cellsTmp[x][y].isAlive()) {
				int waits = Settings.MinWaitTime + random.nextInt(Settings.MaxWaitTime - Settings.MinWaitTime);
				cellsTmp[x][y] = new Cell(this, x, y, waits, true);
				alive++;
			}
		}
		this.cells = cellsTmp;
		
		this.isGameStarted = false;
		
		this.listeners = new ArrayList<GameChangeListener>();
	}
	
	public synchronized void startGame() {
		if (!isGameStarted) {
			isGameStarted = true;
			for (Cell[] cols : cells)
				for (Cell cell : cols)
					cell.startGame();
			for (GameChangeListener action : listeners)
				action.gameStarted(this);
		}
	}

	public synchronized void stopGame() {
		if (isGameStarted) {
			isGameStarted = false;
			for (GameChangeListener action : listeners)
				action.gameStoped(this);
		}
	}
	
	public long getAliveSince(int x, int y) {
		if (x >= width || y >= height)
			throw new IllegalArgumentException();
		return cells[x][y].aliveSince();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean isGameStarted() {
		return isGameStarted;
	}
	
	public void addListener(final GameChangeListener listener) {
		listeners.add(listener);
	}
	
	void onStatusChange() {
		for (GameChangeListener action : listeners)
			action.statusChanged(this);
	}
	
	void onRoundChange() {
		for (GameChangeListener action : listeners)
			action.roundChanged(this);
	}
	
	synchronized boolean isAllowedToProceed(final Cell cell) {
		long round = cell.round();
		if (left(cell).round() < round)		return false;
		if (right(cell).round() < round)	return false;
		if (top(cell).round() < round)		return false;
		if (bottom(cell).round() < round)	return false;
		return true;
	}
	
	synchronized boolean isAliveInNextRound(final Cell cell) {
		int aliveNeightbours = aliveNeightbours(cell);
		if (cell.isAlive())
			return aliveNeightbours == 2 || aliveNeightbours == 3;
		else
			return aliveNeightbours == 3;
	}
	
	@Override
	public synchronized String toString() {
		StringBuilder builder = new StringBuilder("Current state:\n");
		for (Cell[] cols : cells) {
			for (Cell cell : cols)
				builder.append('[').append(cell.isAlive() ? 'x' : ' ').append(']');
			builder.append('\n');
		}
		return builder.toString();
	}
	
	private int aliveNeightbours(final Cell cell) {
		int alive = 0;
		if (left(cell).isAlive())	alive++;
		if (right(cell).isAlive())	alive++;
		if (top(cell).isAlive())	alive++;
		if (bottom(cell).isAlive())	alive++;
		return alive;
	} 
	
	private Cell left(final Cell cell) {
		int x = (width  + cell.x - 1) % width;
		int y = (height + cell.y + 0) % height;
		return cells[x][y];
	}
	
	private Cell right(final Cell cell) {
		int x = (width  + cell.x + 1) % width;
		int y = (height + cell.y + 0) % height;
		return cells[x][y];
	}
	
	private Cell top(final Cell cell) {
		int x = (width  + cell.x + 0) % width;
		int y = (height + cell.y + 1) % height;
		return cells[x][y];
	}
	
	private Cell bottom(final Cell cell) {
		int x = (width  + cell.x + 0) % width;
		int y = (height + cell.y - 1) % height;
		return cells[x][y];
	}
}
