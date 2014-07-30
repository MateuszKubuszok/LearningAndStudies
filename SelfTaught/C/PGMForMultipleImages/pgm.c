/*
 * pgm.c - Biblioteka funkcji operuj¹cych na plikach PGM.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgm.h"



/* Wychwytuje b³êdy, jeœli zamiast wskaŸnika do zmiennej b³êdów poda siê jako argument NULLa. */
numer_bledu lapacz_bledow;
/* Tworzy makro preprocesora, inicjalizuj¹ce zmienn¹ b³êdu w danej funkcji.
   Makro sprawdza, czy zmienna blad nie jest NULLem, a jeœli jest przypisuje jej adres lapacza_bledow.
   Upewniwszy siê ¿e blad nie wskazuje na NULLa, inisjuje j¹ waartoœci¹ BRAK_BLEDU. */
#define INICJALIZUJ_ZMIENNA_BLEDU(blad) (blad=(blad!=NULL?blad:&lapacz_bledow))&&(*blad=BRAK_BLEDU)



/* Deklaracje funkcji pomocniczych. */

grafika utworz_grafike_pgm (wymiar  szerokosc, wymiar wysokosc, numer_bledu* blad);
void    usun_grafike_pgm   (grafika zawartosc, wymiar wysokosc);



/* Funkcje operujace na galeriach. */

/*
 * Tworzy now¹ galeriê obrazów.
 *
 * Parametry:
 *  - blad: wskaŸnik na zmienn¹ b³êdów.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik do nowej galerii lub NULL jeœli nie uda³o siê zaalokowaæ pamiêci.
 */
GaleriaPGM* utworz_galerie_pgm (numer_bledu* blad) {
    GaleriaPGM* galeria = (GaleriaPGM*) malloc (sizeof (GaleriaPGM));
    
    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (galeria != NULL)
        galeria->pierwszy = NULL;
    else
        *blad = BLAD_ALOKACJI_PAMIECI;

    return galeria;
}

/*
 * Wczytuje do galerii obraz.
 * 
 * Parametry:
 *  - galeria:          wskaŸnik do galerii,
 *  - sciezka_do_pliku: œcie¿ka do wczytywanego pliku,
 *  - blad:             wskaŸnik na zmienn¹ b³êdów.
 *
 * Wartoœæ zwracana:
 *  - wskaŸnik do wczytanego obrazu.
 */
ObrazPGM* wczytaj_obraz_do_galerii_pgm (GaleriaPGM* galeria, sciezka sciezka_do_obrazu, numer_bledu* blad) {
    ObrazPGM* obraz;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (galeria == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return NULL;
    }

    /* Wczytuje obraz. */
    obraz = wczytaj_obraz_pgm (sciezka_do_obrazu, blad);
    if (*blad)
        return NULL;

    /* Umieszcza obraz w galerii. */
    if (!jest_obrazem_pgm (galeria->pierwszy))
        galeria->pierwszy = obraz;
    else {
        ObrazPGM* poprzednik = galeria->pierwszy;

        /* Szuka ostatniego obrazu w galerii i umieszcza wczytany tu¿ za nim. */
        while (jest_obrazem_pgm (poprzednik->nastepny))
            poprzednik = poprzednik->nastepny;
        poprzednik->nastepny = obraz;
    }

    return obraz;
}

/*
 * Usuwa obraz z galerii i zwraca do niego wskaŸnik.
 * 
 * Parametry:
 *  - galeria: wskaŸnik do galerii,
 *  - obraz:   wskaŸnik do obrazu,
 *  - blad:    wskaŸnik na zmienn¹ b³êdów.
 *
 * Wartoœæ zwracana:
 *  - wskaŸnik do wczytanego obrazu.
 */
ObrazPGM* usun_obraz_z_galerii_pgm (GaleriaPGM* galeria, ObrazPGM* obraz, numer_bledu* blad) {
    ObrazPGM* poprzednik;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (galeria == NULL || !jest_obrazem_pgm (obraz)) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return obraz;
    }

    if (galeria->pierwszy == obraz)
        galeria->pierwszy = galeria->pierwszy->nastepny;
    else {
        for (poprzednik = galeria->pierwszy; jest_obrazem_pgm (poprzednik->nastepny); poprzednik = poprzednik->nastepny) {
            if (poprzednik->nastepny == obraz) {
                poprzednik->nastepny = poprzednik->nastepny->nastepny;
                break;
            }
        }
        if (!jest_obrazem_pgm (poprzednik))
            *blad = BLAD_NIE_ZNALEZIONO_OBRAZU;
    }

    return obraz;
}

