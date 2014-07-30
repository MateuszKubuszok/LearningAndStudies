public class QuickSort {
    public static void quickSort (Container array, int begin, int end) {
        if (begin < end) {
            int[] Pivot = QuickSort.partition (array, begin, end);
            QuickSort.quickSort (array, begin, Pivot [1]);
            QuickSort.quickSort (array, Pivot [0], end);
        }
    }

    private static int[] partition (Container array, int begin, int end) {
        int i = begin,
            j = end;

        Element Pivot = array.get ((i+j)/2);

        do {
	        while (array.lesser (i, Pivot.currentlyAt ()))
                i++;
	        while (array.greater (j, Pivot.currentlyAt ()))
                j--;
	        if (i <= j) {
                if (i != j) {
                    array.swap (i, j);
                    array.display ();
                }
                i++;
	            j--;
	        }
	    } while (i <= j);

        int[] IJ = {i, j};

        return IJ;
    }
}
