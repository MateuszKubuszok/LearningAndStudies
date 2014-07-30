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
typedef int          numer_bledu; /* Przechowuje numer b³êdu (b³¹d = 0 jeœli ¿aden b³¹d nie wyst¹pi³, b³¹d != 0 jeœli jakiœ nast¹pi³). */
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

/* Definiuje strukturê Galerii obrazów. */
typedef struct {
    ObrazPGM *pierwszy; /* WskaŸnik na pierwszy obraz w Galerii (NULL jeœli ¿adnego jeszcze nie dodano). */
} GaleriaPGM;

/* Definiuje strukturê Histogramu. */
typedef struct {
    ilosc lacznie;        /* £¹czna iloœæ pikseli w obrazie. */
    ilosc maksimum;       /* Najwiêksza iloœæ pikseli danego koloru (wartoœæ maksimum histogramu). */
    ilosc *ilosc_pikseli; /* Tablica przechowuj¹ca iloœæ pikseli dal danego koloru: ilosc_pikseli[kolor] = ilosc. */
    kolor maxval;         /* Najwiêksza mo¿liwa wartoœæ koloru (rozmiar tablicy to maxval+1, indeksy: od 0 do maxval w³¹cznie). */
} HistogramPGM;



/* Sta³e okreœlaj¹ce b³êdy wystêpuj¹ce przy przetwarzaniu obrazów PGM. */
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
 * Tworzy now¹ galeriê obrazów.
 *
 * Parametry:
 *  - blad: wskaŸnik na zmienn¹ b³êdów.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik do nowej galerii lub NULL jeœli nie uda³o siê zaalokowaæ pamiêci.
 */
GaleriaPGM* utworz_galerie_pgm (numer_bledu* blad);

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
ObrazPGM* wczytaj_obraz_do_galerii_pgm (GaleriaPGM* galeria, sciezka sciezka_do_obrazu, numer_bledu* blad);

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
ObrazPGM* usun_obraz_z_galerii_pgm (GaleriaPGM* galeria, ObrazPGM* obraz, numer_bledu* blad);

/*
 * Usuwa galeriê i wszystkie zawarte w niej obrazy.
 * 
 * Parametry:
 *  - obraz: wskaŸnik do galerii.
 */
void usun_galerie_pgm (GaleriaPGM* galeria);



/* Funkcje pojedynczych obrazów PGM. */

/*
 * Sprawdza czy wska¿nik do obrazu nie jest NULLem.
 * 
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 *
 * Zwracana wartoœæ:
 *  - 1 jeœli wskaŸnik nie wskazuje na NULLa, 0 jeœli wskazuje.
 */
int jest_obrazem_pgm (ObrazPGM* obraz);

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
ObrazPGM* utworz_obraz_pgm (wymiar szerokosc, wymiar wysokosc, kolor maxval, numer_bledu* blad);

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
ObrazPGM* wczytaj_obraz_pgm (sciezka sciezka_do_obrazu, numer_bledu* blad);

/*
 * Ustawia œcie¿kê w obrazie.
 * 
 * Parametry:
 *  - obraz:             wskaŸnik do obrazu,
 *  - sciezka_do_obrazu: sciezka do obrazu w systemie plikow,
 *  - blad:              wskaznik do zmienn¹ b³êdów.
 */
void ustaw_sciezke_obrazu_pgm (ObrazPGM* obraz, sciezka sciezka_do_obrazu, numer_bledu* blad);

/*
 * Powiêksza obraz dwukrotnie.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik na zmienn¹ b³êdu.
 */
void powieksz_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Odbija obraz w poziomie.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik na zmienn¹ b³êdu.
 */
void odbij_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Wykrywa krawêdzie obrazu w kierunku poziomym.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik do zmiennej b³êdu.
 */
void wykryj_krawedzie_obrazu_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Wyg³adza histogram obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik do zmiennej b³êdu.
 */
void wygladz_obraz_pgm (ObrazPGM* obraz, numer_bledu* blad);

/*
 * Zapisuje obraz do pliku o podanej nazwie.
 *
 * Parametry:
 *  - obraz:             wskaŸnik do obrazu,
 *  - sciezka_do_zapisu: œcie¿ka do miejsca, gdzie ma zostac zapisany plik,
 *  - blad:              wskaŸnik do zmiennej b³êdu.
 */
void zapisz_obraz_pgm (ObrazPGM* obraz, sciezka sciezka_do_brazu, numer_bledu* blad);

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
 *  - obraz: wskaŸnik do obrazu,
 *  - blad:  wskaŸnik na zmienn¹ b³êdu.
 *
 * Zwracana wartoœæ:
 *  - wskaŸnik na histogram obrazu.
 */
HistogramPGM* utworz_histogram_pgm (ObrazPGM* obraz, numer_bledu* blad);

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
ObrazPGM* utworz_obraz_histogramu_pgm (HistogramPGM* histogram, numer_bledu* blad);

/*
 * Zapisuje histogram do pliku CSV.
 *
 * Parametry:
 *  - histogram:             wskaŸnik do histogramu,
 *  - sciezka_do_histogramu: œcie¿ka do pliku, gdzie ma zostaæ zapisany histogram,
 *  - blad:                  wskaŸnik na zmienn¹ b³êdu.
 */
void zapisz_histogram_pgm (HistogramPGM* histogram, sciezka sciezka_do_histogramu, numer_bledu* blad);

/*
 * Usuwa histogram z pamiêci.
 *
 * Parametry:
 *  - histogram: wskaŸnik do histogramu.
 */
void usun_histogram_pgm (HistogramPGM* histogram);

#endif /* #ifndef PGM_LIB */