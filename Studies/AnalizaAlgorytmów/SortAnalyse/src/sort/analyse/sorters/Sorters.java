package sort.analyse.sorters;

import java.lang.reflect.InvocationTargetException;

public enum Sorters {
	QUICK_SORT("Quick-Sort", QuickSorter.class),
	MERGE_SORT("Merge-Sort", MergeSorter.class);
	
	private final String sorterName;
	
	@SuppressWarnings("rawtypes")
	private final Class<? extends Sorter> sorterClass;
	
	@SuppressWarnings("rawtypes")
	private Sorters(String sorterName, Class<? extends Sorter> sorterClass) {
		this.sorterName = sorterName;
		this.sorterClass = sorterClass;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Comparable<T>> Sorter<T> forClass(Class<T> clazz) {
		try {
			return sorterClass.getConstructor(Class.class).newInstance(clazz);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getSorterName() {
		return sorterName;
	}
	
	@Override
	public String toString() {
		return sorterName;
	}
}
