package krowa.algo2d;

import java.awt.Dimension;
import java.util.Random;

public enum StartDirection {
	TOP{
		@Override
		public Dimension transform(Dimension dimension) {
			return new Dimension(-1 * dimension.width, -1 * dimension.height);
		}
	},
	RIGHT {
		@Override
		public Dimension transform(Dimension dimension) {
			return new Dimension(-1 * dimension.height, dimension.width);
		}
	},
	BOTTOM{
		@Override
		public Dimension transform(Dimension dimension) {
			return new Dimension(dimension.width, dimension.height);
		}
		
	},
	LEFT{
		
		@Override
		public Dimension transform(Dimension dimension) {
			return new Dimension(dimension.height, -1 * dimension.width);
		}
	};
	
	private static final Random RANDOM = new Random(); 

	public abstract Dimension transform(Dimension dimension);
	
	public static StartDirection random() {
		switch (RANDOM.nextInt(4)) {
		case 0:
			return TOP;
		case 1:
			return RIGHT;
		case 2:
			return BOTTOM;
		default:
			return LEFT;
		}
	}
}
