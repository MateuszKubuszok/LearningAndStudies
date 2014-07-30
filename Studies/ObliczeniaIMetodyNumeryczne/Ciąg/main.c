#include <stdio.h>
#include <stdlib.h>

#define a 2.0
#define b 10.0
#define k 30

#define display_format "x(%d) %s= %g\n      = %g\n\n"

void liczba_binarnie (float liczba) {
    union {
        float f;
        char bajt;
    } unia;
    int i,j;
    char* bajt = (char*) (&unia.bajt);
    unia.f = liczba;

    for (i = sizeof (float)-1; i >= 0; i--)
        for (j = 7; j >= 0; j--)
            printf ("%d", (bajt[i] >> j) & 1);
}

int main() {
    int         i;

    float       float_forward  [k+2],
                float_backward [k+2];

    double      double_forward [k+2],
                double_backward[k+2];

    // float

    float_forward[0] = 1.0f;
    float_forward[1] = (1.0f/((float) a));
    for (i = 2; i <= k+1; i++)
        float_forward[i] = b/a*float_forward[i-1] - float_forward[i-2];

    float_backward[k+1] = float_forward[k+1];
    float_backward[k] = float_forward[k];
    for (i = k-1; i >= 0; i--)
        float_backward[i] = b/a*float_backward[i+1] - float_backward[i+2];

    printf ("float:\n");
    for (i = 0; i <= k+1; i++) {
        liczba_binarnie (float_forward[i]); printf ("\n");
        liczba_binarnie (float_backward[i]); printf ("\n");
        printf (display_format, i, i < 10 ? " " : "", float_forward[i], float_backward[i]);

    }

    // double
/*
    double_forward[0] = 1.0;
    double_forward[1] = (1.0/(a));
    for (i = 2; i <= k+1; i++)
        double_forward[i] = (b/a*double_forward[i-1] - double_forward[i-2]);

    double_backward[k+1] = double_forward[k+1];
    double_backward[k] = double_forward[k];
    for (i = k-1; i >= 0; i--)
        double_backward[i] = b/a*double_backward[i+1] - double_backward[i+2];

    printf ("double:\n");
    for (i = 0; i <= k+1; i++)
        printf (display_format, i, i < 10 ? " " : "", double_forward[i], double_backward[i]);
*/
    return 0;
}
