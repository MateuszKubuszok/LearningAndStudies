package sort.analyse.listeners;

import sort.analyse.sorters.Sorter;

public interface SorterListener {
	public void sortingStart(Sorter<?> sorter);
	public void sortingFinished(Sorter<?> sorter);
	public void comparisonMade(Sorter<?> sorter);
	public void actionMade(Sorter<?> sorter);
}
