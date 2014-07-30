/*
 * pgm.c - Biblioteka funkcji operuj�cych na plikach PGM.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgm.h"



/* Deklaracje funkcji pomocniczych. */

grafika utworz_grafike_pgm (wymiar  szerokosc, wymiar wysokosc);
void    usun_grafike_pgm   (grafika zawartosc, wymiar wysokosc);



/* Funkcje operuj�ce na pojedynczych obrazach. */

/*
 * Tworzy obraz o podanych parametrach.
 * 
 * Parametry:
 *  - szerokosc: szeroko�� obrazu,
 *  - wysokosc:  wysoko�� obrazu,
 *  - maxval:    maksymalna warto�� koloru.
 *
 * Zwracana warto��:
 *  - wska�nik do obrazu je�li alokowanie si� powiod�o, NULL w przeciwnym razie.
 */
ObrazPGM* utworz_obraz_pgm (wymiar szerokosc, wymiar wysokosc, kolor maxval) {
    ObrazPGM*   obraz = NULL;

    if ((obraz = (ObrazPGM*) malloc (sizeof (ObrazPGM))) == NULL)
        return NULL;

    obraz->sciezka_oryginalu = NULL;
    obraz->szerokosc = szerokosc;
    obraz->wysokosc = wysokosc;
    obraz->maxval = maxval;
    obraz->nastepny = NULL;

    if ((obraz->zawartosc = utworz_grafike_pgm (szerokosc, wysokosc)) == NULL) {
        usun_obraz_pgm (obraz);
        return NULL;
    }

    return obraz;
}

/*
 * Wczytuje obraz z pliku.
 * 
 * Parametry:
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow.
 *
 * Zwracana warto��:
 *  - wska�nik do obrazu je�li wczytanie si� powiod�o, NULL w przeciwnym razie.
 */
ObrazPGM* wczytaj_obraz_pgm (sciezka sciezka_do_obrazu) {
    FILE*     plik;
    ObrazPGM* obraz = NULL;
    char      c;
    int       pomocnik;
    wymiar    szerokosc,
              wysokosc,
              x = 0,
              y = 0;
    kolor     maxval;

    /* Otwiera plik. */
    if ((plik = fopen (sciezka_do_obrazu, "r")) == NULL)
        return NULL;

    /* Sprawdza czy plik zaczyna si� od nag��wka "P2". */
    if (fscanf (plik, "P%d\n", &pomocnik) <= 0 || pomocnik != 2) {
        fclose (plik);
        usun_obraz_pgm (obraz);
        obraz = NULL;
    }

    /* Przewija komentarze.

       fscanf (jak i scanf) to szemrane funkcje, kt�re w przypadku w�adowania bia�ego znaku na koniec �a�cucha formatu, bezczelnie przesuwaj�
       wska�nik pozycji w pliku do pierwszego niebia�ego znaku. Tak wi�c po wczytaniu nag��wka (fscanf (plik, "P%d\n", i tak dalej...)),
       mo�e wyst�pi� dowolna liczba dowolnych bia�ych znak�w, a� do '#' - i tak pierwszym znakiem pobranym przez getc b�dzie '#' (chyba, �e nie ma �adnych komentarzy).
       
       Co ciekawe, w przypadku b��dnie pobranego wej�cia [f]scanf nie czy�ci swojego bufora i trzeba zrobi� to r�cznie.
       W przeciwnym wypadku nast�pne wywo�anie (dla tego samgo struumienia) te� si� wywali. Tutaj poczas b��du zamykamy strumie�,
       wi�c nie musimy si� z tym grzeba�. */
    while ((c = getc (plik)) == '#')
        while((c = getc (plik)) != '\n' && c != EOF);
    ungetc (c, plik);

    /* Odczytuje szeroko��. */
    if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik <= 0) {
        fclose (plik);
        usun_obraz_pgm (obraz);
        return NULL;
    }
    szerokosc = (wymiar) pomocnik;

    /* Odczytuje wysoko��. */
    if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik <= 0) {
        fclose (plik);
        usun_obraz_pgm (obraz);
        return NULL;
    }
    wysokosc = (wymiar) pomocnik;
        
    /* Odczytuje maxval. */
    if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik <= 0) {
        fclose (plik);
        usun_obraz_pgm (obraz);
        return NULL;
    }
    maxval = (kolor) pomocnik;

    /* Tworzy obraz. */
    if ((obraz = utworz_obraz_pgm (szerokosc, wysokosc, maxval)) == NULL) {
        fclose (plik);
        usun_obraz_pgm (obraz);
        return NULL;
    }

    /* Ustawia �cie�k� do obrazu. */
    if (!ustaw_sciezke_obrazu_pgm (obraz, sciezka_do_obrazu)) {
        fclose (plik);
        usun_obraz_pgm (obraz);
        return NULL;
    }

    /* Wczytuje dane z pliku. */
    for (y = 0; y < obraz->wysokosc; y++) {
        for (x = 0; x < obraz->szerokosc; x++) {
            if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik < 0 || pomocnik > (int) obraz->maxval) {
                fclose (plik);
                usun_obraz_pgm (obraz);
                return NULL;
            }

            obraz->zawartosc[y][x] = (kolor) pomocnik;
        }
    }

    /* Zamyka po��czenie z plikiem. */
    fclose (plik);

    return obraz;
}

