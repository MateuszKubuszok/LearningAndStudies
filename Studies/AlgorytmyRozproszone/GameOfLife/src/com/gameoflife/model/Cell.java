package com.gameoflife.model;

import static java.lang.Thread.sleep;

final class Cell {
	private final	Board parent;
	final int		x;
	final int		y;
	private long	round;
	private boolean	isAlive;
	private long	aliveSince;
	private int		waits;
	
	Cell(final Board parent, final int x, final int y, final int waits) {
		this(parent, x, y, waits, false);
	}
	
	Cell(final Board parent, final int x, final int y, final int waits, final boolean isAlive) {
		this.parent		= parent;
		this.x 			= x;
		this.y  		= y;
		this.round		= 0L;
		this.isAlive	= isAlive;
		this.aliveSince = 0;
		this.waits		= waits;
	}
	
	boolean isAlive() {
		return isAlive;
	}
	
	void setAlive(final boolean isAlive) {
		boolean wasAlive = this.isAlive;
		
		if (this.isAlive) {
			if (isAlive)
				aliveSince++;
		} else
			this.aliveSince = 0;
		
		this.isAlive = isAlive;
		this.round++;
		
		boolean hasStatusChanged = wasAlive != isAlive;
		if (hasStatusChanged)
			parent.onStatusChange();
		parent.onRoundChange();
	}
	
	long aliveSince() {
		return aliveSince;
	}
	
	long round() {
		return round;
	}
	
	void startGame() {
		new Thread(new CellThread()).start();
	}
	
	private class CellThread implements Runnable {
		@Override
		public void run() {
			while (parent.isGameStarted()) {
				if (parent.isAllowedToProceed(Cell.this))
					setAlive(parent.isAliveInNextRound(Cell.this));
				
				try {
					sleep((long) (waits * Settings.Scaling));
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}
}
