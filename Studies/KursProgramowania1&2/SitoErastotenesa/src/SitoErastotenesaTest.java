public class SitoErastotenesaTest {
    public static void main (String[] args) {
        int i, Container, N = 0;
        SitoErastotenesa Sito;

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

            if (Container > N)
                N = Container;
        }

        if (N < 2) {
            System.out.println ("Nie podano zadnej liczby mogacej byc liczba pierwsza (>= 2).");
            return;
        }

        try {
            Sito = new SitoErastotenesa (N);
        } catch (MyException ex) {
            System.out.println (ex);
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
                System.out.println (N + (Sito.prime (N) ? "" : " nie") + " jest pierwsza.");
            } catch (MyException ex) {
                System.out.println (ex);
            }
        }
    }
}
