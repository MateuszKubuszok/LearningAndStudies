#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "complex.h"
#include "matrixc.h"

/*
 Dodaje do siebie dwie macierze A i B i wynik zapisuje w result.

 Parametry:
 A      - macierz,
 B      - macierz,
 result - macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy.
*/
void addMC (MatrixC A, MatrixC B, MatrixC result, int n, int m) {
    int i,j;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++)
            addC (A [i][j], B [i][j], result [i][j]);
}

/*
 Dodaje liczbe do wszytkich elementow macierzy.

 Parametry:
 matrix - macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy,
 c      - dodawana liczba.
*/
void addNrToMC (MatrixC matrix, int n, int m, ComplexP c) {
    int i,j;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++)
            addC (matrix [i][j], c, matrix [i][j]);
}

/*
 Alokuje pamiec dla macierzy n x m.

 Parametry
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.

 Wartosc zwracana:
 wskaznik do macierzy jesli sukces, NULL w przypadku porazki.
*/
MatrixC allocMC (int n, int m) {
    int i, j;

    MatrixC matrix;

    matrix = (MatrixC) malloc (sizeof (ComplexP*) * n);

    if (matrix == NULL) {
        printf ("Memory allocation error!\n");

        return NULL;
    }

    for (i = 0; i < n; i++) {
        matrix [i] = (ComplexP*) malloc (sizeof (ComplexP) * m);

        if (matrix [i] == NULL) {
            printf ("Memory allocation error!\n");

            freeMC (matrix, i-1, m);

            return NULL;
        }


        for (j = 0; j < m; j++) {
            matrix [i][j] = (ComplexP) malloc (sizeof (Complex));
            if (matrix [i][j] == NULL) {
                printf ("Memory allocation error!\n");

                for (j--; j >= 0; j--)
                    free (matrix [i][j]);

                freeMC (matrix, i-1, m);

                return NULL;
            }
        }

    }

    return matrix;
}

/*
 Oblicza wyznacznik macierzy n-tego stopnia.

 Parametry:
 matrix - wskaznik na macierz,
 n      - stopien macierzy,
 result - zwracana wartos wyznacznika.
*/
void detMC (MatrixC matrix, int n, ComplexP result) {
    int     i,
            j;

    MatrixC Copy;


    Copy = allocMC (n, n);

    if (Copy == NULL)
        return;

    for (i = 0; i < n; i++)
        for (j = 0; j < n; j++) {
            Copy [i][j]->Re = matrix [i][j]->Re;
            Copy [i][j]->Im = matrix [i][j]->Im;
        }

    result->Re = gaussanEliminationMC (Copy, n, n) % 2 == 0 ? 1.0 : -1.0;
    result->Im = 0.0;

    for (i = 0; i < n; i++)
        mplyC (result, Copy [i][i], result);


    freeMC (Copy, n, n);
}

/*
 Zwalnia pamiec zajeta przez macierz o n wierszach.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.
*/
void freeMC (MatrixC matrix, int n, int m) {
    int i, j;

    for (i = 0; i < n; i++) {
        for (j = 0; j < m; j++)
            if (matrix [i][j] != NULL)
                free (matrix [i][j]);
        free (matrix [i]);
    }

    free (matrix);
}

