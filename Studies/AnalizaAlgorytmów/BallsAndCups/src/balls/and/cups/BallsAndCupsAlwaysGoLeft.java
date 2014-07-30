package balls.and.cups;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.min;

public class BallsAndCupsAlwaysGoLeft extends BallsAndCups {
	private final int groups;
	
	public BallsAndCupsAlwaysGoLeft(int cups, int groups) {
		super(cups);
		if (groups > cups)
			throw new IllegalArgumentException("groups number ("+ groups +") cannot be greater that cups");
		if (groups <= 0)
			throw new IllegalArgumentException(groups + " is not positive number of groups");
		this.groups = groups;
	}

	@Override
	protected int selectCup() {
		int[] selectedCups = new int[groups];
		int maxGroupSize = cups/groups;
		int smallestCup = MAX_VALUE;
		
		for (int group = 0; group < groups-1; group++) {
			int cup = group*maxGroupSize + random.nextInt(maxGroupSize);
			selectedCups[group] = cup;
			smallestCup = min(smallestCup, results[cup]);
		}
		int lowerBound = (groups-1)*maxGroupSize;
		selectedCups[groups-1] = lowerBound + random.nextInt(cups - lowerBound);
		smallestCup = min(smallestCup, results[selectedCups[groups-1]]);
		
		for (int selectedCup : selectedCups)
			if (results[selectedCup] == smallestCup)
				return selectedCup;
		
		throw new IllegalStateException("Should never happen");
	}

}