/*
 * Ustawia �cie�k� w obrazie.
 * 
 * Parametry:
 *  - obraz:             wska�nik do obrazu,
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int ustaw_sciezke_obrazu_pgm (ObrazPGM* obraz, sciezka sciezka_do_obrazu) {
    if (obraz == NULL || sciezka_do_obrazu == NULL)
        return 0;

    free (obraz->sciezka_oryginalu);
    obraz->sciezka_oryginalu = NULL;
    if ((obraz->sciezka_oryginalu = (sciezka) malloc ((strlen (sciezka_do_obrazu) + 1) * sizeof (char))) == NULL)
        return 0;
    strcpy (obraz->sciezka_oryginalu, sciezka_do_obrazu);

    return 1;
}

/*
 * Powi�ksza obraz dwukrotnie.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int powieksz_obraz_pgm (ObrazPGM* obraz) {
    grafika nowa_zawartosc;
    wymiar  x,
            y;

    if (obraz == NULL)
        return 0;

    if ((nowa_zawartosc = utworz_grafike_pgm (2*obraz->szerokosc, 2*obraz->wysokosc)) == NULL)
        return 0;

    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++) {
            /* Tworzy ma�� tablic� zawieraj�c� punkt [x,y] oraz punkty tu� na lewo/poni�ej niego.
               Upewnia si� te�, �e ww. punkty istniej�, a je�li nie, wybiera najbli�ej po�o�ony istniej�cy punkt. */
            kolor sasiedzi[2][2] = {
                {
                    obraz->zawartosc[y][x],
                    x+1 < obraz->szerokosc ? obraz->zawartosc[y][x+1] : obraz->zawartosc[y][x]
                },
                {
                    y+1 < obraz->wysokosc ? obraz->zawartosc[y+1][x] : obraz->zawartosc[y][x],
                    x+1 < obraz->szerokosc && y+1 < obraz->wysokosc ? obraz->zawartosc[y+1][x+1] :
                        x+1 < obraz->szerokosc ? obraz->zawartosc[y][x+1] :
                        y+1 < obraz->wysokosc ? obraz->zawartosc[y+1][x] :
                            obraz->zawartosc[y][x]
                }
            };
            int i,
                j;

            /* Oblicza warto�� punkt�w w powi�kszonym obrazie jako �redni�:
               [x,y]                 ([x,y] + [x+1,y])/2     [x+1,y]
               ([x,y] + [x,y+1])/2   ([x,y] + [x+1,y+1])/2   ...
               [x,y+1]               ...                     [x+1,y+1]
             */
            for (i = 0; i < 2; i++)
                for (j = 0; j < 2; j++)
                    nowa_zawartosc[2*y+i][2*x+j] = (sasiedzi[0][0] + sasiedzi[i][j]) / 2;
        }

    usun_grafike_pgm (obraz->zawartosc, obraz->wysokosc);

    obraz->szerokosc *= 2;
    obraz->wysokosc *= 2;
    obraz->zawartosc = nowa_zawartosc;

    return 1;
}

