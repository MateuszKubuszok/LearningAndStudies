/*
 * pgm.h - Nag��wek biblioteki PGM.
 */
#ifndef PGM_LIBRARY
#define PGM_LIBRARY

typedef unsigned int ilosc;       /* Przechowuje ilo�� obraz�w w galerii oraz ilo�� pikselu dla danego koloru w histogramie. */
typedef unsigned int wymiar;      /* Przechowuje wymiar obrazu PGM (wymiar > 0). */
typedef unsigned int kolor;       /* Przechowuje kolor danego piksela (0 <= kolor <= Obraz.maxval). */
typedef kolor*       wiersz;      /* Przechowuje 1 wiersz pikseli. */
typedef wiersz*      grafika;     /* Przechowuje 1 kopi� obrazu. */
typedef char*        sciezka;     /* Przechowuje �cie�k� do pliku (musi si� ko�czy� znakiem \0). */

/* Definiuje struktur� Obrazu. */
typedef struct _obraz_pgm_ {
    sciezka            sciezka_oryginalu; /* �cie�ka do oryginalnego pliku. */
    wymiar             wysokosc;          /* Wysoko�� obrazu. */
    wymiar             szerokosc;         /* Szeroko�� obrazu. */
    grafika            zawartosc;         /* Dwuwymiarowa tablica przechowuj�ca wszystkie piksele w formacie zawarto��[numer_wiersza][numer_kolumny]. */
    kolor              maxval;            /* Okresla zakres kolor�w - warto�ci kolor�w sk� skalowane do skali: 0 - absolutna czer�, maxval - absolutna biel. */
    struct _obraz_pgm_ *nastepny;         /* Wska�nik na nast�pny obraz w galerii. */
} ObrazPGM;

/* Definiuje struktur� Histogramu. */
typedef struct {
    ilosc lacznie;        /* ��czna ilo�� pikseli w obrazie. */
    ilosc maksimum;       /* Najwi�ksza ilo�� pikseli danego koloru (warto�� maksimum histogramu). */
    ilosc *ilosc_pikseli; /* Tablica przechowuj�ca ilo�� pikseli dal danego koloru: ilosc_pikseli[kolor] = ilosc. */
    kolor maxval;         /* Najwi�ksza mo�liwa warto�� koloru (rozmiar tablicy to maxval+1, indeksy: od 0 do maxval w��cznie). */
} HistogramPGM;



/* Funkcje pojedynczych obraz�w PGM. */

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
ObrazPGM* utworz_obraz_pgm (wymiar szerokosc, wymiar wysokosc, kolor maxval);

/*
 * Wczytuje obraz z pliku.
 * 
 * Parametry:
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow.
 *
 * Zwracana warto��:
 *  - wska�nik do obrazu je�li wczytanie si� powiod�o, NULL w przeciwnym razie.
 */
ObrazPGM* wczytaj_obraz_pgm (sciezka sciezka_do_obrazu);

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
int ustaw_sciezke_obrazu_pgm (ObrazPGM* obraz, sciezka sciezka_do_obrazu);

/*
 * Powi�ksza obraz dwukrotnie.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int powieksz_obraz_pgm (ObrazPGM* obraz);

/*
 * Odbija obraz w poziomie.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 */
int odbij_obraz_pgm (ObrazPGM* obraz);

/*
 * Wykrywa kraw�dzie obrazu w kierunku poziomym.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int wykryj_krawedzie_obrazu_pgm (ObrazPGM* obraz);

/*
 * Wyg�adza histogram obrazu.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Warto�� zwracana:
 *  - 1 je�li sukces, 0 je�li pora�ka.
 */
int wygladz_obraz_pgm (ObrazPGM* obraz);

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
int zapisz_obraz_pgm (ObrazPGM* obraz, sciezka sciezka_do_brazu);

/*
 * Zwalnia pami�� zajmowan� przez obraz i zwraca wska�nik do nast�pnego obrazu.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Zwracana warto��:
 *  - wska�nik na nast�pny obraz (lub NULL je�li obraz by� ostatnim lub wska�nik do niego by� NULLem).
 */
ObrazPGM* usun_obraz_pgm (ObrazPGM* obraz);



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
HistogramPGM* utworz_histogram_pgm (ObrazPGM* obraz);

/*
 * Tworzy obraz histogramu.
 *
 * Parametry:
 *  - histogram: wska�nik do histogramu.
 *
 * Zwracantna warto��:
 *  - wska�nik na obrazu histogramu.
 */
ObrazPGM* utworz_obraz_histogramu_pgm (HistogramPGM* histogram);

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
int zapisz_histogram_pgm (HistogramPGM* histogram, sciezka sciezka_do_histogramu);

/*
 * Usuwa histogram z pami�ci.
 *
 * Parametry:
 *  - histogram: wska�nik do histogramu.
 */
void usun_histogram_pgm (HistogramPGM* histogram);

#endif /* #ifndef PGM_LIB */