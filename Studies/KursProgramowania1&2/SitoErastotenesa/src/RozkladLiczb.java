import java.util.*;

public class RozkladLiczb {
    int[] Sito;

    int MaxN;

    public RozkladLiczb (int n)
    throws MyException
    {
        int i, j;

        if (n < 2)
            throw new MyException ("N jest za maly!");
        else if (n > 10000000)
            throw new MyException ("N jest za duzy!");

        this.Sito = new int[n-1];

        this.MaxN = n;

        i = 2;
        do {
            while (i <= n && this.Sito [i-2] != 0)
                i++;

            for (j = i; j <= n; j += i)
                if (this.Sito [j-2] == 0)
                    this.Sito [j-2] = i;

            i++;
        } while (i <= n);
    }

    public int[] rozloz (int n)
    throws MyException
    {
        int i, j,
            NSqrt = (int) Math.sqrt (n) + 1,
            NCopy = n;
        boolean WasFirst = false;
        int[] Result;

        if (n < 0)
            throw new MyException ("Liczby ujemne (" + n + ") nie sa rozkladalne na czynniki pierwsze!");
        if (n > this.MaxN)
            throw new MyException ("Liczba " + n + " spoza zasiegu sita (" + this.MaxN + ")!");
        if (n < 2)
            throw new MyException ("Liczba " + n + " nie jest rozkladalna");

        Result = new int[n-1];

        if (this.Sito [n-2] == n)
            Result [n-2] = 1;
        else {
            j = 0;
            while (n % 2 == 0) {
                n /= 2;
                j++;
            }
            if (j > 0)
                Result [2-2] = j;

            for (i = this.Sito [NCopy-2] > 2 ? this.Sito [NCopy-2] : 3; i <= n; i += 2) {
                if (this.Sito [i-2] == i) {
                    j = 0;
                    while (n % i == 0) {
                        n /= i;
                        j++;
                    }
                    if (j > 0)
                        Result [i-2] = j;
                }
            }
        }

        return Result;
    }
}