/*
 * Odbija obraz w poziomie.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int odbij_obraz_pgm (ObrazPGM* obraz) {
    wymiar x,
           y,
           polowa = obraz->szerokosc/2;
    kolor  temp;

    if (obraz == NULL || obraz->zawartosc == NULL)
        return 0;
    
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < polowa; x++) {
            temp = obraz->zawartosc[y][x];
            obraz->zawartosc[y][x] = obraz->zawartosc[y][obraz->szerokosc-1 - x];
            obraz->zawartosc[y][obraz->szerokosc-1 - x] = temp;
        }

    return 1;
}

/*
 * Wykrywa kraw�dzie obrazu w kierunku poziomym.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int wykryj_krawedzie_obrazu_pgm (ObrazPGM* obraz) {
    int operator_Sobela[3][3] = {
        { 1,  2,  1},
        { 0,  0,  0},
        {-1, -2, -1}
    };
    wymiar  x,
            y;
    int     dx,
            dy;
    grafika nowa_zawartosc;

    if (obraz == NULL || obraz->zawartosc == NULL)
        return 0;

    /* Alokuje pami�� na now� zawarto�� obrazu. */
    if ((nowa_zawartosc = utworz_grafike_pgm (obraz->szerokosc, obraz->wysokosc)) == NULL) {
        usun_grafike_pgm (nowa_zawartosc, obraz->wysokosc);
        return 0;
    }

    /* Generuje obraz zawieraj�cy wykryte kraw�dzie. */
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++) {
            int pomocnik = 0;

            /* Oblicza now� warto�� dla punktu (x,y). */
            for (dy = -1; dy <= 1; dy++)
                for (dx = -1; dx <= 1; dx++)
                    pomocnik += operator_Sobela[1+dy][1+dx] * (
                        /* Sprawdza, czy wsp�lrz�dne punktu (x+dx,y+dy) wskazuj� na istniej�cy punkt obrazu, czy mo�e wychodz� poza jego kraw�d�. */
                        (0 <= (int)x+dx && (int)x+dx < (int)obraz->szerokosc && 0 <= (int)y+dy && (int)y+dy < (int)obraz->wysokosc) ?
                        obraz->zawartosc[y+dy][x+dx] :
                        0
                    );            

            /* Sprawdza czy otrzymana warto�� mie�ci si� w dopuszczalnym zakresie. */
            if (pomocnik < 0)
                nowa_zawartosc[y][x] = 0;
            else if (pomocnik > (int) obraz->maxval)
                nowa_zawartosc[y][x] = obraz->maxval;
            else
                nowa_zawartosc[y][x] = (kolor) pomocnik;
        }

    /* Zast�puje star� zawarto�� przez now�. */
    usun_grafike_pgm (obraz->zawartosc, obraz->wysokosc);
    obraz->zawartosc = nowa_zawartosc;

    return 1;
}

/*
 * Wyg�adza histogram obrazu.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int wygladz_obraz_pgm (ObrazPGM* obraz) {
    HistogramPGM* histogram;
    int*          dystrybuanta;
    int           d_min;
    kolor*        h;
    kolor         k;
    wymiar        x,
                  y;

    if ((histogram = utworz_histogram_pgm (obraz)) == NULL)
        return 0;

    if ((dystrybuanta = (int*) malloc ((histogram->maxval+1) * sizeof (int))) == NULL ||
        (h = (kolor*) malloc ((histogram->maxval+1) * sizeof (kolor))) == NULL) {
        free (dystrybuanta);
        free (h);
        return 0;
    }

    /* Oblicza dystrybuant�. */
    dystrybuanta[0] = histogram->ilosc_pikseli[0];
    d_min = dystrybuanta[0];
    for (k = 1; k <= histogram->maxval; k++) {
        dystrybuanta[k] = dystrybuanta[k-1] + histogram->ilosc_pikseli[k];
        if (dystrybuanta[k] && !d_min)
            d_min = dystrybuanta[k];
    }

    /* Oblicza now� warto�� koloru. */
    for (k = 0; k <= histogram->maxval; k++)
        h[k] = (kolor) ( ((double) (dystrybuanta[k] - d_min)) / ((double)histogram->lacznie - d_min) * ((double)histogram->maxval) );

    /* Przetwarza obraz. */
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++)
            obraz->zawartosc[y][x] = h[ obraz->zawartosc[y][x] ];

    /* Zwalnia wszystkie zaalokowane zasoby. */
    usun_histogram_pgm (histogram);
    free (dystrybuanta);
    free (h);

    return 1;
}

