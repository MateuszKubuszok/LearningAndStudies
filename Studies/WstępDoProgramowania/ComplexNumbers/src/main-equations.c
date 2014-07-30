#include <stdio.h>

#include "complex.h"
#include "matrixc.h"

int main () {
    int n, m;

    MatrixC  A,
             X,
             B;


    printf ("Enter row number: ");
    while (scanf ("%d", &n) == -1) {
        while (getchar () != '\n');
    }

    printf ("Enter column number: ");
    while (scanf ("%d", &m) == -1) {
        while (getchar () != '\n');
    }


    A = allocMC (n, m);
    if (A == NULL)
        return 0;

    B = allocMC (n, 1);
    if (B == NULL) {
        freeMC (A, n, m);
        return 0;
    }

    X = allocMC (m, 1);
    if (X == NULL) {
        freeMC (A, n, m);
        freeMC (B, n, 1);
        return 0;
    }


    printf ("\nMatrix A [%dx%d]:\n", n, m);

    readMC  (A, n, m);
    printf ("\n");
    printMC (A, n, m);

    printf ("\n\nVector B [%dx1]:\n", n);

    readMC  (B, n, 1);
    printf ("\n");
    printMC (B, n, 1);

    solveEquationsSystemMC (A, B, X, n, m);

    return 0;
}
