package main;

import static java.lang.Math.log10;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static main.Problem.FLIPS;
import static main.Problem.MAX_LENGTH;
import static main.Problem.MIN_LENGTH;
import static main.Problem.ROUNDS;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import trie.Trie;
import trie.TrieBuilder;

public class Main {
	public static void main(String[] args) throws IOException {
		long startedAllAt = currentTimeMillis();

		for (int d : Arrays.asList(1, 4)) {
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
	
	private static void createStatistics(int leaders) throws IOException {
		for (int n = start; n < stop; n += step) {
			long startedAt = currentTimeMillis();
			initiateFor(n);
			for (int i = 0; i < tries; i++) {
				Trie trie = TrieBuilder.generate(n, leaders);
				calculateFor(n, trie);
			}
			long stoppedAt = currentTimeMillis();
			informAboutCalculationSuccess(n, startedAt, stoppedAt);
		}
		
		for (Problem problem : Problem.values()) {
			Map<Integer, List<Integer>> result = results.get(problem);
			FileWriter fw = new FileWriter("trie_"
					 + problem.name().toLowerCase() + "_" + leaders + ".m");
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
	
	private static void calculateFor(int n, Trie trie) {
		results.get(FLIPS).get(n).add(trie.getFlips());
		results.get(ROUNDS).get(n).add(trie.getRounds());
		results.get(MIN_LENGTH).get(n).add(trie.getMinLength());
		results.get(MAX_LENGTH).get(n).add(trie.getMaxLength());
	}
	
	private static Map<Problem, Map<Integer, List<Integer>>> results = new TreeMap<Problem, Map<Integer, List<Integer>>>();
	static {
		for (Problem problem : Problem.values())
			results.put(problem, new TreeMap<Integer, List<Integer>>());
	}
	
	private static void initiateFor(int n) {
		for (Problem problem : Problem.values())
			results.get(problem).put(n, new ArrayList<Integer>(tries));
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
