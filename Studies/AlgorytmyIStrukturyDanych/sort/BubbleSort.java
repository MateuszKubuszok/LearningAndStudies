public class InsertionSort {
    public static void insertionSort (Container array) {
        int i, j;

        for (i = 2; i <= array.getSize (); i++)
            for (j = i; j > 1 && array.lesser (j, j-1); j--) {
                array.swap (j-1, j);
                array.display ();
            }
        /*
        for (i = 2; i <= array.getSize (); i++) {
            for (j = i; j > 1 && array.lesser (j, j-1); j--);

        }
        */
    }
}
