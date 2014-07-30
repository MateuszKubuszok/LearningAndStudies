package sort.analyse.statistics.formatters;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleStatisticsFormatter implements StatisticsFormatter<List<Integer>> {
	@Override
	public String formatHeaders(String... columnNames) {
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public String formatData(Map<Integer, List<Integer>>... statistics) {
		StringBuilder builder = new StringBuilder();
		
		if (statistics.length > 0) {
			Set<Integer> keys = statistics[0].keySet();
			
			for (Integer key : keys)
				builder.append(valueForAllStatistics(key, statistics)).append('\n');
		}
		
		return builder.toString();
	}
	
	private String valueForAllStatistics(Integer key, Map<Integer, List<Integer>>[] statistics) {
		StringBuilder builder = new StringBuilder().append(key);
		
		for (Map<Integer, List<Integer>> testedStatistics : statistics)
			if (testedStatistics.containsKey(key)) {
				for (Integer integer : testedStatistics.get(key))
					builder.append(" ").append(integer);
			} else {
				return "";
			}
		
		return builder.toString();
	}
}
