package sort.analyse.statistics;

import sort.analyse.listeners.SorterListener;
import sort.analyse.sorters.Sorter;

public class SortStatistics {
	private final Sorter<?> observedSorter; 
	
	private int comparisions = 0;
	private int actions = 0;
	
	public SortStatistics(Sorter<?> sorter) {
		observedSorter = sorter;
		observedSorter.addListener(new SortListener());
	}
	
	public int getComparisions() {
		return comparisions;
	}

	public int getActions() {
		return actions;
	}

	private class SortListener implements SorterListener {
		@Override
		public void sortingStart(Sorter<?> sorter) {
			if (sorter == observedSorter) {
				comparisions = 0;
				actions = 0;
			}
		}

		@Override
		public void sortingFinished(Sorter<?> sorter) {
		}

		@Override
		public void comparisonMade(Sorter<?> sorter) {
			if (sorter == observedSorter)
				comparisions++;
		}

		@Override
		public void actionMade(Sorter<?> sorter) {
			if (sorter == observedSorter)
				actions++;			
		}
	}
}
