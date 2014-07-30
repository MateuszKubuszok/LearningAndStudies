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
typedef int          numer_bledu; /* Przechowuje numer b��du (b��d = 0 je�li �aden b��d nie wyst�pi�, b��d != 0 je�li jaki� nast�pi�). */
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

/* Definiuje struktur� Galerii obraz�w. */
typedef struct {
    ObrazPGM *pierwszy; /* Wska�nik na pierwszy obraz w Galerii (NULL je�li �adnego jeszcze nie dodano). */
} GaleriaPGM;

/* Definiuje struktur� Histogramu. */
typedef struct {
    ilosc lacznie;        /* ��czna ilo�� pikseli w obrazie. */
    ilosc maksimum;       /* Najwi�ksza ilo�� pikseli danego koloru (warto�� maksimum histogramu). */
    ilosc *ilosc_pikseli; /* Tablica przechowuj�ca ilo�� pikseli dal danego koloru: ilosc_pikseli[kolor] = ilosc. */
    kolor maxval;         /* Najwi�ksza mo�liwa warto�� koloru (rozmiar tablicy to maxval+1, indeksy: od 0 do maxval w��cznie). */
} HistogramPGM;



/* Sta�e okre�laj�ce b��dy wyst�puj�ce przy przetwarzaniu obraz�w PGM. */
#define BLAD_NIE_ZNALEZIONO_OBRAZU     (numer_bledu)3
#define BLAD_ZAPISU_DO_PLIKU           (numer_bledu)2
#define BLAD_ODCZYTU_PLIKU             (numer_bledu)1
#define BRAK_BLEDU                     (numer_bledu)0
#define BLAD_NIEOBSLUGIWANY_TYP_OBRAZU (numer_bledu)-1
#define BLAD_NIEPRAWIDLOWEGO_FORMATU   (numer_bledu)-2
#define BLAD_ALOKACJI_PAMIECI          (numer_bledu)-3
#define BLAD_WSKAZNIKA_DO_NULLA        (numer_bledu)-4



/* Funkcje Galerii PGM. */

/*
 * Tworzy now� galeri� obraz�w.
 *
 * Parametry:
 *  - blad: wska�nik na zmienn� b��d�w.
 *
 * Zwracana warto��:
 *  - wska�nik do nowej galerii lub NULL je�li nie uda�o si� zaalokowa� pami�ci.
 */
GaleriaPGM* utworz_galerie_pgm (numer_bledu* blad);

/*
 * Wczytuje do galerii obraz.
 * 
 * Parametry:
 *  - galeria:          wska�nik do galerii,
 *  - sciezka_do_pliku: �cie�ka do wczytywanego pliku,
 *  - blad:             wska�nik na zmienn� b��d�w.
 *
 * Warto�� zwracana:
 *  - wska�nik do wczytanego obrazu.
 */
ObrazPGM* wczytaj_obraz_do_galerii_pgm (GaleriaPGM* galeria, sciezka sciezka_do_obrazu, numer_bledu* blad);

/*
 * Usuwa obraz z galerii i zwraca do niego wska�nik.
 * 
 * Parametry:
 *  - galeria: wska�nik do galerii,
 *  - obraz:   wska�nik do obrazu,
 *  - blad:    wska�nik na zmienn� b��d�w.
 *
 * Warto�� zwracana:
 *  - wska�nik do wczytanego obrazu.
 */
ObrazPGM* usun_obraz_z_galerii_pgm (GaleriaPGM* galeria, ObrazPGM* obraz, numer_bledu* blad);

/*
 * Usuwa galeri� i wszystkie zawarte w niej obrazy.
 * 
 * Parametry:
 *  - obraz: wska�nik do galerii.
 */
void usun_galerie_pgm (GaleriaPGM* galeria);



/* Funkcje pojedynczych obraz�w PGM. */

