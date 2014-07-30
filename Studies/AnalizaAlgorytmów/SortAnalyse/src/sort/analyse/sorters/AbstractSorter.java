package sort.analyse.sorters;

import static java.util.Arrays.copyOf;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import sort.analyse.listeners.SorterListener;

abstract class AbstractSorter<T extends Comparable<T>> implements Sorter<T> {
	private Class<T> dataType;
	private T[] sortedData;
	private Set<SorterListener> listeners;
	
	@SuppressWarnings("unchecked")
	AbstractSorter(Class<T> dataType) {
		this.dataType = dataType;
		this.sortedData = (T[]) Array.newInstance(dataType, 0);
		this.listeners = new HashSet<SorterListener>();
	}
	
	@Override
	public Sorter<T> forArray(T[] array) {
		if (array != null)
			sortedData = copyOf(array, array.length);
		return this;
	}

	@Override
	public Sorter<T> addListener(SorterListener listener) {
		if (listener != null)
			listeners.add(listener);
		return this;
	}
	
	@Override
	public Sorter<T> removeListener(SorterListener listener) {
		if (listener != null)
			listeners.remove(listener);
		return this;
	}
	
	@Override
	public Sorter<T> sort() {
		notifyStart();
		actualSort();
		notifyFinish();
		return this;
	}
	
	@Override
	public T[] getArray() {
		return sortedData;
	}

	@Override
	public Class<T> getSortedType() {
		return dataType;
	}
	
	protected abstract void actualSort(); 
	
	protected int compare(int index1, int index2) {
		notifyComparison();
		return sortedData[index1].compareTo(sortedData[index2]);
	}
	
	protected void swap(int index1, int index2) {
		T tmp = sortedData[index1];
		sortedData[index1] = sortedData[index2];
		sortedData[index2] = tmp;
		notifyAction();
	}
	
	private void notifyStart() {
		for (SorterListener listener : listeners)
			listener.sortingStart(this);
	}
	
	private void notifyFinish() {
		for (SorterListener listener : listeners)
			listener.sortingFinished(this);
	}
	
	private void notifyComparison() {
		for (SorterListener listener : listeners)
			listener.comparisonMade(this);
	}
	
	protected void notifyAction() {
		for (SorterListener listener : listeners)
			listener.actionMade(this);
	}
}