/*
 * Usuwa galeriê i wszystkie zawarte w niej obrazy.
 * 
 * Parametry:
 *  - obraz: wskaŸnik do galerii.
 */
void usun_galerie_pgm (GaleriaPGM* galeria) {
    ObrazPGM* obraz;
    
    if (galeria == NULL)
        return;

    /* Funkcja usuwania obrazu zwraca wskaŸnik na nastêpnik. Umo¿liwia to usuniêcie wszystkich obrazów w jednej pêtli. */
    for (obraz = galeria->pierwszy; jest_obrazem_pgm (obraz); obraz = usun_obraz_pgm (obraz));
    free (galeria);
}



/* Funkcje operuj¹ce na pojedynczych obrazach. */

/*
 * Sprawdza czy wska¿nik do obrazu nie jest NULLem.
 * 
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Zwracana wartoœæ:
 *  - 1 jeœli wskaŸnik nie wskazuje na NULLa, 0 jeœli wskazuje.
 */
int jest_obrazem_pgm (ObrazPGM* obraz) {
    return !(obraz == NULL);
}

/*
 * Tworzy obraz o podanych parametrach.
 * 
 * Parametry:
 *  - szerokosc: szerokoœæ obrazu,
 *  - wysokosc:  wysokoœæ obrazu,
 *  - maxval:    maksymalna wartoœæ koloru,
 *  - blad:      wskaznik do zmienn¹ b³êdów.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik do obrazu jeœli alokowanie siê powiod³o, NULL w przeciwnym razie.
 */
