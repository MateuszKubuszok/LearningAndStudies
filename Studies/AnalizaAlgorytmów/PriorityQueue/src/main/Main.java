package main;

import static java.lang.Math.log10;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.err;
import static main.Operation.DECREASE_KEY;
import static main.Operation.DELETE;
import static main.Operation.EXTRACT_MIN;
import static main.Operation.FIND_MIN;
import static main.Operation.INSERT;
import static main.Operation.UNION;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import array.generators.RandomArrayGenerator;
import binomial.heap.BinomialNode;
import binomial.heap.with.statistics.BinomialHeapWithStatistics;

public class Main {
	public static void main(String[] args) throws IOException {
		long startedAllAt = currentTimeMillis();

		createStatistics();

		long stoppedAllAt = currentTimeMillis();
		err.println("Finished after " + tookSeconds(startedAllAt, stoppedAllAt)
				+ "s");
	}

	// generate statistics

	private static RandomArrayGenerator arrayGenerator = new RandomArrayGenerator();

	private static int start = 10;
	private static int stop = 5000;
	private static int step = 100;

	private static int tries = 500;

	@SuppressWarnings("static-access")
	private static void createStatistics() throws IOException {
		Random random = new Random();

		for (int n = start; n < stop; n += step) {
			Integer[] array = new Integer[n];
			long startedAt = currentTimeMillis();
			for (int i = 0; i < tries; i++) {
				ArrayList<BinomialNode<Integer, Integer>> nodes = new ArrayList<BinomialNode<Integer, Integer>>();
				BinomialHeapWithStatistics<Integer, Integer> bh = new BinomialHeapWithStatistics<Integer, Integer>();
				BinomialHeapWithStatistics<Integer, Integer> bh2 = new BinomialHeapWithStatistics<Integer, Integer>();
				array = arrayGenerator.integerArray(array);

				for (int added : array) {
					nodes.add(bh.insert(added, added));
					bh2.insert(added, added);
				}
				bh.resetComparisons();
				bh2.resetComparisons();

				bh.findMin();
				addComparisonsFor(bh, n, FIND_MIN);

				nodes.remove(bh.extractMin());
				addComparisonsFor(bh, n, EXTRACT_MIN);

				nodes.add(bh.insert(array[0], array[0]));
				addComparisonsFor(bh, n, INSERT);
				nodes.remove(bh.delete(nodes.get(nodes.size() - 1)));
				bh.resetComparisons();

				bh.decreaseKey(nodes.get(random.nextInt(nodes.size())), -1);
				addComparisonsFor(bh, n, DECREASE_KEY);

				nodes.remove(bh.delete(nodes.get(random.nextInt(nodes.size()))));
				addComparisonsFor(bh, n, DELETE);

				bh2.union(bh);
				addComparisonsFor(bh, n, UNION);
			}
			long stoppedAt = currentTimeMillis();
			informAboutCalculationSuccess(n, startedAt, stoppedAt);
		}

		for (Operation operation : Operation.values()) {
			Map<Integer, List<Integer>> operationComparisons = comparisons
					.get(operation);
			FileWriter fw = new FileWriter("queue_"
					+ operation.name().toLowerCase() + ".m");
			for (Integer n : operationComparisons.keySet()) {
				StringBuilder builder = new StringBuilder();
				builder.append(n);
				for (Integer comparison : operationComparisons.get(n))
					builder.append(' ').append(comparison);
				builder.append('\n');
				fw.write(builder.toString());
			}
			fw.close();
			err.println("Generated output for " + operation.name());
		}
	}

	// comparisons handling

	private static Map<Operation, Map<Integer, List<Integer>>> comparisons;
	static {
		comparisons = new TreeMap<Operation, Map<Integer, List<Integer>>>();
		comparisons.put(DECREASE_KEY, new TreeMap<Integer, List<Integer>>());
		comparisons.put(DELETE, new TreeMap<Integer, List<Integer>>());
		comparisons.put(EXTRACT_MIN, new TreeMap<Integer, List<Integer>>());
		comparisons.put(FIND_MIN, new TreeMap<Integer, List<Integer>>());
		comparisons.put(INSERT, new TreeMap<Integer, List<Integer>>());
		comparisons.put(UNION, new TreeMap<Integer, List<Integer>>());
	}

	private static void addComparisonsFor(
			BinomialHeapWithStatistics<Integer, Integer> bh, int n,
			Operation operation) {
		int comparisonsForNForOperation = bh.getComparisons();
		bh.resetComparisons();

		if (!comparisons.get(operation).containsKey(n))
			comparisons.get(operation).put(n, new ArrayList<Integer>());
		comparisons.get(operation).get(n).add(comparisonsForNForOperation);
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
		err.println("Calculated comparisons for n = "
				+ digitPrefix.get(nDigits) + n + ";\t took: " + took + "s");
	}

	private static double tookSeconds(long startedAt, long stoppedAt) {
		return (stoppedAt - startedAt) / 1000.0;
	}
}
