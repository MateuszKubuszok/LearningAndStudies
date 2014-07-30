package factorization;

public class Factorize {
	int[] Sito;

    int MaxN;

    public Factorize (int n)
    throws Exception
    {
        int i, j;

        if (n < 2)
            throw new Exception (n + " is not factorizable!");
        else if (n > 10000000)
            throw new Exception (n + " whould took to much memory!!");

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

    public int[] factorize (int n)
    throws Exception
    {
        int i, j,
            NSqrt = (int) Math.sqrt (n) + 1,
            NCopy = n;
        boolean WasFirst = false;
        int[] Result;

        if (n < 0)
            throw new Exception ("Negative numbers (" + n + ") aren't factorizable over natural field!");
        if (n > this.MaxN)
            throw new Exception ("Number \"" + n + "\" out of sieve's range (n max: " + this.MaxN + ")!");
        if (n < 2)
            throw new Exception ("Number " + n + " is not factorizable!");

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
    
    public int size () {
    	return this.MaxN;
    }
}
