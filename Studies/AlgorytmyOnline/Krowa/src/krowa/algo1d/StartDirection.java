package krowa.algo1d;

import java.util.Random;

public enum StartDirection {
	LEFT(1), RIGHT(0), RANDOM(-1);
	
	private final static Random random = new Random();
	
	private final int startDirection;
	
	private StartDirection(int startDirection) {
		this.startDirection = startDirection;
	}
	
	public int getStartDirection() {
		if (startDirection >= 0)
			return startDirection;
		return random.nextBoolean() ? 1 : 0;
	}
}