/*
 * Zapisuje obraz do pliku o podanej nazwie.
 *
 * Parametry:
 *  - obraz:             wska�nik do obrazu,
 *  - sciezka_do_zapisu: �cie�ka do miejsca, gdzie ma zostac zapisany plik.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int zapisz_obraz_pgm (ObrazPGM* obraz, sciezka sciezka_do_zapisu) {
    FILE*  plik;
    wymiar x,
           y;

    if (obraz == NULL || obraz->zawartosc == NULL || (plik = fopen (sciezka_do_zapisu, "w")) == NULL)
        return 0;

    if (fprintf (plik, "P2\n") < 0 ||
        fprintf (plik, "%u %u\n", obraz->szerokosc, obraz->wysokosc) < 0 ||
        fprintf (plik, "%d\n", obraz->maxval) < 0) {
        fclose (plik);
        return 0;
    } else {
        for (y = 0; y < obraz->wysokosc; y++)
            for (x = 0; x < obraz->szerokosc; x++)
                if (fprintf (plik, "%u ", obraz->zawartosc[y][x]) < 0) {
                    fclose (plik);
                    return 0;
                }
    }

    fclose (plik);
    return 1;
}

/*
 * Zwalnia pami�� zajmowan� przez obraz i zwraca wska�nik do nast�pnego obrazu.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Zwracana warto��:
 *  - wska�nik na nast�pny obraz (lub NULL je�li obraz by� ostatnim lub wska�nik do niego by� NULLem).
 */
ObrazPGM* usun_obraz_pgm (ObrazPGM* obraz) {
    ObrazPGM* nastepny = NULL;

    if (obraz != NULL) {
        usun_grafike_pgm (obraz->zawartosc, obraz->wysokosc);

        if (obraz->nastepny != NULL)
             nastepny = obraz->nastepny;

        free (obraz);
    }

    return nastepny;
}



/* Funkcje histogram�w. */

/*
 * Zwraca histogram dla danego obrazu.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Zwracana warto��:
 *  - wska�nik na histogram obrazu.
 */
HistogramPGM* utworz_histogram_pgm (ObrazPGM* obraz) {
    HistogramPGM* histogram;
    wymiar        x,
                  y;

    if (obraz == NULL || (histogram = (HistogramPGM*) malloc (sizeof (HistogramPGM))) == NULL)
        return NULL;

    /* Alokuje tablic� z warto�ciamy i wype�nia j� zerami. */
    if ((histogram->ilosc_pikseli = (ilosc*) calloc (obraz->maxval+1, sizeof (ilosc))) == NULL) {
        free (histogram);
        return NULL;
    }

    histogram->maxval = obraz->maxval;
    histogram->lacznie = (ilosc) (obraz->szerokosc*obraz->wysokosc);
    /* Oblicza histogram. */
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++)
            histogram->ilosc_pikseli[ obraz->zawartosc[y][x] ]++;

    /* Najwi�ksza warto�� w histogramie. */
    histogram->maksimum = 0;
    for (x = 0; x <= histogram->maxval; x++)
        histogram->maksimum = histogram->maksimum > histogram->ilosc_pikseli[x] ? histogram->maksimum : histogram->ilosc_pikseli[x];

    return histogram;
}

