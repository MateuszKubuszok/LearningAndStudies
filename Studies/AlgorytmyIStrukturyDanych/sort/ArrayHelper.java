import java.util.Random;

class ArrayHelper {
    static void display (int[] array, boolean displayFull) {
        int i, j, Max;

        Max = 0;
        for (i = 0; i < array.length; i++)
            if (array [i] > Max)
                Max = array [i];

        if (displayFull) {
            for (j = Max; j > 0; j--) {
                for (i = 0; i < array.length; i++) {
                    if (array [i] >= j)
                        System.out.print ("@");
                    else
                        System.out.print (" ");
                }
                System.out.print ("\n");
            }
            for (i = 0; i < array.length; i++)
                System.out.print ("-");
        } else {
            for (i = 0; i < array.length; i++)
                System.out.print (array [i] + " ");
        }
        System.out.print ("\n\n");
    }

    static int[] make (String numbers[]) {
        int[] Array;
        int   i;

        switch (numbers.length) {
            default:
                Array = new int[numbers.length];
                for (i = 0; i < numbers.length; i++) {
                    try {
                        Array [i] = Integer.parseInt (numbers [i]);
                    } catch (NumberFormatException ex) {
                        System.out.println (numbers [i] + " is not a valid integer!");
                        return new int[0];
                    }

                    if (Array [i] < 0)
                        Array [i] *= -1;
                }
            break;

            case 1:
                try {
                    i = Integer.parseInt (numbers [0]);
                } catch (NumberFormatException ex) {
                    i = 10;
                }

                if (i > 0) {
                    Array = new int[i];
                    ArrayHelper.makeRandom (Array);
                    break;
                }
                else if (i < 0) {
                    Array = new int[-i];
                    ArrayHelper.makeDescending (Array);
                    break;
                }

            case 0:
                Array = new int[10];
                ArrayHelper.makeRandom (Array);
            break;
        }

        return Array;
    }

    private static void makeDescending (int[] array) {
        int i;

        for (i = 0; i < array.length; i++)
            array [i] = array.length-i-1;
    }

    private static void makeRandom (int[] array) {
        int i, j;
        boolean ValueAppeared;
        Random Rand = new Random();

        for (i = 0; i < array.length; i++) {
            do {
                array [i] = Rand.nextInt (array.length);
                ValueAppeared = false;
                for (j = i-1; j >= 0; j--)
                    if (array [i] == array [j])
                        ValueAppeared = true;
            } while (ValueAppeared);
        }
    }
}
