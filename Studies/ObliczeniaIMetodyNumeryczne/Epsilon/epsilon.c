#include <stdio.h>

int main() {
    float       float_representant;
    double      double_representant;
    long double extended_representatn;

    int q;

    float_representant = (float) 1.0;
    q = 0;
    while (true) {
        float_representant /= 2.0;
        if (float_representant == ((float) 1.0))
            break;
        else
            q--;
    }
    printf ("Epsilon maszynowy dla typu float wynosi: 2^%d", q);

    return 0;
}
