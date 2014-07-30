#include <stdbool.h>
#include <stdio.h>

int main () {
    bool *Sieve;
    unsigned long int i, j, k, n;

    printf ("Program will find all prime numbers less than or equal to n:\nn = ");
    while (scanf ("%lu", &n) != 1) {
        printf ("n = ");
        while(getchar() != '\n');
    }

    i = -1;
    if (i - n < 3){
        printf ("Number you typed (%ul) is to big to calculate.", n);
        return 0;
    } else if ((Sieve = calloc (((n-3)/2)+1, sizeof (bool))) == NULL) {
        printf ("Program could not allocate memory for this task.");
        return 0;
    } else if (n < 2) {
        printf ("There is no prime number less than 2.");
        return 0;
    }

    for (i = 0; 2*i+3 <= n;) {
        for (j = (2*i+3)*3; j <= n; j += 2*(2*i+3))
            Sieve [(j-3)/2] = 1;
        do i++; while (Sieve [i] != 0);
    }

    printf ("\n2", j = 1);
    for (i = 0; 2*i+3 <= n; i++)
        if (Sieve [i] == 0)
            printf (", %lu", 2*i+3, j++);

    printf (" - all in all %lu prime numbers in range [1, %lu].", j, n);

    printf ("\n\n%d", n);
    if ((n-3)%2 == 0 && Sieve [(n-3)/2] == 0)
        printf (" is a prime number.");
    else {
        printf (" = ");

        j = 0;
        for (i = 2; n%i == 0; i *= 2)
            j++;
        if (j != 0)
            printf ("2^%lu", j);

        for (i = 0; i <= (n-3)/2 && (n%(2*i+3) != 0 || Sieve [i] > 0); i++);;

        if (i <= (n-3)/2) {
            k = 0;
            for (j = 2*i+3; n%j == 0; j *= (2*i+3))
                k++;
            printf ("%s%lu^%lu", n%2==0 ? " * " : "", 2*i+3, k);

            for (i++; i <= (n-3)/2; i++)
                if (Sieve [i] == 0 && i <= (n-3)/2) {
                    k = 0;
                    for (j = 2*i+3; n%j == 0; j *= (2*i+3))
                        k++;
                    if (k > 0) {
                        printf (" * %lu^%lu", 2*i+3, k);
                        n /= (j/(2*1+3));
                    }
                }
        }
    }

    free (Sieve);

    return 0;
}
