public class Liczba {
    private static String[] Digits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    private int n;

    Liczba (int n) {
        this.n = n;
    }

    Liczba (String n, int podstawa) throws LiczbaException {
        if (podstawa > 16 || podstawa < 2)
            throw new LiczbaException ("Podstawa " + podstawa + " jest poza dopuszczalnym zakresem [2,16]!");

        String  OstatniaCyfra,
                Kopia = n;
        int     i,
                rzad = 1,
                znak = 1;

        if (n.length () > 0 && n.substring (0, 1).equals ("-")) {
            znak = -1;
            n = n.substring (1);
        }

        while (n.length () > 0) {
            OstatniaCyfra = n.substring (n.length ()-1);

            for (i = 0; i < Liczba.cyfry.length; i++)
                if (Liczba.cyfry [i].equals (OstatniaCyfra)) {
                    if (i >= podstawa)
                        throw new LiczbaException (Kopia + " nie jest liczba z systemu o podstawie " + podstawa + "!");

                    this.n += rzad * i;
                    break;
                }
            if (i >= Liczba.cyfry.length)
                throw new LiczbaException (Kopia + " nie jest liczba z systemu o podstawie " + podstawa + "!");

            n = n.substring (0, n.length ()-1);
            rzad *= podstawa;
        }

        this.n *= znak;
    }

    public String zapis (int podstawa) throws LiczbaException {
        if (podstawa > 16 || podstawa < 2)
            throw new LiczbaException ("Podstawa " + podstawa + " jest poza dopuszczalnym zakresem [2,16]!");

        String wynik = new String ("");

        int znak = 1,
            robocza = this.n,
            pomocnicza;

        if (this.n < 0)
            znak = -1;

        robocza *= znak;

        if (robocza != 0) {
            while (robocza > 0) {
                pomocnicza = robocza % podstawa;
                robocza /= podstawa;

                if (pomocnicza > 0 || robocza > 0)
                    wynik = Liczba.cyfry [pomocnicza] + wynik;
            }
        } else
            wynik = new String ("0");

        if (znak < 0)
            wynik = "-" + wynik;

        return wynik;
    }
}