ObrazPGM* utworz_obraz_pgm (wymiar szerokosc, wymiar wysokosc, kolor maxval, numer_bledu* blad) {
    ObrazPGM*   obraz = NULL;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if ((obraz = (ObrazPGM*) malloc (sizeof (ObrazPGM))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        return NULL;
    }

    obraz->sciezka_oryginalu = NULL;
    obraz->szerokosc = szerokosc;
    obraz->wysokosc = wysokosc;
    obraz->maxval = maxval;
    obraz->nastepny = NULL;
    obraz->zawartosc = utworz_grafike_pgm (szerokosc, wysokosc, blad);

    if (*blad) {
        usun_obraz_pgm (obraz);
        return NULL;
    }

    return obraz;
}

/*
 * Wczytuje obraz z pliku.
 * 
 * Parametry:
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow,
 *  - blad:              wskaznik do zmienn¹ b³êdów.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik do obrazu jeœli wczytanie siê powiod³o, NULL w przeciwnym razie.
 */
ObrazPGM* wczytaj_obraz_pgm (sciezka sciezka_do_obrazu, numer_bledu* blad) {
    FILE*     plik;
    ObrazPGM* obraz = NULL;
    char      c;
    int       pomocnik;
    wymiar    szerokosc,
              wysokosc,
              x = 0,
              y = 0;
    kolor     maxval;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    /* Otwiera plik. */
    plik = fopen (sciezka_do_obrazu, "r");
    if (plik == NULL) {
        *blad = BLAD_ODCZYTU_PLIKU;
        return NULL;
    }

    /* Sprawdza czy plik zaczyna siê od nag³ówka "P2". */
    if (fscanf (plik, "P%d\n", &pomocnik) <= 0) {
        *blad = BLAD_NIEPRAWIDLOWEGO_FORMATU;
        goto obsluga_bledu_wczytywania;
    } else if (pomocnik != 2) {
        *blad = BLAD_NIEOBSLUGIWANY_TYP_OBRAZU;
        goto obsluga_bledu_wczytywania;
    }

    /* Przewija komentarze.

       fscanf (jak i scanf) to szemrane funkcje, które w przypadku w³adowania bia³ego znaku na koniec ³añcucha formatu, bezczelnie przesuwaj¹
       wskaŸnik pozycji w pliku do pierwszego niebia³ego znaku. Tak wiêc po wczytaniu nag³ówka (fscanf (plik, "P%d\n", i tak dalej...)),
       mo¿e wyst¹piæ dowolna liczba dowolnych bia³ych znaków, a¿ do '#' - i tak pierwszym znakiem pobranym przez getc bêdzie '#' (chyba, ¿e nie ma ¿adnych komentarzy).
       
       Co ciekawe, w przypadku b³êdnie pobranego wejœcia [f]scanf nie czyœci swojego bufora i trzeba zrobiæ to rêcznie.
       W przeciwnym wypadku nastêpne wywo³anie (dla tego samgo struumienia) te¿ siê wywali. Tutaj poczas b³êdu zamykamy strumieñ,
       wiêc nie musimy siê z tym grzebaæ. */
    while ((c = getc (plik)) == '#')
        while((c = getc (plik)) != '\n' && c != EOF);
    ungetc (c, plik);

    /* Odczytuje szerokoœæ. */
    if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik <= 0) {
        *blad = BLAD_NIEPRAWIDLOWEGO_FORMATU;
        goto obsluga_bledu_wczytywania;
    }
    szerokosc = (wymiar) pomocnik;

    /* Odczytuje wysokoœæ. */
    if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik <= 0) {
        *blad = BLAD_NIEPRAWIDLOWEGO_FORMATU;
        goto obsluga_bledu_wczytywania;
    } 
    wysokosc = (wymiar) pomocnik;
        
    /* Odczytuje maxval. */
    if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik <= 0) {
        *blad = BLAD_NIEPRAWIDLOWEGO_FORMATU;
        goto obsluga_bledu_wczytywania;
    }
    maxval = (kolor) pomocnik;

    /* Tworzy obraz. */
    obraz = utworz_obraz_pgm (szerokosc, wysokosc, maxval, blad);
    if (*blad)
        goto obsluga_bledu_wczytywania;

    /* Ustawia œcie¿kê do obrazu. */
    ustaw_sciezke_obrazu_pgm (obraz, sciezka_do_obrazu, blad);
    if (*blad) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        goto obsluga_bledu_wczytywania;
    }

    /* Wczytuje dane z pliku. */
    for (y = 0; y < obraz->wysokosc; y++) {
        for (x = 0; x < obraz->szerokosc; x++) {
            if (fscanf (plik, "%d", &pomocnik) <= 0 || pomocnik < 0 || pomocnik > (int) obraz->maxval) {
                *blad = BLAD_NIEPRAWIDLOWEGO_FORMATU;
                goto obsluga_bledu_wczytywania;
            }

            obraz->zawartosc[y][x] = (kolor) pomocnik;
        }
    }

    /* Zamyka po³¹czenie z plikiem i - jeœli nast¹pi³ - obs³uguje b³¹d. */
    obsluga_bledu_wczytywania:
    fclose (plik);
    if (*blad) {
        usun_obraz_pgm (obraz);
        obraz = NULL;
    }

    return obraz;
}

/*
 * Ustawia œcie¿kê w obrazie.
 * 
 * Parametry:
 *  - obraz:             wskaŸnik do obrazu,
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow,
 *  - blad:              wskaznik do zmienn¹ b³êdów.
 */
void ustaw_sciezke_obrazu_pgm (ObrazPGM* obraz, sciezka sciezka_do_obrazu, numer_bledu* blad) {
    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (!jest_obrazem_pgm (obraz) || sciezka_do_obrazu == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return;
    }

    free (obraz->sciezka_oryginalu);
    if ((obraz->sciezka_oryginalu = (sciezka) malloc ((strlen (sciezka_do_obrazu) + 1) * sizeof (char))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        return;
    }
    strcpy (obraz->sciezka_oryginalu, sciezka_do_obrazu);
}

/*
 * Powiêksza obraz dwukrotnie.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik na zmienn¹ b³êdu.
 */
void powieksz_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad) {
    grafika nowa_zawartosc;
    wymiar  x,
            y;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (!jest_obrazem_pgm (obraz)) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return;
    }

    nowa_zawartosc = utworz_grafike_pgm (2*obraz->szerokosc, 2*obraz->wysokosc, blad);
    if (*blad)
        return;

    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++) {
            /* Tworzy ma³¹ tablicê zawieraj¹c¹ punkt [x,y] oraz punkty tu¿ na lewo/poni¿ej niego.
               Upewnia siê te¿, ¿e ww. punkty istniej¹, a jeœli nie, wybiera najbli¿ej po³o¿ony istniej¹cy punkt. */
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

            /* Oblicza wartoœæ punktów w powiêkszonym obrazie jako œredni¹:
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
}

/*
 * Odbija obraz w poziomie.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik na zmienn¹ b³êdu.
 */
void odbij_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad) {
    wymiar x,
           y,
           polowa = obraz->szerokosc/2;
    kolor  temp;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (obraz == NULL || obraz->zawartosc == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return;
    }
    
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < polowa; x++) {
            temp = obraz->zawartosc[y][x];
            obraz->zawartosc[y][x] = obraz->zawartosc[y][obraz->szerokosc-1 - x];
            obraz->zawartosc[y][obraz->szerokosc-1 - x] = temp;
        }
}

