public class Sort {
	public static void insertionSort (Element[] array) {
        int i, j;
        Element Temp;

        for (i = 1; i < array.length; i++) {
            if (array [i-1].Value > array [i].Value) {
                Temp = array [i];
                j = i;
                do {
                	array [j] = array [--j];
                } while (j > 0 && Temp.Value < array [j-1].Value);
                array [j] = Temp;
            }
        }
    }
	
	public static void insertionSort (Container array)
	throws ContainerException {
        int i, j;
        Element Temp;

        for (i = 2; i <= array.size (); i++) {
            if (array.greater (i-1, i)) {
                Temp = array.get (i);
                j = i;
                array.InformAll = false;
                do {
                	array.set (j, array.get (--j));
                } while (j > 1 && Temp.Value < array.get (j-1).Value);
                array.InformAll = true;
                array.set (j, Temp);
            }
        }
    }
}
