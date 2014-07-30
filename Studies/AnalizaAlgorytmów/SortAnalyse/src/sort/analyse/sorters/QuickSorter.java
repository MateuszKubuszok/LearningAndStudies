package sort.analyse.sorters;

public class QuickSorter<T extends Comparable<T>> extends AbstractSorter<T> {
	public QuickSorter(Class<T> dataType) {
		super(dataType);
	}
	
	@Override
	protected void actualSort() {
		quickSort(0, getArray().length-1);
	}
	
	private void quickSort(int left, int right) {
		if (0 <= left && left < right && right <= getArray().length-1) {
			int pivotPosition = partition(left, right);
			quickSort(left, pivotPosition-1);
			quickSort(pivotPosition+1, right);
		}
	}
	
	private int partition(int left, int right) {
		int pivotPosition = left; 
		int x = left+1;
		int y = right;
		
		while (x < y) {
			// while T[x] <= pivot move to right
			while (x < y && compare(x, pivotPosition) <= 0)
				x++;
			// while pivot < T[y] move to left
			while (x < y && compare(pivotPosition, y) < 0)
				y--;
			// if x < y and T[y] <= pivot < T[x], swap them, move x to right and y to left 
			if (x < y && compare(y, pivotPosition) <= 0 && compare(pivotPosition, x) < 0) {
				swap(x, y);
				x++;
				y--;
			}
		}
		
		// after partition either: x = y, or x = y + 1 (..., y, x, ...)
		// then, partition would happen either at y or y-1
		if (compare(y, pivotPosition) <= 0) {
			swap(y, pivotPosition);
			pivotPosition = y;
		} else if (compare(y-1, pivotPosition) <= 0) {
			swap(y-1, pivotPosition);
			pivotPosition = y-1;
		}
		
		return pivotPosition;
	}
}
