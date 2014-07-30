package main;

import static java.lang.Math.log10;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static main.Problem.BIRTHDAY_PARADOX;
import static main.Problem.COUPON_COLLECTOR;
import static main.Problem.MAX_LOAD;
import static main.Strategy.ALWAYS_GO_LEFT;
import static main.Strategy.MINIMUM;
import static main.Strategy.UNIFORM;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import balls.and.cups.BallsAndCups;
import balls.and.cups.BallsAndCupsAlwaysGoLeft;
import balls.and.cups.BallsAndCupsMinimum;
import balls.and.cups.BallsAndCupsUniform;

public class Main {
	public static void main(String[] args) throws IOException {
		long startedAllAt = currentTimeMillis();

		for (int d = 2; d <= 10; d++) {
			long startedForGroup = 
					currentTimeMillis(); 
			err.println("Generating for d = " + d);
			createStatistics(d);
			long stoppedForGroup = currentTimeMillis();
			err.println("Calculated for d = " + d + "; took: " + tookSeconds(startedForGroup, stoppedForGroup)
					+ "s");
		}

		long stoppedAllAt = currentTimeMillis();
		err.println("Finished after " + tookSeconds(startedAllAt, stoppedAllAt)
				+ "s");
	}
	
	// generate statistics

	private static int start = 10;
	private static int stop = 5000;
	private static int step = 100;

	private static int tries = 500;
	
	private static void createStatistics(int groups) throws IOException {
		for (int n = start; n < stop; n += step) {
			long startedAt = currentTimeMillis();
			BallsAndCups uniform = new BallsAndCupsUniform(n);
			BallsAndCups minimal = new BallsAndCupsMinimum(n, groups);
			BallsAndCups alwaysGoLeft = new BallsAndCupsAlwaysGoLeft(n, groups);
			initiateFor(n);
			for (int i = 0; i < tries; i++) {
				calculateFor(n, uniform, UNIFORM);
				calculateFor(n, minimal, MINIMUM);
				calculateFor(n, alwaysGoLeft, ALWAYS_GO_LEFT);
			}
			long stoppedAt = currentTimeMillis();
			informAboutCalculationSuccess(n, startedAt, stoppedAt);
		}
		
		for (Strategy strategy : Strategy.values())
			for (Problem problem : Problem.values()) {
				Map<Integer, List<Integer>> result = results.get(strategy).get(problem);
				FileWriter fw = new FileWriter("cups_"
						+ strategy.name().toLowerCase() + "_" + problem.name().toLowerCase() + "_" + groups + ".m");
				for (Integer n : result.keySet()) {
					StringBuilder builder = new StringBuilder();
					builder.append(n);
					for (Integer cups : result.get(n))
						builder.append(' ').append(cups);
					builder.append('\n');
					fw.write(builder.toString());
				}
				fw.close();
			}
	}
	
	private static void calculateFor(int n, BallsAndCups bac, Strategy strategy) {
		results.get(strategy).get(COUPON_COLLECTOR).get(n).add(bac.couponCollectorProblem().getBallsThrown());
		results.get(strategy).get(BIRTHDAY_PARADOX).get(n).add(bac.birthDayParadoxProblem().getBallsThrown());
		results.get(strategy).get(MAX_LOAD).get(n).add(bac.maxLoadProblem().getMaxBallInCup());
	}
	
	private static Map<Strategy, Map<Problem, Map<Integer, List<Integer>>>> results;
	static {
		results = new TreeMap<Strategy, Map<Problem, Map<Integer, List<Integer>>>>();
		for (Strategy strategy : Strategy.values()) {
			results.put(strategy, new TreeMap<Problem, Map<Integer, List<Integer>>>());
			for (Problem problem : Problem.values())
				results.get(strategy).put(problem, new TreeMap<Integer, List<Integer>>());
		}
	}
	
	private static void initiateFor(int n) {
		for (Strategy strategy : Strategy.values())
			for (Problem problem : Problem.values())
				results.get(strategy).get(problem).put(n, new ArrayList<Integer>(tries));
	}

	// printing information
	
	private static final int maxDigits = (int) log10(stop);
	private static Map<Integer, String> digitPrefix;
	static {
		digitPrefix = new HashMap<Integer, String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i <= maxDigits; i++) {
			digitPrefix.put(maxDigits - i, builder.toString());
			builder.append(' ');
		}
	}
	
	private static void informAboutCalculationSuccess(int n, long startedAt,
			long stoppedAt) {
		double took = tookSeconds(startedAt, stoppedAt);
		int nDigits = (int) log10(n);
		err.println("Calculated values for n = "
				+ digitPrefix.get(nDigits) + n + ";\t took: " + took + "s");
	}
	
	private static double tookSeconds(long startedAt, long stoppedAt) {
		return (stoppedAt - startedAt) / 1000.0;
	}
}
