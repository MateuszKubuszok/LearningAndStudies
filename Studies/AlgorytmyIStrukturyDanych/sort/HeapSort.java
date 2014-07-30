public class HeapSort {
    public static void heapSort (Container array) {
        int i, Size;

        Size = array.getSize ();
        HeapSort.buildHeap (array);

        for (i = Size; i >= 2; i--) {
            if (array.differ (1, i)) {
                array.swap (1, i);
                array.display ();
            }
            HeapSort.heapify (array, --Size, 1);
        }
    }

    private static void buildHeap (Container array) {
        int i,
            Size = array.getSize ();

        for (i = (Size)/2; i >= 1; i--)
            HeapSort.heapify (array, Size, i);
    }

    private static void heapify (Container heap, int size, int i) {
        int Largest = i;

        if (2*i <= size && heap.greater (2*i, Largest))
            Largest = 2*i;
        if (2*i+1 <= size && heap.greater (2*i+1, Largest))
            Largest = 2*i+1;
        if (Largest != i) {
            heap.swap (i, Largest);
            heap.display ();
            HeapSort.heapify (heap, size, Largest);
        }
    }
}
