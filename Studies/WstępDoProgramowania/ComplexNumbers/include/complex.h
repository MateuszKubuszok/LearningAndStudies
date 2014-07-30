/*
 Definicja struktury l. zespolonej.
*/
typedef struct complexNr {
    double  Re,
            Im;
} Complex;

typedef Complex* ComplexP;

/*
 Dodaje do siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - skladnik dodawania,
 c2     - skladnik dodawania,
 result - zmienna na sume c1 i c2.
*/
void addC (ComplexP c1, ComplexP c2, ComplexP result);

/*
 Porownuje dwie l. zespolone.

 Wykazue sie pewna tolerancja iwzgledniajaca niedokladnosc cyfrowego zapisu liczb.

 Parametry:
 c1 - liczba do porownania,
 c2 - liczba do porownania.

 Wartosc zwracana:
 1 jesli sa rowne, 0 jesli sie roznia.
*/
int compC (ComplexP c1, ComplexP c2);

/*
 Oblicza l. sprzezana do c.

 Parametry:
 c      - sprzegana liczba,
 result - wynik sprzezenia.
*/
void conjC (ComplexP c, ComplexP result);

/*
 Dodaje do siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - odjemna,
 c2     - odjemnik,
 result - roznica c1 i c2.
*/
void diffC (ComplexP c1, ComplexP c2, ComplexP result);

/*
 Dzieli przez siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - dzielna,
 c2     - dzielnik,
 result - iloraz c1 i c2.
*/
void divC (ComplexP c1, ComplexP c2, ComplexP result);

/*
 Zwraca wskaznik na utworzona liczbe zepolona a+bi dla podanych a i b.

 Parametry:
 a - czesc rzeczywista,
 b - czesc urojona.

 Zwracana wartosc:
 wskaznik na liczbe.
*/
ComplexP makeC (double a, double b);

/*
 Modul z liczby zespolonej.

 Parametry:
 c - l. zespolona.

 Wartosc zwracana:
 wartosc modulu z liczby.
*/
double modC (ComplexP c);

/*
 Mnozy przez siebie l. zespolone c1 i c2 i wynik zapisuje w zmiennej result.

 Parametry:
 c1     - czynnik,
 c2     - czynnik,
 result - iloczyn c1 i c2.
*/
void mplyC (ComplexP c1, ComplexP c2, ComplexP result);

/*
 N-ta potega z liczby zespolonej.

 Parametry:
 c      - podstawa potegi,
 n      - wykladnik potegi,
 result - wynik potegowania.
*/
void nthPowC (ComplexP c, int n, ComplexP result);

/*
 Oblicza jeden z pierwiastkow n-tego stopnia z liczby zespolonej.

 Parametry:
 c      - pierwiastkowana liczba,
 n      - wykladnik pierwiastka,
 k      - ktory z pierwiastkow jest obliczany (0, 1, 2, ... , n-1),
 result - wynik.
*/
void nthRootC (ComplexP c, int n, int k, ComplexP result);

/*
 Wypisuje liczbe na standardowe wyjscie.

 Parametry:
 c - liczba do wyswietlenia.
*/
void printC (ComplexP c);

/*
 Wczytuje liczbe do struktury.

 Parametry:
 c - zmienna do wypelnienia.
*/
void readC (ComplexP c);

/*
 Sluzy do porownywania dlugosci liczb po wyposaniu uch na wejscie.

 Uwzglednia tylko zmieniajace dlugosc znaki tj. minus przez Re c oraz ilosc liczb przed przecinkiem.

 Parametry:
 c - liczba dla ktorej liczona jest dlugosc.

 Wartosc zwracana:
 dlugosc sluzaca do porownan.
*/
int strCLength (ComplexP c);
