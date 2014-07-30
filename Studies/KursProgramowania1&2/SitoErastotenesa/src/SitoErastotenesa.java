public class SitoErastotenesa {
    boolean[] Sito;

    int MaxN;

    public SitoErastotenesa (int n)
    throws MyException
    {
        int i, j;

        if (n < 2)
            throw new MyException ("N jest za maly!");

        this.Sito = new boolean[n-1];

        this.MaxN = n;

        for (i = 2; i <= n; i++)
            this.Sito [i-2] = true;

        i = 2;
        do {
            while (i <= n && !this.Sito [i-2])
                i++;

            for (j = 2*i; j <= n; j += i)
                this.Sito [j-2] = false;

            i++;
        } while (i <= n);
    }

    public boolean prime (int n)
    throws MyException
    {
        if (n < 0)
            throw new MyException ("Liczby ujemne (" + n + ") nie moga byc pierwsze!");
        if (n > this.MaxN)
            throw new MyException ("Liczba " + n + " spoza zasiegu sita" + this.MaxN + "!");
        if (n < 2)
            return false;

        return this.Sito [n-2];
    }
}
