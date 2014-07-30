public class MergeSort {
    public static void mergeSort (Container sArray, int begin, int end) {
        int Middle = (begin+end)/2;

        if (begin < end) {
            MergeSort.mergeSort (sArray, begin, Middle);
            MergeSort.mergeSort (sArray, Middle+1, end);
            MergeSort.merge (sArray, begin, Middle, end);
        }
    }

    private static void merge (Container sArray, int begin, int middle, int end) {
        int c = 0,
            i = begin,
            j = middle+1;

        Element[] Array = new Element[end-begin+1];

        while (i <= middle && j <= end)
            Array [c++] = sArray.get (sArray.lesser (i, j) ? i++ : j++);
        if (i <= middle)
            while (i <= middle)
                Array [c++] = sArray.get (i++);
        else
            while (j <= end)
                Array [c++] = sArray.get (j++);

        for (i = 0; i <= (end-begin); i++)
            sArray.set (i+begin, Array [i]);

        sArray.display ();
    }
}
