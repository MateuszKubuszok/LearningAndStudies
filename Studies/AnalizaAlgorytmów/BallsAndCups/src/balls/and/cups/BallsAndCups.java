package balls.and.cups;

import static java.lang.Integer.MIN_VALUE;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class BallsAndCups {
	protected static Random random = new Random();
	
	protected final int cups;
	protected int[] results;
	private int ballsThrown;
	
	public BallsAndCups(int cups) {
		if (cups <= 0)
			throw new IllegalArgumentException(cups + " is not positive number of cups");
		
		this.cups = cups;
		this.results = null;
	}
	
	public BallsAndCups couponCollectorProblem() {
		results = new int[cups];
		ballsThrown = 0;
		
		SortedSet<Integer> notEmptyCups = new TreeSet<Integer>();
		for (int i = 0; i < cups; i++)
			notEmptyCups.add(i);
		
		while (!notEmptyCups.isEmpty())
			notEmptyCups.remove(throwBallToCups());
		
		return this;
	}
	
	public BallsAndCups birthDayParadoxProblem() {
		results = new int[cups];
		ballsThrown = 0;
		
		SortedSet<Integer> notEmptyCups = new TreeSet<Integer>();
		
		while (true) {
			int cup = throwBallToCups();
			if (!notEmptyCups.contains(cup))
				notEmptyCups.add(cup);
			else 
				return this;
		}
	}
	
	public BallsAndCups maxLoadProblem() {
		results = new int[cups];
		ballsThrown = 0;
		
		for(int i = 0; i < cups; i++)
			throwBallToCups();
		
		return this;
	}
	
	public int getBallsThrown() {
		return ballsThrown;
	}
	
	public int getMaxBallInCup() {
		int max = MIN_VALUE;
		for(int i = 0; i < cups; i++)
			max = Math.max(max, results[i]);
		return max;
	}
	
	public int[] getResults() {
		return results;
	}
	
	protected abstract int selectCup();
	
	private int throwBallToCups() {
		int cup = selectCup();
		results[cup]++;
		ballsThrown++;
		return cup;
	}
}
