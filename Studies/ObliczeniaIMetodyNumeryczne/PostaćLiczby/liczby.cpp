#include <iostream>

using namespace std;

template <typename t>
void liczba_binarnie (t liczba) {
    union {
        t f;
        char bajt;
    } unia;
    int i,j;
    char* bajt = reinterpret_cast<char*> (&unia.bajt);
    unia.f = liczba;

    for (i = sizeof (t)-1; i >= 0; i--)
        for (j = 7; j >= 0; j--)
            cout << ((bajt[i] >> j) & 1);
}

template <typename t>
void epsilon (t* liczba, int* wykladnik) {
    t   e = (t) 1,
        d;

    *wykladnik = 1;
    do {
        *liczba = e;
        (*wykladnik)--;
        e /= 2.0f;
        d = e + 1.0f;
    } while (d > 1.0f);
}

template <typename t>
void najmniejsza (t* liczba, int* wykladnik) {
    t   e = (t) 1,
        d;

    *wykladnik = 1;
    do {
        *liczba = e;
        (*wykladnik)--;
        e /= 2.0f;
    } while (e > 0.0f);
}

main () {
    int         i;
    float       f;
    double      d;
    long double l;

    cout << "s - bit znaku" << endl;
    cout << "c - wykladnik cechy (kod dopelnien do 2)" << endl;
    cout << "m - mantysa (binarnie cyfry po przecinku + 1.0)" << endl;
    cout << endl;

    cout << "sccccccccmmmmmmmmmmmmmmmmmmmmmmm" << endl;
    epsilon<float> (&f, &i);
    liczba_binarnie<float> (f);
    cout << "(2) = 2^" << i << endl;
    najmniejsza<float> (&f, &i);
    liczba_binarnie<float> (f);
    cout << "(2) = 2^" << i << endl;

    cout << "scccccccccccmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" << endl;
    epsilon<double> (&d, &i);
    liczba_binarnie<double> (d);
    cout << "(2) = 2^" << i << endl;
    najmniejsza<double> (&d, &i);
    liczba_binarnie<double> (d);
    cout << "(2) = 2^" << i << endl;

    cout << "                 scccccccccccccccmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm" << endl;
    epsilon<long double> (&l, &i);
    liczba_binarnie<long double> (l);
    cout << "(2) = 2^" << i << endl;
    najmniejsza<long double> (&l, &i);
    liczba_binarnie<long double> (l);
    cout << "(2) = 2^" << i << endl << endl;

    epsilon<long double> (&l, &i);
    liczba_binarnie<long double> (l+1.0);
    cout << endl;
    liczba_binarnie<long double> (0.5);
    cout << endl;
    liczba_binarnie<long double> (0.25);
}
