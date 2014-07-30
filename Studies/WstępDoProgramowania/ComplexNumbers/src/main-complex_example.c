#include <stdio.h>

#include "complex.h"

int main () {
    Complex cplx_a,
            cplx_b,
            cplx_c;

    ComplexP a = &cplx_a,
             b = &cplx_b,
             c = &cplx_c;

    int n, k;

    printf ("Type a complex number a:\n");
    readC (a);

    printf ("\n\nType a complex number b:\n");
    readC (b);

    printf ("\na = ");
    printC (a);
    printf ("\nb = ");
    printC (b);

    printf ("\n\nMod a = %f", modC (a));
    printf ("\n\nMod b = %f", modC (b));

    printf ("\n\nconjuncion (a) = ");
    conjC (a, c);
    printC (c);

    printf ("\n\nconjuncion (b) = ");
    conjC (b, c);
    printC (c);

    printf ("\n\na + b = ");
    addC (a, b, c);
    printC (c);

    printf ("\n\na - b = ");
    diffC (a, b, c);
    printC (c);

    printf ("\n\na * b = ");
    mplyC (a, b, c);
    printC (c);

    printf ("\n\na / b = ");
    divC (a, b, c);
    printC (c);

    printf ("\n\nType some n (natural positive integer):\nn = ");
    do {
        while (scanf ("%d", &n) == -1) {
            while (getchar () != '\n');
            printf ("Wrong input!\nn = ");
        }

        if (n <= 0)
            printf ("Wrong input!\nn = ");

    } while (n <= 0);

    printf ("\n\na ^ n = ");
    nthPowC (a, n, c);
    printC (c);

    printf ("\n\na ^ 1/n = { ");
    nthRootC (a, n, 0, c);
    printC (c);
    for (k = 1; k < n; k++) {
        printf (", ");
        nthRootC (a, n, k, c);
        printC (c);
    }

    printf (" }\n");

    return 0;
}
