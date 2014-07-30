/*
 Deklaracja typu macierzy zespolonej.
*/
typedef ComplexP** MatrixC;

/*
 Dodaje do siebie dwie macierze A i B i wynik zapisuje w result.

 Parametry:
 A      - macierz,
 B      - macierz,
 result - macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy.
*/
void addMC (MatrixC A, MatrixC B, MatrixC result, int n, int m);

/*
 Dodaje liczbe do wszytkich elementow macierzy.

 Parametry:
 matrix - macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy,
 c      - dodawana liczba.
*/
void addNrToMC (MatrixC matrix, int n, int m, ComplexP c);

/*
 Alokuje pamiec dla macierzy n x m.

 Parametry
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.

 Wartosc zwracana:
 wskaznik do macierzy jesli sukces, NULL w przypadku porazki.
*/
MatrixC allocMC (int n, int m);

/*
 Oblicza wyznacznik macierzy n-tego stopnia.

 Parametry:
 matrix - wskaznik na macierz,
 n      - stopien macierzy,
 result - zwracana wartos wyznacznika.
*/
void detMC (MatrixC matrix, int n, ComplexP result);

/*
 Zwalnia pamiec zajeta przez macierz o n wierszach.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.
*/
void freeMC (MatrixC matrix, int n, int m);

/*
 Dokonuje eliminacji Gaussa na macierzy.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.

 Zwracana wartosc:
 ilosc przeniesien wierszy dokonanych podczas eliminacji (potrzebna np. przy obliczaniu wyznacznika).
*/
int gaussanEliminationMC (MatrixC matrix, int n, int m);

/*
 Dokonuje eliminacji Gaussa-Jordana na macierzy.

 Parametry:
 matrix     - wskaznik na macierz,
 n          - wymiar pionowy macierzy,
 m          - wymiar poziomy macierzy,
 extended   - 1 jesli jest to rozszerzona macierz ukladu (ostatnia kolumna nie ma byc zerowana), 0 w przeciwnym razie.

 Zwracana wartosc:
 ilosc przeniesien wierszy dokonanych podczas eliminacji (potrzebna np. przy obliczaniu wyznacznika).
*/
int gaussanJordanEliminationMC (MatrixC matrix, int n, int m, int extended);

/*
 Mnozy przez siebie macierze A i B i wynik mnozenia zapisuje w result.

 Parametry:
 A      - macierz (n x p),
 B      - macierz (p x m),
 result - macierz (n x m),
 n      - wymiar macierzy,
 p      - wymiar macierzy,
 m      - wymiar macierzy.
*/
void mplyMC (MatrixC A, MatrixC B, MatrixC result, int n, int p, int m);

/*
 Mnozy wszystkie elementy macierzy przez liczbe.

 Parametry:
 matrix - macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy,
 c      - mnoznik.
*/
void mplyMCbyNr (MatrixC matrix, int n, int m, ComplexP c);

/*
 Drukuje macierz na standardowe wyjscie.

 Parametry:
 matrix     - wskaznik na macierz,
 n          - wymiar pionowy macierzy,
 m          - wymiar poziomy macierzy.
*/
void printMC (MatrixC matrix, int n, int m);

/*
 Oblicza rzad macierzy o rozmiarach n x m.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.

 Wartosc zwracanaL
 wartosc rzedu macierzy.
*/
int rankMC (MatrixC matrix, int n, int m);

void readMC (MatrixC matrix, int n, int m);

/*
 Przerzuca wiersze z 0 w okreslonym rzedzie macierzy pod wiersze tego 0 pozbawione.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 k      - sprawdzany rzad,
 from   - wiersz od ktorego maja byc sprawdzane 0 w rzedach.

 Zwracana wartosc:
 Liczba przeniesien wierszy (istotna np. przy obliczaniu wyznacznika).
*/
int sortMCbyRow (MatrixC matrix, int n, int k, int from);

/*
 Rozwiazuje uklad rownan liniowych i oblicza rozwiazanie (jesli jest jedno).

 Parametry:
 A - macierz glowna (n x m),
 B - wektor wyrazow wolnych (n x 1),
 X - wektor rozwiazan (zapisane zostana do niego rozwiazania) (m x 1),
 n - wymiar pionowy macierzy glownej,
 m - wymiar poziomy macierzy glownej.

 Wartosc zwracana:
 1 - uklad ma rozwiazanie, 0 - uklad nie ma rozwiazan, -1 uklad ma nieskonczenie wiele rozwiazan, -2 wystapil blad alokacji.
*/
int solveEquationsSystemMC (MatrixC A, MatrixC B, MatrixC X, int n, int m);