/*
 * Tworzy obraz histogramu.
 *
 * Parametry:
 *  - histogram: wska�nik do histogramu,
 *  - blad:      wska�nik na zmienn� b��du.
 *
 * Zwracana warto��:
 *  - wska�nik na obrazu histogramu.
 */
ObrazPGM* utworz_obraz_histogramu_pgm (HistogramPGM* histogram) {
    ObrazPGM* obraz;
    wymiar    x,
              y,
              szerokosc = histogram->maxval+1,
              wysokosc = (histogram->maxval+1)*2,
              wysokosc_slupka;

    if (histogram == NULL)
        return NULL;

    if ((obraz = utworz_obraz_pgm (szerokosc, wysokosc, 1)) == NULL)
        return NULL;

    for (x = 0; x < szerokosc; x++) {
        wysokosc_slupka = (wymiar) (((double) histogram->ilosc_pikseli[x]) / ((double) histogram->maksimum) * ((double) wysokosc));
        for (y = wysokosc - wysokosc_slupka; y < wysokosc; y++)
            obraz->zawartosc[y][x] = 1;
    }

    return obraz;
}

/*
 * Zapisuje histogram do pliku CSV.
 *
 * Parametry:
 *  - histogram:             wska�nik do histogramu,
 *  - sciezka_do_histogramu: �cie�ka do pliku, gdzie ma zosta� zapisany histogram.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int zapisz_histogram_pgm (HistogramPGM* histogram, sciezka sciezka_do_histogramu) {
    FILE* plik;
    kolor k;

    if (histogram == NULL || histogram->ilosc_pikseli == NULL || (plik = fopen (sciezka_do_histogramu, "w")) == NULL)
        return 0;

    /* Nag��wek. */
    if (fprintf (plik, "ColorValue,Occurances") < 0) {
        fclose (plik);
        return 0;
    }
    /* Warto�ci. */
    for (k = 0; k <= histogram->maxval; k++)
        if (fprintf (plik, "\n%u,%u", k, histogram->ilosc_pikseli[k]) < 0) {
            fclose (plik);
            return 0;
        }

    fclose (plik);
    return 1;
}

/*
 * Usuwa histogram z pami�ci.
 *
 * Parametry:
 *  - histogram: wska�nik do histogramu.
 */
void usun_histogram_pgm (HistogramPGM* histogram) {
    if (histogram != NULL) {
        if (histogram->ilosc_pikseli != NULL)
            free (histogram->ilosc_pikseli);
        free (histogram);
    }
}



/* Funkcje pomocnicze. */

/*
 * Alokuje pami�� na grafik�.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Zwracana warto��:
 *  - wska�nik na grafik�.
 */
grafika utworz_grafike_pgm (wymiar szerokosc, wymiar wysokosc) {
    grafika zawartosc;
    wymiar  y;

    /* Alokuje tablic� wierszy. */
    if ((zawartosc = (grafika) calloc (wysokosc, sizeof (wiersz))) == NULL)
        return NULL;

    /* Alokuje poszczeg�lne wiersze. */        
    for (y = 0; y < wysokosc; y++) {
        zawartosc[y] = (wiersz) calloc (szerokosc, sizeof (kolor));

        if (zawartosc[y] == NULL) {
            usun_grafike_pgm (zawartosc, wysokosc);
            return NULL;
        }
    }

    return zawartosc;
}

/*
 * Zwalnia pami�� zajmowan� przez grafik�.
 *
 * Parametry:
 *  - zawarto��: wska�nik do grafiki,
 *  - wysokosc:  wysokosc grafiki (rozmiar tablicy wierszy).
 *
 * Zwracana warto��:
 *  - wska�nik na nast�pny obraz (lub NULL je�li obraz by� ostatnim lub wska�nik do niego by� NULLem).
 */
void usun_grafike_pgm (grafika zawartosc, wymiar wysokosc) {
    if (zawartosc != NULL) {
        wymiar y;

        for (y = 0; y < wysokosc; y++)
            if (zawartosc[y] != NULL)
                free (zawartosc[y]);
        free (zawartosc);
    }
}