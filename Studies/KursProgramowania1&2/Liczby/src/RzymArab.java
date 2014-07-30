import java.lang.Math;

public class RzymArab {
    private static String[] liczby = { "I", "V", "X", "L", "C", "D", "M" };

    public static void main (String[] args) {
        int i, number;

        for (i = 0; i < args.length; i++) {
            try {
                number = Integer.parseInt (args [i]);

                try {
                    System.out.println (args [i] + " -> " + RzymArab.arab2rzym (number));
                } catch (RzymArabException ex) {
                    System.out.println (ex);
                }
            } catch (NumberFormatException ex) {
                try {
                    System.out.println (args [i] + " -> " + RzymArab.rzym2arab (args [i].toUpperCase ()));
                } catch (RzymArabException ex2) {
                    System.out.println (ex2);
                }
            }
        }
    }

    public static int rzym2arab (String rzym) throws RzymArabException {
        int i,
            biezaca,
            ileUciac,
            rzad,
            wynik = 0;

        String copy = rzym;

        for (i = 3; (i >= 0) && (rzym.length () > 0); i--) {
            biezaca = 0;
            ileUciac = 0;
            rzad = (int) Math.pow ((double) 10, (double) i);

            try {
                if (rzym.startsWith (RzymArab.liczby [2*i], 0)) {
                    // zaczyna sie od jednosci w danym rzedzie wielkosci: np. I, II, III, IV, IX
                    biezaca = rzad;
                    ileUciac = 1;

                    if (rzym.startsWith (RzymArab.liczby [2*i] + RzymArab.liczby [2*i], 1)) {
                        // jest to trojka w danym rzedzie wielkosci
                        biezaca = rzad * 3;
                        ileUciac = 3;
                    } else if (rzym.startsWith (RzymArab.liczby [2*i], 1)) {
                        // jest to dwojka w danym rzedzie wielkosci
                        biezaca = rzad * 2;
                        ileUciac = 2;
                    } else try {
                        if (rzym.startsWith (RzymArab.liczby [2*i+1], 1)) {
                            // jest to czworka w danym rzedzie wielkosci
                            biezaca = rzad * 4;
                            ileUciac = 2;
                        } else if (rzym.startsWith (RzymArab.liczby [2*i+2], 1)) {
                            // jest to dziewiatka w danym rzedzie wielkosci
                            biezaca = rzad * 9;
                            ileUciac = 2;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex2) { /* nic nie rob */ }
                } else if (rzym.startsWith (RzymArab.liczby [2*i+1], 0)) {
                    // zaczyna sie od piatki w danym rzedzie wielkosci: np. V, VI, VII, VIII
                    biezaca = rzad * 5;
                    ileUciac = 1;

                    if (rzym.startsWith (RzymArab.liczby [2*i] + RzymArab.liczby [2*i] + RzymArab.liczby [2*i], 1)) {
                        // jest to osemka w danym rzedzie wielkosci
                        biezaca = rzad * 8;
                        ileUciac = 4;
                    } else if (rzym.startsWith (RzymArab.liczby [2*i] + RzymArab.liczby [2*i], 1)) {
                        // jest to siodemka w danym rzedzie wielkosci
                        biezaca = rzad * 7;
                        ileUciac = 3;
                    } else if (rzym.startsWith (RzymArab.liczby [2*i], 1)) {
                        // jest to szostka w danym rzedzie wielkosci
                        biezaca = rzad * 6;
                        ileUciac = 2;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ex) { /* nic nie rob */ }

            //System.out.println ("Rzad (" + rzad + ") wartosc dla rzedu (" + biezaca + ") obecny (" + rzym + ")");

            wynik += biezaca;
            rzym = rzym.substring (ileUciac);
        }

        if (!rzym.equals (""))
            throw new RzymArabException (copy + " nie jest poprawna liczba rzymska!");

        return wynik;
    }

    public static String arab2rzym (int arab) throws RzymArabException {
        int i, j, rzad, wartoscWRzedzie;

        String wynik = "";

        if (arab < 1 || arab > 3999)
            throw new RzymArabException ("Podana liczba "+arab+" jest poza zakresem [1, 3999]!");

        for (i = 3; i >= 0; i--) {
            rzad = (int) Math.pow ((double) 10, (double) i);

            wartoscWRzedzie = arab/rzad;

            if (wartoscWRzedzie == 9) {
                // cyfry IX, XC, CM

                wynik += RzymArab.liczby [2*i] + RzymArab.liczby [2*i+2];
            }  else if (wartoscWRzedzie > 5) {
                // cyfry VI, VII, VIII, LX, LXX, ...

                wynik += RzymArab.liczby [2*i+1];
                for (j = 0; j < wartoscWRzedzie-5; j++)
                    wynik += RzymArab.liczby [2*i];
            } else if (wartoscWRzedzie == 5) {
                // cyfry V, L D

                wynik += RzymArab.liczby [2*i+1];
            } else if (wartoscWRzedzie == 4) {
                // cyfry IV, XL, CD

                wynik += RzymArab.liczby [2*i] + RzymArab.liczby [2*i+1];
            } else if (wartoscWRzedzie > 0) {
                // cyfry I, II, III, X, XX, XXX, ...

                for (j = 0; j < wartoscWRzedzie; j++)
                    wynik += RzymArab.liczby [2*i];
            }

            arab -= wartoscWRzedzie*rzad;
        }

        return wynik;
    }

    /*
    private static String subString (String cropped) {
        return RzymArab.subString (cropped, 0);
    }

    private static String subString (String cropped, int begin) {
        int Begin = cropped.length ()-1 >= begin ? begin : cropped-1;

        return cropped.substring (Begin);
    }

    private static String subString (String cropped, int begin, int end) {
        int End = cropped.length ()-1 >= end ? end : cropped-1;
        int Begin = End >= begin ? begin : end;

        return cropped.substring (Begin, End);
    }
    */
}