/*
 Dokonuje eliminacji Gaussa na macierzy.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.

 Zwracana wartosc:
 ilosc przeniesien wierszy dokonanych podczas eliminacji (potrzebna np. przy obliczaniu wyznacznika).
*/
int gaussanEliminationMC (MatrixC matrix, int n, int m) {
    Complex  Div;
    ComplexP DivP = &Div,
             Helper,
             NullC;

    int     i,
            i2,
            j,
            j2,
            Switches = 0;

    Helper = makeC (0.0, 0.0);
    NullC = makeC (0.0, 0.0);

    /* Szukanie pierwszej niezerowej kolumny */
    for (j = 0; j < m; j++)
        for (i = 0; i < n; i++)
            if (!compC (matrix [i][j], NullC))
                goto EndOfLoop;
    EndOfLoop:

    /* Przenoszenie do gory niezerowych pol pierwszej niezerowej kolumny */
    Switches += sortMCbyRow (matrix, n, j, 0);

    for (i = 0; i < n; i++) {
        for (j = i; j < m && compC (matrix [i][j], NullC); j++);

        if (j == m) /* j - obecnie pierwszy niezerowy w wierszu */
            continue;


        for (i2 = i+1; i2 < n; i2++) {
            if (compC (matrix [i2][j], NullC))
                continue;

            divC (matrix [i2][j], matrix [i][j], DivP);
            for (j2 = j; j2 < m; j2++) {
                mplyC (DivP, matrix [i][j2], Helper);
                diffC (matrix [i2][j2], Helper, matrix [i2][j2]);
            }
        }

        Switches += sortMCbyRow (matrix, n, j, i+1);
    }

    free (NullC);
    free (Helper);

    return Switches;
}

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
int gaussanJordanEliminationMC (MatrixC matrix, int n, int m, int extended) {
    Complex  Div;
    ComplexP DivP = &Div,
             NullC,
             Helper;

    int i, i2,
        j, j2,
        Switches;

    Helper = makeC (0.0, 0.0);
    NullC = makeC (0.0, 0.0);

    Switches = gaussanEliminationMC (matrix, n, m);

    for (i = n-1; i > 0; i--) {/* Po zejsciu do pierwszego wiersza nie ma sensu eliminowac wierszy "wyzej" */
        for (j = extended ? m-2 : m-1; j >= 0; j--) {
            if (compC (matrix [i][j], NullC))
                continue;

            for (i2 = i-1; i2 >= 0; i2--) {
                divC (matrix [i2][j], matrix [i][j], DivP);

                for (j2 = m-1; j2 >= 0; j2--) {
                    mplyC (DivP, matrix [i][j2], Helper);
                    diffC (matrix [i2][j2], Helper, matrix [i2][j2]);
                }
            }
        }
    }

    free (NullC);
    free (Helper);

    return Switches;
}

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
void mplyMC (MatrixC A, MatrixC B, MatrixC result, int n, int p, int m) {
    Complex Helper;

    int i, j, c;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++) {
            result [i][j]->Re = 0.0;
            result [i][j]->Im = 0.0;

            for (c = 0; c < p; c++) {
                mplyC (A [i][c], B [c][j], &Helper);
                addC (result [i][j], &Helper, result [i][j]);
            }
        }
}

/*
 Mnozy wszystkie elementy macierzy przez liczbe.

 Parametry:
 matrix - macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy,
 c      - mnoznik.
*/
void mplyMCbyNr (MatrixC matrix, int n, int m, ComplexP c) {
    int i,j;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++)
            mplyC (matrix [i][j], c, matrix [i][j]);
}

/*
 Drukuje macierz na standardowe wyjscie.

 Parametry:
 matrix     - wskaznik na macierz,
 n          - wymiar pionowy macierzy,
 m          - wymiar poziomy macierzy.
*/
void printMC (MatrixC matrix, int n, int m) {
    int i,
        j,
        c,
        *colDigits,
        colLength;

    /* Obliczanie minimalnej ilosci miejsca dla kazdej kolumny */

    colDigits = (int*) calloc (m, sizeof (int));
    if (colDigits == NULL) {
        printf ("Memory allocation error!\n");
        return;
    }

    for (j = 0; j < m; j++)
        for (i = 0; i < n; i++) {
            colLength = strCLength (matrix [i][j]);
            if (colLength > colDigits [j])
                colDigits [j] = colLength;
        }

    /* Wypisywanie macierzy */
    for (i = 0; i < n; i++) {
        for (j = 0; j < m; j++) {
            printC (matrix [i][j]);
            colLength = colDigits [j] - strCLength (matrix [i][j]);
            /* printf ("(%d%;%d;%d)", strCLength (matrix [i][j]), colDigits [j], colLength); */
            for (c = 0; c <= colLength; c++)
                printf (" ");
        }
        printf ("\n");
    }

    free (colDigits);
}