/*
 * Sprawdza czy wska�nik do obrazu nie jest NULLem.
 * 
 * Parametry:
 *  - obraz: wska�nik do obrazu.
 *
 * Zwracana warto��:
 *  - 1 je�li wska�nik nie wskazuje na NULLa, 0 je�li wskazuje.
 */
int jest_obrazem_pgm (ObrazPGM* obraz);

/*
 * Tworzy obraz o podanych parametrach.
 * 
 * Parametry:
 *  - szerokosc: szeroko�� obrazu,
 *  - wysokosc:  wysoko�� obrazu,
 *  - maxval:    maksymalna warto�� koloru,
 *  - blad:      wskaznik do zmienn� b��d�w.
 *
 * Zwracana warto��:
 *  - wska�nik do obrazu je�li alokowanie si� powiod�o, NULL w przeciwnym razie.
 */
ObrazPGM* utworz_obraz_pgm (wymiar szerokosc, wymiar wysokosc, kolor maxval, numer_bledu* blad);

/*
 * Wczytuje obraz z pliku.
 * 
 * Parametry:
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow,
 *  - blad:              wskaznik do zmienn� b��d�w.
 *
 * Zwracana warto��:
 *  - wska�nik do obrazu je�li wczytanie si� powiod�o, NULL w przeciwnym razie.
 */
ObrazPGM* wczytaj_obraz_pgm (sciezka sciezka_do_obrazu, numer_bledu* blad);

/*
 * Ustawia �cie�k� w obrazie.
 * 
 * Parametry:
 *  - obraz:             wska�nik do obrazu,
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow,
 *  - blad:              wskaznik do zmienn� b��d�w.
 */
void ustaw_sciezke_obrazu_pgm (ObrazPGM* obraz, sciezka sciezka_do_obrazu, numer_bledu* blad);

/*
 * Powi�ksza obraz dwukrotnie.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu,
 *  - blad:  wska�nik na zmienn� b��du.
 */
void powieksz_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Odbija obraz w poziomie.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu,
 *  - blad:  wska�nik na zmienn� b��du.
 */
void odbij_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Wykrywa kraw�dzie obrazu w kierunku poziomym.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu,
 *  - blad:  wska�nik do zmiennej b��du.
 */
void wykryj_krawedzie_obrazu_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Wyg�adza histogram obrazu.
 *
 * Parametry:
 *  - obraz: wska�nik do obrazu,
 *  - blad:  wska�nik do zmiennej b��du.
 */
void wygladz_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Zapisuje obraz do pliku o podanej nazwie.
 *
 * Parametry:
 *  - obraz:             wska�nik do obrazu,
 *  - sciezka_do_zapisu: �cie�ka do miejsca, gdzie ma zostac zapisany plik,
 *  - blad:              wska�nik do zmiennej b��du.
 */
void zapisz_obraz_pgm (ObrazPGM* obraz, sciezka sciezka_do_brazu, numer_bledu* blad);

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
 *  - obraz: wska�nik do obrazu,
 *  - blad:  wska�nik na zmienn� b��du.
 *
 * Zwracana warto��:
 *  - wska�nik na histogram obrazu.
 */
HistogramPGM* utworz_histogram_pgm (ObrazPGM* obraz, numer_bledu* blad);

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
ObrazPGM* utworz_obraz_histogramu_pgm (HistogramPGM* histogram, numer_bledu* blad);

/*
 * Zapisuje histogram do pliku CSV.
 *
 * Parametry:
 *  - histogram:             wska�nik do histogramu,
 *  - sciezka_do_histogramu: �cie�ka do pliku, gdzie ma zosta� zapisany histogram,
 *  - blad:                  wska�nik na zmienn� b��du.
 */
void zapisz_histogram_pgm (HistogramPGM* histogram, sciezka sciezka_do_histogramu, numer_bledu* blad);

/*
 * Usuwa histogram z pami�ci.
 *
 * Parametry:
 *  - histogram: wska�nik do histogramu.
 */
void usun_histogram_pgm (HistogramPGM* histogram);

#endif /* #ifndef PGM_LIB */