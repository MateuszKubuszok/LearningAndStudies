/*
 * pgm.h - Nag³ówek biblioteki PGM.
 */
#ifndef PGM_LIBRARY
#define PGM_LIBRARY

typedef unsigned int ilosc;       /* Przechowuje iloœæ obrazów w galerii oraz iloœæ pikselu dla danego koloru w histogramie. */
typedef unsigned int wymiar;      /* Przechowuje wymiar obrazu PGM (wymiar > 0). */
typedef unsigned int kolor;       /* Przechowuje kolor danego piksela (0 <= kolor <= Obraz.maxval). */
typedef kolor*       wiersz;      /* Przechowuje 1 wiersz pikseli. */
typedef wiersz*      grafika;     /* Przechowuje 1 kopiê obrazu. */
typedef char*        sciezka;     /* Przechowuje œcie¿kê do pliku (musi siê koñczyæ znakiem \0). */

/* Definiuje strukturê Obrazu. */
typedef struct _obraz_pgm_ {
    sciezka            sciezka_oryginalu; /* Œcie¿ka do oryginalnego pliku. */
    wymiar             wysokosc;          /* Wysokoœæ obrazu. */
    wymiar             szerokosc;         /* Szerokoœæ obrazu. */
    grafika            zawartosc;         /* Dwuwymiarowa tablica przechowuj¹ca wszystkie piksele w formacie zawartoœæ[numer_wiersza][numer_kolumny]. */
    kolor              maxval;            /* Okresla zakres kolorów - wartoœci kolorów sk¹ skalowane do skali: 0 - absolutna czerñ, maxval - absolutna biel. */
    struct _obraz_pgm_ *nastepny;         /* WskaŸnik na nastêpny obraz w galerii. */
} ObrazPGM;

/* Definiuje strukturê Histogramu. */
typedef struct {
    ilosc lacznie;        /* £¹czna iloœæ pikseli w obrazie. */
    ilosc maksimum;       /* Najwiêksza iloœæ pikseli danego koloru (wartoœæ maksimum histogramu). */
    ilosc *ilosc_pikseli; /* Tablica przechowuj¹ca iloœæ pikseli dal danego koloru: ilosc_pikseli[kolor] = ilosc. */
    kolor maxval;         /* Najwiêksza mo¿liwa wartoœæ koloru (rozmiar tablicy to maxval+1, indeksy: od 0 do maxval w³¹cznie). */
} HistogramPGM;



/* Funkcje pojedynczych obrazów PGM. */

/*
 * Tworzy obraz o podanych parametrach.
 * 
 * Parametry:
 *  - szerokosc: szerokoœæ obrazu,
 *  - wysokosc:  wysokoœæ obrazu,
 *  - maxval:    maksymalna wartoœæ koloru.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik do obrazu jeœli alokowanie siê powiod³o, NULL w przeciwnym razie.
 */
ObrazPGM* utworz_obraz_pgm (wymiar szerokosc, wymiar wysokosc, kolor maxval);

/*
 * Wczytuje obraz z pliku.
 * 
 * Parametry:
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik do obrazu jeœli wczytanie siê powiod³o, NULL w przeciwnym razie.
 */
ObrazPGM* wczytaj_obraz_pgm (sciezka sciezka_do_obrazu);

/*
 * Ustawia œcie¿kê w obrazie.
 * 
 * Parametry:
 *  - obraz:             wskaŸnik do obrazu,
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow.
 *
 * Wartoœæ zwracana:
 *  - 1 jeœli sukces, 0 jeœli pora¿ka.
 */
int ustaw_sciezke_obrazu_pgm (ObrazPGM* obraz, sciezka sciezka_do_obrazu);

/*
 * Powiêksza obraz dwukrotnie.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Wartoœæ zwracana:
 *  - 1 jeœli sukces, 0 jeœli pora¿ka.
 */
int powieksz_obraz_pgm (ObrazPGM* obraz);

/*
 * Odbija obraz w poziomie.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 */
int odbij_obraz_pgm (ObrazPGM* obraz);

/*
 * Wykrywa krawêdzie obrazu w kierunku poziomym.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Wartoœæ zwracana:
 *  - 1 jeœli sukces, 0 jeœli pora¿ka.
 */
int wykryj_krawedzie_obrazu_pgm (ObrazPGM* obraz);

/*
 * Wyg³adza histogram obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Wartoœæ zwracana:
 *  - 1 jeœli sukces, 0 jeœli pora¿ka.
 */
int wygladz_obraz_pgm (ObrazPGM* obraz);

/*
 * Zapisuje obraz do pliku o podanej nazwie.
 *
 * Parametry:
 *  - obraz:             wskaŸnik do obrazu,
 *  - sciezka_do_zapisu: œcie¿ka do miejsca, gdzie ma zostac zapisany plik.
 *
 * Wartoœæ zwracana:
 *  - 1 jeœli sukces, 0 jeœli pora¿ka.
 */
int zapisz_obraz_pgm (ObrazPGM* obraz, sciezka sciezka_do_brazu);

/*
 * Zwalnia pamiêæ zajmowan¹ przez obraz i zwraca wskaŸnik do nastêpnego obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na nastêpny obraz (lub NULL jeœli obraz by³ ostatnim lub wskaŸnik do niego by³ NULLem).
 */
ObrazPGM* usun_obraz_pgm (ObrazPGM* obraz);



/* Funkcje histogramów. */

/*
 * Zwraca histogram dla danego obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na histogram obrazu.
 */
HistogramPGM* utworz_histogram_pgm (ObrazPGM* obraz);

/*
 * Tworzy obraz histogramu.
 *
 * Parametry:
 *  - histogram: wskaŸnik do histogramu.
 *
 * Zwracantna wartoœæ:
 *  - wskaŸnik na obrazu histogramu.
 */
ObrazPGM* utworz_obraz_histogramu_pgm (HistogramPGM* histogram);

/*
 * Zapisuje histogram do pliku CSV.
 *
 * Parametry:
 *  - histogram:             wskaŸnik do histogramu,
 *  - sciezka_do_histogramu: œcie¿ka do pliku, gdzie ma zostaæ zapisany histogram.
 *
 * Wartoœæ zwracana:
 *  - 1 jeœli sukces, 0 jeœli pora¿ka.
 */
int zapisz_histogram_pgm (HistogramPGM* histogram, sciezka sciezka_do_histogramu);

/*
 * Usuwa histogram z pamiêci.
 *
 * Parametry:
 *  - histogram: wskaŸnik do histogramu.
 */
void usun_histogram_pgm (HistogramPGM* histogram);

#endif /* #ifndef PGM_LIB */