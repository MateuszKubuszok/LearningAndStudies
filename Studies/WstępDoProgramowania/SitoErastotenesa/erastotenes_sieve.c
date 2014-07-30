#include <stdio.h>

int main () {
    signed char *Sieve;
    unsigned int i, j, n;

    printf ("Program will find all prime numbers less than or equal to n:\nn = ");
    while (scanf ("%u", &n) != 1) {
        printf ("n = ");
        while(getchar() != '\n');
    }

    if (n < 2) {
        printf ("There is no prime number less than 2.");
        return 0;
    }

    Sieve = calloc (((n-3)/2)+1, sizeof (signed char));

    for (i = 0; i <= ((n-3)/2);) {
        for (j = (2*i+3)*3; j <= n; j += 2*(2*i+3))
            Sieve [(j-3)/2] = 1;
        do i++; while (Sieve [i] != 0);
    }

    printf ("\n2", j = 1);
    for (i = 0; i <= ((n-3)/2); i++)
        if (Sieve [i] == 0)
            printf (", %d", 2*i+3, j++);

    printf (" - all in all %d prime numbers in range [1, %d].", j, n);

    printf ("\n\n%d", n);
    if ((n-3)%2 == 0 && Sieve [(n-3)/2] == 0)
        printf (" is a prime number.");
    else {
        printf (" = ");

        for (i = 0; i <= ((n-3)/2)/2; i++)
            if (Sieve [i] == 0)
                for (j = 2*i+3; n%j == 0; j *= (2*i+3))
                    Sieve [i]--;

        j = 0;
        for (i = 2; n%i == 0; i *= 2)
            j++;
        if (j != 0)
            printf ("2^%d", j);
        for (i = 0; Sieve [i] >= 0 && i <= (n-3)/2; i++);;
        if (i <= (n-3)/2)
            printf ("%s%d^%d", j != 0 ? " * " : "", 2*i+3, -1 * Sieve [i]);
        for (i++; i <= (n-3)/2; i++)
            if (Sieve [i] < 0 && i <= (n-3)/2)
                printf (" * %d^%d", 2*i+3, -1 * Sieve [i]);
    }

    free (Sieve);

    return 0;
}
