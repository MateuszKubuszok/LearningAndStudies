#include <stdio.h>
#include <stdlib.h>

int main() {
    float   f1 = 9.0 / 27.0 * 3.0 - 1.0,
            f2 = 9.0 / 27.0 * 3.0 - 0.5 - 0.5;

    printf ("%g\n", 0.333333 * 3.0);
    printf ("%g\n", 0.3333333 * 3.0);

    printf ("guard digit:\nfloat: %s\n", f1 == f2 ? "yes" : "no");

    return 0;
}
