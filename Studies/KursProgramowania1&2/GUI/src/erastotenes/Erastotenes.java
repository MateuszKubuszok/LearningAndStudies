package erastotenes;

public class Erastotenes {
    private boolean[] Sieve;
    private int MaxN;

    public Erastotenes (int n)
    throws Exception
    {
        int i, j;

        if (n < 2)
            throw new Exception ("N is smaller than the least prime number!");

        this.Sieve = new boolean[n-1];

        this.MaxN = n;

        for (i = 2; i <= n; i++)
            this.Sieve [i-2] = true;

        i = 2;
        do {
            while (i <= n && !this.Sieve [i-2])
                i++;

            for (j = 2*i; j <= n; j += i)
                this.Sieve [j-2] = false;

            i++;
        } while (i <= n);
    }

    public boolean prime (int n)
    throws Exception
    {
        if (n < 0)
            throw new Exception ("Negative numbers (" + n + ") cannot be prime!");
        if (n > this.MaxN)
            throw new Exception ("Number \"" + n + "\" is out of sieves range (max n: " + this.MaxN + ")!");
        if (n < 2)
            return false;

        return this.Sieve [n-2];
    }
    
    public int size () {
    	return this.MaxN;
    }
}