/*
 * Wykrywa krawêdzie obrazu w kierunku poziomym.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik do zmiennej b³êdu.
 */
void wykryj_krawedzie_obrazu_pgm (ObrazPGM* obraz, numer_bledu* blad) {
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

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (obraz == NULL || obraz->zawartosc == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return;
    }

    /* Alokuje pamiêæ na now¹ zawartoœæ obrazu. */
    nowa_zawartosc = utworz_grafike_pgm (obraz->szerokosc, obraz->wysokosc, blad);
    
    /* Obs³ugujê b³¹d zwi¹zany z alokacj¹ pamiêci. */
    if (*blad) {
        usun_grafike_pgm (nowa_zawartosc, obraz->wysokosc);
        return;
    }

    /* Generuje obraz zawieraj¹cy wykryte krawêdzie. */
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++) {
            int pomocnik = 0;

            /* Oblicza now¹ wartoœæ dla punktu (x,y). */
            for (dy = -1; dy <= 1; dy++)
                for (dx = -1; dx <= 1; dx++)
                    pomocnik += operator_Sobela[1+dy][1+dx] * (
                        /* Sprawdza, czy wspólrzêdne punktu (x+dx,y+dy) wskazuj¹ na istniej¹cy punkt obrazu, czy mo¿e wychodz¹ poza jego krawêdŸ. */
                        (0 <= (int)x+dx && (int)x+dx < (int)obraz->szerokosc && 0 <= (int)y+dy && (int)y+dy < (int)obraz->wysokosc) ?
                        obraz->zawartosc[y+dy][x+dx] :
                        0
                    );            

            /* Sprawdza czy otrzymana wartoœæ mieœci siê w dopuszczalnym zakresie. */
            if (pomocnik < 0)
                nowa_zawartosc[y][x] = 0;
            else if (pomocnik > (int) obraz->maxval)
                nowa_zawartosc[y][x] = obraz->maxval;
            else
                nowa_zawartosc[y][x] = (kolor) pomocnik;
        }

    /* Zastêpuje star¹ zawartoœæ przez now¹. */
    usun_grafike_pgm (obraz->zawartosc, obraz->wysokosc);
    obraz->zawartosc = nowa_zawartosc;
}

/*
 * Wyg³adza histogram obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik do zmiennej b³êdu.
 */
