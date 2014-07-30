package sort.analyse.execution;

import static java.lang.System.err;
import static java.lang.System.out;
import static sort.analyse.sorters.Sorters.MERGE_SORT;
import static sort.analyse.sorters.Sorters.QUICK_SORT;

import java.util.Map;
import java.util.TreeMap;

import sort.analyse.array.generators.RandomArrayGenerator;
import sort.analyse.sorters.Sorter;
import sort.analyse.sorters.Sorters;
import sort.analyse.statistics.SortStatistics;
import sort.analyse.statistics.formatters.StatisticsFormatter;

public class SorterComparisionsStatisticsExecution implements Execution {
	private final RandomArrayGenerator rag = new RandomArrayGenerator();
	
	private final StatisticsFormatter<Double> formatter;
	private final int arraySizeLowerLimit;
	private final int arraySizeUpperLimit;
	private final int step;
	private final int randomGenerationsPerSize;
	
	private final Map<Integer,Double> avarageMergeSortComparisonMap;
	private final Map<Integer,Double> avarageQuickSortComparisonMap;
	
	public SorterComparisionsStatisticsExecution(StatisticsFormatter<Double> formatter,
			int arraySizeLowerLimit, int arraySizeUpperLimit,
			int step, int randomGenerationsPerSize) {
		this.formatter = formatter;
		this.arraySizeLowerLimit = arraySizeLowerLimit;
		this.arraySizeUpperLimit = arraySizeUpperLimit;
		this.step = step;
		this.randomGenerationsPerSize = randomGenerationsPerSize;
		
		this.avarageMergeSortComparisonMap = new TreeMap<Integer, Double>();
		this.avarageQuickSortComparisonMap = new TreeMap<Integer, Double>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		err.println("Calculating statistics for Quick-Sort...");
		generateStatisticsForEachSize(MERGE_SORT, avarageMergeSortComparisonMap);
		err.println("Calculating statistics for Merge-Sort...");
		generateStatisticsForEachSize(QUICK_SORT, avarageQuickSortComparisonMap);
		
		err.println("Preparing output...");
		String headers = formatter.formatHeaders("n", MERGE_SORT.toString(), QUICK_SORT.toString());
		String data = formatter.formatData(avarageMergeSortComparisonMap, avarageQuickSortComparisonMap);
		err.println();
		
		out.print(headers);
		out.print(data);
	}
	
	private void generateStatisticsForEachSize(Sorters sorter, Map<Integer,Double> comparisonMap) {
		for (int size = arraySizeLowerLimit; size <= arraySizeUpperLimit; size += step)
			generateStatisticsForSize(sorter, comparisonMap, size);
	}
	
	private void generateStatisticsForSize(Sorters sorter, Map<Integer,Double> comparisonMap, int size) {
		err.println("\t - for size="+size);
		
		long overallComparisons = 0;
		Integer[] array = new Integer[size];
		
		for (int i = 0; i < randomGenerationsPerSize; i++)
			overallComparisons += generateStatistics(sorter, array).getComparisions();
		
		double avarageComparisons = (double)overallComparisons/(double)randomGenerationsPerSize;
		comparisonMap.put(size, avarageComparisons);
	}
	
	private SortStatistics generateStatistics(Sorters sorters, Integer[] array) {
		Sorter<Integer> sorter = sorters.forClass(Integer.class);
		SortStatistics statistics = new SortStatistics(sorter);
		sorter.forArray(rag.array(Integer.class, array)).sort();
		return statistics;
	}
}
