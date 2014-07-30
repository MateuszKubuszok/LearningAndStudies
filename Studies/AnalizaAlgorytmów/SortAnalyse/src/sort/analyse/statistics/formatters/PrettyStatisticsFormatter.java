package sort.analyse.statistics.formatters;

import java.util.Map;
import java.util.Set;

public class PrettyStatisticsFormatter<Value extends Number> implements StatisticsFormatter<Value> {
	@Override
	public String formatHeaders(String... columnNames) {
		StringBuilder builder = new StringBuilder();
		
		for (String column : columnNames)
			builder.append(column).append(":\t\t");
		
		return builder.append('\n')
				.append("--------------------------------------------------")
				.append('\n')
				.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String formatData(Map<Integer, Value>... statistics) {
		StringBuilder builder = new StringBuilder();
		
		if (statistics.length > 0) {
			Set<Integer> keys = statistics[0].keySet();
			
			for (Integer key : keys)
				builder.append(valueForAllStatistics(key, statistics)).append('\n');
		}
		
		return builder.toString();
	}
	
	private String valueForAllStatistics(Integer key, Map<Integer, Value>[] statistics) {
		StringBuilder builder = new StringBuilder().append(key);
		
		for (Map<Integer, Value> testedStatistics : statistics)
			if (testedStatistics.containsKey(key)) {
				builder.append("\t\t").append(testedStatistics.get(key));
			} else {
				return "";
			}
		
		return builder.toString();
	}
}
