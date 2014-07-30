public class InsertionSort {
    public static void insertionSort (Container array) {
        int i, j;
        Element Temp;

        for (i = 2; i <= array.getSize (); i++) {
            if (array.greater (i-1, i)) {
                Temp = array.get (i);
                j = i;
                do {
                    array.set (j, array.get ((j--)-1));
                    array.addComparision ();
                } while (j > 1 && Temp.getValue () < array.get (j-1).getValue ());
                array.set (j, Temp);
                array.display ();
            }
        }
    }
}
