package sort.analyse.sorters;

import java.lang.reflect.Array;

public class MergeSorter<T extends Comparable<T>> extends AbstractSorter<T> {
	public MergeSorter(Class<T> dataType) {
		super(dataType);
	}
	
	@Override
	protected void actualSort() {
		mergeSort(0, getArray().length-1);
	}
	
	private void mergeSort(int left, int right) {
		if (0 <= left && left < right && right <= getArray().length-1) {
			int middle = (left+right)/2;
			mergeSort(left, middle);
			mergeSort(middle+1, right);
			merge(left, middle, right);
		}
	}
	
	private void merge(int left, int middle, int right) {
		int x = left;
		int y = middle+1;
		@SuppressWarnings("unchecked")
		T[] tmp = (T[]) Array.newInstance(getSortedType(), right-left+1);
		int current = 0;
		
		while (current < tmp.length) {
			if (x > middle) {
				// rewrite end of second array
				while (current < tmp.length) {
					tmp[current++] = getArray()[y++];
					notifyAction();
				}
				break;
			} else if (y > right) {
				// rewrite end of first array
				while (current < tmp.length) { 
					tmp[current++] = getArray()[x++];
					notifyAction();
				}
				break;			
			}
			
			// rewrite smaller of two arrays
			if (compare(x, y) <= 0) {
				tmp[current++] = getArray()[x++];
				notifyAction();
			} else {
				tmp[current++] = getArray()[y++];
				notifyAction();
			}
		}
		
		// copy changes back to sorted array
		for (int i = 0; i < tmp.length; i++)
			getArray()[left+i] = tmp[i];
	}
}
