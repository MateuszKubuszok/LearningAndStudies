package sort.analyse.sorters;

import sort.analyse.listeners.SorterListener;

public interface Sorter<T extends Comparable<T>> {
	public Sorter<T> forArray(T[] array); 
	public Sorter<T> addListener(SorterListener listener);
	public Sorter<T> removeListener(SorterListener listener);
	public Sorter<T> sort();
	public T[] getArray();
	public Class<T> getSortedType();
}
