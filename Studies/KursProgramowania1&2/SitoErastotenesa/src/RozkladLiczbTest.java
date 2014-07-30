public class RozkladLiczbTest {
    public static void main (String[] args) {
        boolean Pierwsza;
        int i, j, Container, N = 2;
        RozkladLiczb Rozklad;
        int[] Czynniki;

        if (args.length == 0) {
            System.out.println ("Nie podano argumentow!");
            return;
        }

        for (i = 0; i < args.length; i++) {
            try {
                Container = Integer.parseInt (args [i]);
            } catch (NumberFormatException ex) {
                continue;
            }

            if (Container > N) {
                if (Container <= 10000000)
                    N = Container;
                else
                    System.out.println (Container + " jest za duzy aby zagwarantowac poprawne utworzenie sita!");
            }
        }

        try {
            Rozklad = new RozkladLiczb (N);
        } catch (MyException ex2) {
            System.out.println (ex2);
            return;
        }

        for (i = 0; i < args.length; i++) {
            try {
                N = Integer.parseInt (args [i]);
            } catch (NumberFormatException ex) {
                System.out.println (args [i] + " nie jest poprawna liczba!");
                continue;
            }

            try {
                Czynniki = Rozklad.rozloz (N);
                Pierwsza = true;
                System.out.print (N + " = ");
                for (j = 2; j <= N; j++)
                    if (Czynniki [j-2] > 0) {
                        System.out.print ((Pierwsza ? "" : " * ") + j + "^" + Czynniki [j-2]);
                        Pierwsza = false;
                    }
                System.out.println ();
            } catch (MyException ex) {
                System.out.println (ex);
            }
        }
    }
}
