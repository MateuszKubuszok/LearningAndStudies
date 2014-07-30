package sort.analyse.execution;

import static java.lang.System.err;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import sort.analyse.array.generators.RandomArrayGenerator;
import sort.analyse.sorters.Sorter;
import sort.analyse.sorters.Sorters;
import sort.analyse.statistics.SortStatistics;
import sort.analyse.statistics.formatters.StatisticsFormatter;

public class OneSorterStatisticsExecution implements Execution {
	private final RandomArrayGenerator rag = new RandomArrayGenerator();
	
	private final Sorters sorters;
	private final StatisticsFormatter<List<Integer>> formatter;
	private final int arraySizeLowerLimit;
	private final int arraySizeUpperLimit;
	private final int step;
	private final int randomGenerationsPerSize;
	
	private final Map<Integer,List<Integer>> avarageComparisionsMap;
	
	public OneSorterStatisticsExecution(Sorters sorters, StatisticsFormatter<List<Integer>> formatter,
			int arraySizeLowerLimit, int arraySizeUpperLimit,
			int step, int randomGenerationsPerSize) {
		this.sorters = sorters;
		this.formatter = formatter;
		this.arraySizeLowerLimit = arraySizeLowerLimit;
		this.arraySizeUpperLimit = arraySizeUpperLimit;
		this.step = step;
		this.randomGenerationsPerSize = randomGenerationsPerSize;
		
		this.avarageComparisionsMap = new TreeMap<Integer, List<Integer>>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		err.println("\rCalculating statistics for " + sorters +  "...");
		generateStatisticsForEachSize();
		
		err.println();
		String data = formatter.formatData(avarageComparisionsMap);
		err.println();
		
		out.print(data);
	}
	
	private void generateStatisticsForEachSize() {
		for (int size = arraySizeLowerLimit; size <= arraySizeUpperLimit; size += step)
			generateStatisticsForSize(size);
	}
	
	private void generateStatisticsForSize(int size) {
		err.println("\t - for size="+size);
		
		List<Integer> comparisons = new ArrayList<Integer>();
		Integer[] array = new Integer[size];
		
		for (int i = 0; i < randomGenerationsPerSize; i++)
			comparisons.add(generateStatistics(array).getComparisions());
		
		avarageComparisionsMap.put(size, comparisons);
	}
	
	private SortStatistics generateStatistics(Integer[] array) {
		Sorter<Integer> sorter = sorters.forClass(Integer.class);
		SortStatistics statistics = new SortStatistics(sorter);
		sorter.forArray(rag.array(Integer.class, array)).sort();
		return statistics;
	}
}
