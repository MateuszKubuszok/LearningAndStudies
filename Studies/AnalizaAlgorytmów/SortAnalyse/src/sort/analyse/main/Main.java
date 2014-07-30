package sort.analyse.main;

import static sort.analyse.sorters.Sorters.MERGE_SORT;
import static sort.analyse.sorters.Sorters.QUICK_SORT;

import java.util.Arrays;
import java.util.List;

import sort.analyse.execution.Execution;
import sort.analyse.execution.OneSorterStatisticsExecution;
import sort.analyse.execution.SorterComparisionsStatisticsExecution;
import sort.analyse.sorters.Sorters;
import sort.analyse.statistics.formatters.PrettyStatisticsFormatter;
import sort.analyse.statistics.formatters.SimpleStatisticsFormatter;

public class Main {
	public static void main(String[] args) {
		parse(args).run();
	}
	
	private static Execution parse(String[] args) {
		List<String> arguments = Arrays.asList(args);
		
		int lowerRangeLimit	= parseInt(10,		"from",		arguments);
		int upperRangeLimit	= parseInt(5000,	"to",		arguments);
		int step 			= parseInt(100,		"step",		arguments);
		int tries 			= parseInt(500,		"tries",	arguments);
		Sorters type		= parseType(arguments);
		
		if (type != null) {
			return new OneSorterStatisticsExecution(type, new SimpleStatisticsFormatter(), lowerRangeLimit, upperRangeLimit, step, tries);
		}
		return new SorterComparisionsStatisticsExecution(new PrettyStatisticsFormatter<Double>(), lowerRangeLimit, upperRangeLimit, step, tries);
	}
	
	private static int parseInt(int defaultValue, String name, List<String> arguments) {
		int position = arguments.lastIndexOf(name);
		if (0 <= position && position+1 < arguments.size()) {
			try {
				return Integer.parseInt(arguments.get(position+1));
			} catch (NumberFormatException e) {}
		}
		return defaultValue;
	}
	
	private static Sorters parseType(List<String> arguments) {
		int position = arguments.lastIndexOf("type");
		if (0 <= position && position+1 < arguments.size()) {			
			if (arguments.get(position+1).equalsIgnoreCase("merge"))
				return MERGE_SORT;
			else if (arguments.get(position+1).equalsIgnoreCase("quick"))
				return QUICK_SORT;
		}
		return null;
	}
}
