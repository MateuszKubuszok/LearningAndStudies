package sort.analyse.statistics.formatters;

import java.util.Map;

@SuppressWarnings("unchecked")
public interface StatisticsFormatter<Value> {
	public String formatHeaders(String... columnNames);
	public String formatData(Map<Integer, Value>... statistics);
}
