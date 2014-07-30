package krowa.algo2d;

import java.awt.Dimension;
import java.util.Random;

public enum RotationDirection {
	CLOCKWISE(-1),
	COUNTER_CLOCKWISE(1);
	
	private static final Random RANDOM = new Random(); 
	
	private final int coefficient;
	
	private RotationDirection(int coefficient) {
		this.coefficient = coefficient;
	}
	
	public Dimension transform(Dimension dimension) {
		return new Dimension(dimension.width * coefficient, dimension.height);
	}
	
	public static RotationDirection random() {
		switch (RANDOM.nextInt(4)) {
		case 0:
			return CLOCKWISE;
		default:
			return COUNTER_CLOCKWISE;
		}
	}
}
