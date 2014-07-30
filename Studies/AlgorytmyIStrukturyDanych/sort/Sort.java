public class Sort {
    public static void main (String args[]) {
        if (//args.length < 3 ||
            (!"heap".equals (args [0]) && !"insertion".equals (args [0]) && !"merge".equals (args [0]) && !"quick".equals (args [0])) ||
            (!"preview".equals (args [1]) && !"result".equals (args [1])) ||
            (!"full".equals (args [2]) && !"short".equals (args [2]))
        ) {
            System.out.println ("First argment must be sorting method [heap/insertion/merge/quick].");
            System.out.println ();
            System.out.println ("Second argument must tell whether show all steps [preview] \n\tor display result [result].");
            System.out.println ();
            System.out.println ("Third argument must tell whether show graphical preview [full] \n\tor values only [short].");
            System.out.println ();
            System.out.println ("Generated array depends on the rest of passed arguments:");
            System.out.println ("- no more arguments (or just 0): generate random array of size = 10");
            System.out.println ("- one positive integer: generates random array of given size,");
            System.out.println ("- one negative integer: generates descending array of size opposite to given,");
            System.out.println ("- many integers: array made of passed numbers (in full preview mode their \n\tabsolute values will be used).");
            return;
        }

        boolean     DisplayFull = "full".equals (args [2]),
                    Preview = "preview".equals (args [1]);
        Container   Array;
        int         i;
        int[]       OriginalArray;
        String[]    Numbers;

        Numbers = new String[args.length-3];
        for (i = 3; i < args.length; i++)
            Numbers [i-3] = args [i];

        OriginalArray = ArrayHelper.make (Numbers);
        if (OriginalArray.length == 0)
            return;

        System.out.println ("Starting from:");
        ArrayHelper.display (OriginalArray, DisplayFull);

        Array = new Container (OriginalArray, Preview, DisplayFull);
        if ("heap".equals (args [0]))
            Sort.heapSort (Array);
        if ("insertion".equals (args [0]))
            Sort.insertionSort (Array);
        if ("merge".equals (args [0]))
            Sort.mergeSort (Array);
        if ("quick".equals (args [0]))
            Sort.quickSort (Array);

        System.out.println ("Final result:");
        ArrayHelper.display (OriginalArray, DisplayFull);
        Array.displayStats ();
    }

    public static void heapSort (Container array) {
        HeapSort.heapSort (array);
        array.refreshArray ();
    }

    public static void insertionSort (Container array) {
        InsertionSort.insertionSort (array);
        array.refreshArray ();
    }

    public static void mergeSort (Container array) {
        MergeSort.mergeSort (array, 1, array.getSize ());
        array.refreshArray ();
    }

    public static void quickSort (Container array) {
        QuickSort.quickSort (array, 1, array.getSize ());
        array.refreshArray ();
    }
}
