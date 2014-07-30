package balls.and.cups;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

public class BallsAndCupsMinimum extends BallsAndCups {
	private final int minimum;
	
	public BallsAndCupsMinimum(int cups, int minimum) {
		super(cups);
		if (minimum <= 0)
			throw new IllegalArgumentException(minimum + " is not positive number of minimum cups to choose");
		this.minimum = minimum;
	}
	
	@Override
	protected int selectCup() {
		int[] selectedCups = new int[minimum];
		int smallestCup = MAX_VALUE;
		
		for (int i = 0; i < minimum; i++) {
			int cup = random.nextInt(cups);
			selectedCups[i] = cup;
			smallestCup = min(smallestCup, results[cup]);
		}
		
		List<Integer> list = new ArrayList<Integer>();
		for (int selectedCup : selectedCups)
			if (results[selectedCup] == smallestCup)
				list.add(selectedCup);
		
		return list.get(random.nextInt(list.size()));
	}
}