void wygladz_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad) {
    HistogramPGM* histogram;
    int*          dystrybuanta;
    int           d_min;
    kolor*        h;
    kolor         k;
    wymiar        x,
                  y;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    histogram = utworz_histogram_pgm (obraz, blad);
    if (*blad)
        return;

    if ((dystrybuanta = (int*) malloc ((histogram->maxval+1) * sizeof (int))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        goto obsluga_bledu_alokacji_podczas_wygladzania;
    } else if ((h = (kolor*) malloc ((histogram->maxval+1) * sizeof (kolor))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        goto obsluga_bledu_alokacji_podczas_wygladzania;
    }

    /* Oblicza dystrybuantê. */
    dystrybuanta[0] = histogram->ilosc_pikseli[0];
    d_min = dystrybuanta[0];
    for (k = 1; k <= histogram->maxval; k++) {
        dystrybuanta[k] = dystrybuanta[k-1] + histogram->ilosc_pikseli[k];
        if (dystrybuanta[k] && !d_min)
            d_min = dystrybuanta[k];
    }

    /* Oblicza now¹ wartoœæ koloru. */
    for (k = 0; k <= histogram->maxval; k++)
        h[k] = (kolor) ( ((double) (dystrybuanta[k] - d_min)) / ((double)histogram->lacznie - d_min) * ((double)histogram->maxval) );

    /* Przetwarza obraz. */
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++)
            obraz->zawartosc[y][x] = h[ obraz->zawartosc[y][x] ];

    /* Zwalnia wszystkie zaalokowane zasoby. */
    obsluga_bledu_alokacji_podczas_wygladzania:
    usun_histogram_pgm (histogram);
    free (dystrybuanta);
    free (h);
}

/*
 * Zapisuje obraz do pliku o podanej nazwie.
 *
 * Parametry:
 *  - obraz:             wskaŸnik do obrazu,
 *  - sciezka_do_zapisu: œcie¿ka do miejsca, gdzie ma zostac zapisany plik,
 *  - blad:              wskaŸnik do zmiennej b³êdu.
 */
void zapisz_obraz_pgm (ObrazPGM* obraz, sciezka sciezka_do_zapisu, numer_bledu* blad) {
    FILE*  plik;
    wymiar x,
           y;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (obraz == NULL || obraz->zawartosc == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        goto obsluga_bledu_zapisu;
    } else if ((plik = fopen (sciezka_do_zapisu, "w")) == NULL) {
        *blad = BLAD_ZAPISU_DO_PLIKU;
        goto obsluga_bledu_zapisu;
    }

    if (fprintf (plik, "P2\n") < 0 ||
        fprintf (plik, "%u %u\n", obraz->szerokosc, obraz->wysokosc) < 0 ||
        fprintf (plik, "%d\n", obraz->maxval) < 0) {
        *blad = BLAD_ZAPISU_DO_PLIKU;
    } else {
        for (y = 0; y < obraz->wysokosc; y++)
            for (x = 0; x < obraz->szerokosc; x++)
                if (fprintf (plik, "%u ", obraz->zawartosc[y][x]) < 0) {
                    *blad = BLAD_ZAPISU_DO_PLIKU;
                    goto obsluga_bledu_zapisu;
                }
    }

    obsluga_bledu_zapisu:
    fclose (plik);
}

/*
 * Zwalnia pamiêæ zajmowan¹ przez obraz i zwraca wskaŸnik do nastêpnego obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na nastêpny obraz (lub NULL jeœli obraz by³ ostatnim lub wskaŸnik do niego by³ NULLem).
 */
ObrazPGM* usun_obraz_pgm (ObrazPGM* obraz) {
    ObrazPGM* nastepny = NULL;

    if (jest_obrazem_pgm (obraz)) {
        usun_grafike_pgm (obraz->zawartosc, obraz->wysokosc);

        if (jest_obrazem_pgm (obraz->nastepny))
             nastepny = obraz->nastepny;

        free (obraz);
    }

    return nastepny;
}



/* Funkcje histogramów. */

/*
 * Zwraca histogram dla danego obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik na zmienn¹ b³êdu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na histogram obrazu.
 */
HistogramPGM* utworz_histogram_pgm (ObrazPGM* obraz, numer_bledu* blad) {
    HistogramPGM* histogram;
    wymiar        x,
                  y;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (obraz == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return NULL;
    }

    /* Alokuje pamiêæ histogramu. */
    if ((histogram = (HistogramPGM*) malloc (sizeof (HistogramPGM))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        return NULL;
    }

    /* Alokuje tablicê z wartoœciamy i wype³nia j¹ zerami. */
    if ((histogram->ilosc_pikseli = (ilosc*) calloc (obraz->maxval+1, sizeof (ilosc))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        free (histogram);
        return NULL;
    }

    histogram->maxval = obraz->maxval;
    histogram->lacznie = (ilosc) (obraz->szerokosc*obraz->wysokosc);
    /* Oblicza histogram. */
    for (y = 0; y < obraz->wysokosc; y++)
        for (x = 0; x < obraz->szerokosc; x++)
            histogram->ilosc_pikseli[ obraz->zawartosc[y][x] ]++;

    /* Najwiêksza wartoœæ w histogramie. */
    histogram->maksimum = 0;
    for (x = 0; x <= histogram->maxval; x++)
        histogram->maksimum = histogram->maksimum > histogram->ilosc_pikseli[x] ? histogram->maksimum : histogram->ilosc_pikseli[x];

    return histogram;
}

/*
 * Tworzy obraz histogramu.
 *
 * Parametry:
 *  - histogram: wskaŸnik do histogramu,
 *  - blad:      wskaŸnik na zmienn¹ b³êdu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na obrazu histogramu.
 */
ObrazPGM* utworz_obraz_histogramu_pgm (HistogramPGM* histogram, numer_bledu* blad) {
    ObrazPGM* obraz;
    wymiar    x,
              y,
              szerokosc = histogram->maxval+1,
              wysokosc = (histogram->maxval+1)*2,
              wysokosc_slupka;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (histogram == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return NULL;
    }

    obraz = utworz_obraz_pgm (szerokosc, wysokosc, 1, blad);
    if (*blad)
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
 *  - histogram:             wskaŸnik do histogramu,
 *  - sciezka_do_histogramu: œcie¿ka do pliku, gdzie ma zostaæ zapisany histogram,
 *  - blad:                  wskaŸnik na zmienn¹ b³êdu.
 */
void zapisz_histogram_pgm (HistogramPGM* histogram, sciezka sciezka_do_histogramu, numer_bledu* blad) {
    FILE* plik;
    kolor k;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    if (histogram == NULL || histogram->ilosc_pikseli == NULL) {
        *blad = BLAD_WSKAZNIKA_DO_NULLA;
        return;
    } else if ((plik = fopen (sciezka_do_histogramu, "w")) == NULL) {
        *blad = BLAD_ZAPISU_DO_PLIKU;
        return;
    }

    /* Nag³ówek. */
    if (fprintf (plik, "ColorValue,Occurances") < 0) {
        *blad = BLAD_ZAPISU_DO_PLIKU;
        goto obsluga_bledu_zapisu_histogramu;
    }
    /* Wartoœci. */
    for (k = 0; k <= histogram->maxval; k++)
        if (fprintf (plik, "\n%u,%u", k, histogram->ilosc_pikseli[k]) < 0) {
            *blad = BLAD_ZAPISU_DO_PLIKU;
            goto obsluga_bledu_zapisu_histogramu;
        }

    obsluga_bledu_zapisu_histogramu:
    fclose (plik);
}

/*
 * Usuwa histogram z pamiêci.
 *
 * Parametry:
 *  - histogram: wskaŸnik do histogramu.
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
 * Alokuje pamiêæ na grafikê.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na grafikê.
 */
grafika utworz_grafike_pgm (wymiar szerokosc, wymiar wysokosc, numer_bledu* blad) {
    grafika zawartosc;
    wymiar  y;

    INICJALIZUJ_ZMIENNA_BLEDU (blad);

    /* Alokuje tablicê wierszy. */
    if ((zawartosc = (grafika) calloc (wysokosc, sizeof (wiersz))) == NULL) {
        *blad = BLAD_ALOKACJI_PAMIECI;
        goto obsluga_bledu_alokacji_grafiki;
    }
    /* Alokuje poszczególne wiersze. */        
    for (y = 0; y < wysokosc; y++) {
        zawartosc[y] = (wiersz) calloc (szerokosc, sizeof (kolor));

        if (zawartosc[y] == NULL) {
            *blad = BLAD_ALOKACJI_PAMIECI;
            goto obsluga_bledu_alokacji_grafiki;
        }
    }

    /* Obs³ugujê b³¹d zwi¹zany z alokacj¹ pamiêci. */
    obsluga_bledu_alokacji_grafiki:
    if (*blad) {
        usun_grafike_pgm (zawartosc, wysokosc);
        return NULL;
    }

    return zawartosc;
}

/*
 * Zwalnia pamiêæ zajmowan¹ przez grafikê.
 *
 * Parametry:
 *  - zawartoœæ: wskaŸnik do grafiki,
 *  - wysokosc:  wysokosc grafiki (rozmiar tablicy wierszy).
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na nastêpny obraz (lub NULL jeœli obraz by³ ostatnim lub wskaŸnik do niego by³ NULLem).
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