/*
 Oblicza rzad macierzy o rozmiarach n x m.

 Parametry:
 matrix - wskaznik na macierz,
 n      - wymiar pionowy macierzy,
 m      - wymiar poziomy macierzy.

 Wartosc zwracanaL
 wartosc rzedu macierzy.
*/
int rankMC (MatrixC matrix, int n, int m) {
    ComplexP NullC;

    MatrixC Copy;

    int i,
        j,
        Rank = 0;

    NullC = makeC (0.0, 0.0);

    Copy = allocMC (n, m);
    if (Copy == NULL)
        return 0;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++) {
            Copy [i][j]->Re = matrix [i][j]->Re;
            Copy [i][j]->Im = matrix [i][j]->Im;
        }

    gaussanEliminationMC (Copy, n, m);

    /* Liczenie niezerowych wierszy w macierzy schodkowej (rzad macierzy) */
    for (i = 0; i < n; i++) {
        for (j = 0; j < m; j++) {
            /* Sprawdza czy wartosc jest rowna 0 z pominieciem bledow przyblizenia */
            if (!compC (Copy [i][j], NullC)) {
                Rank++;
                break;
            }
        }
    }


    freeMC (Copy, n, m);
    free (NullC);

    return Rank;
}

void readMC (MatrixC matrix, int n, int m) {
    int i,j;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++) {
            printf ("\n(%d;%d) = \n", i+1, j+1);
            readC (matrix [i][j]);
        }
}

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
int sortMCbyRow (MatrixC matrix, int n, int k, int from) {
    ComplexP NullC,
            *SwitchHelper;

    int Iterator,
        SomeNull,
        SomeNotNull,
        Switches = 0;

    NullC = makeC (0.0, 0.0);

    do {
        SomeNull = -1;
        SomeNotNull = -1;

        for (Iterator = from; Iterator < n && (SomeNotNull == -1 || SomeNull == -1); Iterator++) {

            if (compC (matrix [Iterator][k], NullC) && SomeNull == -1)
                SomeNull = Iterator;
            else if (!compC (matrix [Iterator][k], NullC) && SomeNotNull == -1 && SomeNull != -1)
                SomeNotNull = Iterator;
        }

        if (SomeNull != -1 && SomeNotNull != -1 && SomeNull < SomeNotNull) {
            SwitchHelper = matrix [SomeNull];
            matrix [SomeNull] = matrix [SomeNotNull];
            matrix [SomeNotNull] = SwitchHelper;
            Switches++;
        }
    } while (SomeNull != -1 && SomeNotNull != -1 && SomeNull < SomeNotNull);

    free (NullC);

    return Switches;
}

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
int solveEquationsSystemMC (MatrixC A, MatrixC B, MatrixC X, int n, int m) {
    int i,
        j,
        rankA,
        rankAB;

    MatrixC AB;

    AB = allocMC (n, m+1);
    if (AB == NULL)
        return -2;

    for (i = 0; i < n; i++)
        for (j = 0; j < m; j++) {
            AB [i][j]->Re = A [i][j]->Re;
            AB [i][j]->Im = A [i][j]->Im;
        }

    for (i = 0; i < n; i++) {
        AB [i][m]->Re = B [i][0]->Re;
        AB [i][m]->Im = B [i][0]->Im;
    }

    rankA = rankMC (A, n, m);

    rankAB = rankMC (AB, n, m+1);

    if (rankA == rankAB) {
        if (rankA == m) {
            printf ("\n\nThat system has one solution.\n");

            gaussanJordanEliminationMC (AB, n, m+1, 1);

            for (i = 0; i < m; i++)
                divC (AB [i][m], AB [i][i], X [i][0]);

            printMC (X, m, 1);

            return 1;
        } else {
            printf ("\n\nThat system has infinite amount of solutions.\n");
            return -1;
        }
    } else {
        printf ("\n\nThat system doesn't have any solutions (rank A != rank [A,B]).\n");
        return 0;
    }
}
