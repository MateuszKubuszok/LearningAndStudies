#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "complex.h"

#define PI 3.14159265358979323846


/*
 Dodaje do siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - skladnik dodawania,
 c2     - skladnik dodawania,
 result - zmienna na sume c1 i c2.
*/
void addC (ComplexP c1, ComplexP c2, ComplexP result) {
    result->Re = c1->Re + c2->Re;
    result->Im = c1->Im + c2->Im;
}

/*
 Porownuje dwie l. zespolone.

 Wykazue sie pewna tolerancja iwzgledniajaca niedokladnosc cyfrowego zapisu liczb.

 Parametry:
 c1 - liczba do porownania,
 c2 - liczba do porownania.

 Wartosc zwracana:
 1 jesli sa rowne, 0 jesli sie roznia.
*/
int compC (ComplexP c1, ComplexP c2) {
    return (fabs (c1->Re - c2->Re) < 0.000001) && (fabs (c1->Im - c2->Im) < 0.000001);
}

/*
 Oblicza l. sprzezana do c.

 Parametry:
 c      - sprzegana liczba,
 result - wynik sprzezenia.
*/
void conjC (ComplexP c, ComplexP result) {
    result->Re = c->Re;
    result->Im = (-1.0)*c->Im;
}

/*
 Dodaje do siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - odjemna,
 c2     - odjemnik,
 result - roznica c1 i c2.
*/
void diffC (ComplexP c1, ComplexP c2, ComplexP result) {
    result->Re = c1->Re - c2->Re;
    result->Im = c1->Im - c2->Im;
}

/*
 Dzieli przez siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - dzielna,
 c2     - dzielnik,
 result - iloraz c1 i c2.
*/
void divC (ComplexP c1, ComplexP c2, ComplexP result) {
    double a, b, c, d;

    a = c1->Re;
    b = c1->Im;
    c = c2->Re;
    d = c2->Im;

    result->Re = result->Im = 1.0 / (c*c + d*d);
    result->Re *= (a*c + b*d);
    result->Im *= (b*c - a*d);
}

/*
 Zwraca wskaznik na utworzona liczbe zepolona a+bi dla podanych a i b.

 Parametry:
 a - czesc rzeczywista,
 b - czesc urojona.

 Zwracana wartosc:
 wskaznik na liczbe.
*/
ComplexP makeC (double a, double b) {
    ComplexP C;

    C = (ComplexP) malloc (sizeof (Complex));

    if (C == NULL) {
        printf ("Memory allocation error!\n");
        return NULL;
    }

    C->Re = a;
    C->Im = b;

    return C;
}

/*
 Modul z liczby zespolonej.

 Parametry:
 c - l. zespolona.

 Wartosc zwracana:
 wartosc modulu z liczby.
*/
double modC (ComplexP c) {
    return (double) sqrt (c->Re*c->Re + c->Im*c->Im);
}

/*
 Mnozy przez siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - czynnik,
 c2     - czynnik,
 result - iloczyn c1 i c2.
*/
void mplyC (ComplexP c1, ComplexP c2, ComplexP result) {
    double a, b, c, d;

    a = c1->Re;
    b = c1->Im;
    c = c2->Re;
    d = c2->Im;

    result->Re = a*c - b*d;
    result->Im = a*d + b*c;
}

/*
 N-ta potega z liczby zespolonej.

 Parametry:
 c      - podstawa potegi,
 n      - wykladnik potegi,
 result - wynik potegowania.
*/
void nthPowC (ComplexP c, int n, ComplexP result) {
    double  Arg,
            Mod;

    Arg = atan (c->Im/c->Re);
    Mod = modC (c);

    result->Re = pow (Mod, n) * cos (n*Arg);
    result->Im = pow (Mod, n) * sin (n*Arg);
}

/*
 Oblicza jeden z pierwiastkow n-tego stopnia z liczby zespolonej.

 Parametry:
 c      - pierwiastkowana liczba,
 n      - wykladnik pierwiastka,
 k      - ktory z pierwiastkow jest obliczany (0, 1, 2, ... , n-1),
 result - wynik.
*/
void nthRootC (ComplexP c, int n, int k, ComplexP result) {
    double  Arg,
            Mod;

    if (n == 0)
        return;

    k %= n;

    Arg = atan (c->Im/c->Re);
    Mod = modC (c);

    result->Re = pow (Mod, 1.0/n) * cos ((Arg + 2.0*PI*k)/n);
    result->Im = pow (Mod, 1.0/n) * sin ((Arg + 2.0*PI*k)/n);
}

/*
 Wypisuje liczbe na standardowe wyjscie.

 Parametry:
 c - liczba do wyswietlenia.
*/
void printC (ComplexP c) {
    printf  (c->Im >= 0 ? "%f+%fi" : "%f%fi", c->Re, c->Im);
}

/*
 Wczytuje liczbe do struktury.

 Parametry:
 c - zmienna do wypelnienia.
*/
void readC (ComplexP c) {
    printf ("Re(c) = ");
    while (scanf ("%lf", &(c->Re)) == -1) {
        while (getchar () != '\n');
        printf ("Re(c) = ");
    }

    printf ("Im(c) = ");
    while (scanf ("%lf", &(c->Im)) == -1) {
        while (getchar () != '\n');
        printf ("Im(c) = ");
    }
}

/*
 Sluzy do porownywania dlugosci liczb po wyposaniu uch na wejscie.

 Uwzglednia tylko zmieniajace dlugosc znaki tj. minus przez Re c oraz ilosc liczb przed przecinkiem.

 Parametry:
 c - liczba dla ktorej liczona jest dlugosc.

 Wartosc zwracana:
 dlugosc sluzaca do porownan.
*/
int strCLength (ComplexP c) {
    double nrCopy;

    int length;

    length = c->Re < 0.0 ? 1 : 0; /* za dodatkowy minus przez caloscia */

    nrCopy = fabs (c->Re);
    length += nrCopy >= 1.0 ? (int) (floor (log10 (10.0 * nrCopy))) : 0;

    /* 0 jako pierwsza cyfra tez zajmuje miejsce */
     if (fabs (c->Re) < 1.0  || c->Re == 0.0) length++;

    nrCopy = fabs (c->Im);
    length += nrCopy >= 1.0 ? (int) (floor (log10 (10.0 * nrCopy))) : 0;

    /* 0 jako pierwsza cyfra tez zajmuje miejsce */
    if (fabs (c->Im) < 1.0 || c->Im == 0.0) length++;

    return length;
}
