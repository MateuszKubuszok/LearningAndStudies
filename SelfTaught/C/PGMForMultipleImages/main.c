/*
 * main.c - Wyœwietla menu bêd¹cem interfejsem u¿ytkownika do bilioteki PGM.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgm.h"

#define ROZMIAR_BUFORA 1024



/* Przechowuje galeriê (listê) obrazów. Poniewa¿ w programie u¿ywana jest tylko jedna mo¿na dla wygody u¿yæ zmiennej globalnej. */
GaleriaPGM* galeria;



/* Deklaracje funkcji tworz¹cych interfejs. */

void        menu_glowne              (void);
void        wyswietl_opcje_menu      (void);
ObrazPGM*   wybierz_obraz            (void);
void        wczytaj_obraz            (void);
void        zapisz_obraz             (void);
void        usun_obraz               (void);
void        parametry_obrazu         (void);
void        histogram_obrazu         (void);
void        zapisz_histogram_obrazu  (void);
void        generuj_histogram_obrazu (void);
void        krawedzie_obrazu         (void);
void        wygladz_histogram_obrazu (void);
void        odbij_obraz              (void);
void        powieksz_obraz           (void);


/* Deklaracje funkcji pomocniczych. */

void    wyswietl_wiadomosc_bledu  (numer_bledu blad);
void    wyswietl_parametry_obrazu (ObrazPGM*   obraz);
int     wczytaj_liczbe            (char*       bufor,  int         max_rozmiar_bufora);
void    wczytaj_linie             (char*       bufor,  int         max_rozmiar_bufora);
int     stringi_sa_rowne          (const char* str1,   const char* str2);



/*
 * Inicjalizuje program i tworzy menu.
 */
int main (int argc, int *argv[]) {
    numer_bledu blad;

    galeria = utworz_galerie_pgm (&blad);
    if (!blad)
        menu_glowne ();
    else
        wyswietl_wiadomosc_bledu (blad);

    usun_galerie_pgm (galeria);
    return 0;
}



/* Funkcje menu kontroluj¹ce przebieg programu. */

/*
 * Tworzy menu g³ówne - wyœwietla listê dostêpnych opcji i umo¿liwia wybór jednej z nich.
 *
 * Obs³uguje jedynie opcje dotêpne na liœcie - w przypadku wpisania czegokolwiek innego polecenie zostanie zignorowane.
 */
void menu_glowne (void) {                  
    char    polecenie[ROZMIAR_BUFORA];            
    int     zakoncz = 0;

    printf ("Przetwarzanie obrazow PGM\n");
    while(!zakoncz) {
        wyswietl_opcje_menu ();
        wczytaj_linie (polecenie, ROZMIAR_BUFORA);

        /* Wywo³uje podmenu dla odpowiedniej opcji: */
        if      (stringi_sa_rowne (polecenie, "r") || stringi_sa_rowne (polecenie, "wczytaj"))
            wczytaj_obraz ();

        else if (stringi_sa_rowne (polecenie, "w") || stringi_sa_rowne (polecenie, "zapisz"))
            zapisz_obraz ();

        else if (stringi_sa_rowne (polecenie, "d") || stringi_sa_rowne (polecenie, "usun"))
            usun_obraz ();

        else if (stringi_sa_rowne (polecenie, "p") || stringi_sa_rowne (polecenie, "parametry"))
            parametry_obrazu ();

        else if (stringi_sa_rowne (polecenie, "h") || stringi_sa_rowne (polecenie, "histogram"))
            histogram_obrazu ();

        else if (stringi_sa_rowne (polecenie, "H") || stringi_sa_rowne (polecenie, "zapisz hist"))
            zapisz_histogram_obrazu ();

        else if (stringi_sa_rowne (polecenie, "g") || stringi_sa_rowne (polecenie, "generuj hist"))
            generuj_histogram_obrazu ();

        else if (stringi_sa_rowne (polecenie, "e") || stringi_sa_rowne (polecenie, "krawedzie"))
            krawedzie_obrazu ();

        else if (stringi_sa_rowne (polecenie, "s") || stringi_sa_rowne (polecenie, "wygladz"))
            wygladz_histogram_obrazu ();

        else if (stringi_sa_rowne (polecenie, "o") || stringi_sa_rowne (polecenie, "odbij"))
            odbij_obraz ();

        else if (stringi_sa_rowne (polecenie, "R") || stringi_sa_rowne (polecenie, "powieksz"))
            powieksz_obraz ();

        else if (stringi_sa_rowne (polecenie, "x") || stringi_sa_rowne (polecenie, "zakoncz"))
            zakoncz = 1;

        else
            printf("\nNieprawidlowe polecenie!\n");
    }
}

/*
 * Wyœwietla listê dostêpnych opcji. Wykorzystywane w menu g³ównym.
 */
void wyswietl_opcje_menu (void) {
    printf ("\nDostepne polecenia (jednoliterowy skrot lub pelne):\n");
    printf (" r  wczytaj       - wczytuje obraz do pamieci,\n");
    printf (" w  zapisz        - zapisuje obraz do pliku,\n");
    printf (" d  usun          - usuwa obraz z pamieci,\n");
    printf (" p  parametry     - wyswietla parametry obrazu\n");
    printf (" h  histogram     - wyswietla histogram,\n");
    printf (" H  zapisz hist   - zapisuje histogram do pliku,\n");
    printf (" g  generuj hist  - zapisuje obraz PGM histogramu do pliku,\n");
    printf (" e  krawdzie      - wykrywa krawedzie obrazu,\n");
    printf (" s  wygladz       - wygladza histogram,\n");
    printf (" o  odbij         - odbija obraz w poziomie,\n");
    printf (" R  powieksz      - powieksza obraz dwukrotnie,\n");
    printf (" x  zakoncz       - konczy program.\n");
    printf ("\nWybierz polecenie:\n");
}

/*
 * Wczytuje obraz do programu.
 */
void wczytaj_obraz (void) {
    char        sciezka_do_obrazu[ROZMIAR_BUFORA];
    numer_bledu blad;
    ObrazPGM*    obraz;

    printf ("\nWczytaj obraz z pliku:\n");
    wczytaj_linie (sciezka_do_obrazu, ROZMIAR_BUFORA);

    obraz = wczytaj_obraz_do_galerii_pgm (galeria, sciezka_do_obrazu, &blad);

    wyswietl_wiadomosc_bledu (blad);

    if (!blad)
        wyswietl_parametry_obrazu (obraz);
}

/*
 * Zapisuje wybrany obraz z pamiêci do pliku.
 */
void zapisz_obraz (void) {
    printf ("\nZapisz obraz do pliku.\n");

    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        char         sciezka_do_obrazu[ROZMIAR_BUFORA];
        numer_bledu  blad;
        ObrazPGM*    obraz;

        obraz = wybierz_obraz ();

        printf ("\nPodaj lokacje do zapisu obrazu:\n");
        wczytaj_linie (sciezka_do_obrazu, ROZMIAR_BUFORA);
        zapisz_obraz_pgm (obraz, sciezka_do_obrazu, &blad);

        if (!blad)
            ustaw_sciezke_obrazu_pgm (obraz, sciezka_do_obrazu, &blad);

        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Usuwa wybrany obraz z pamiêci.
 */
void usun_obraz (void) {
    printf ("\nUsun obraz z pamieci.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        ObrazPGM*   obraz;
        numer_bledu blad;

        obraz = wybierz_obraz ();
        usun_obraz_z_galerii_pgm (galeria, obraz, &blad);
        usun_obraz_pgm (obraz);

        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Wyœwietla parametry wybranego obrazu.
 */
void parametry_obrazu (void) {
    printf ("\nWyswietl parametry obrazu.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        ObrazPGM* obraz;

        obraz = wybierz_obraz ();
        wyswietl_parametry_obrazu (obraz);
    }
}


/*
 * Wyœwietla histogram wybranego obrazu.
 */
void histogram_obrazu (void) {
    printf ("\nWyswietl histogram obrazu.\n");

    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        numer_bledu    blad;
        ObrazPGM*      obraz;
        HistogramPGM*  histogram;

        obraz = wybierz_obraz ();
        histogram = utworz_histogram_pgm (obraz, &blad);
        
        if (!blad) {
            kolor k;
            printf ("\nHistogram obrazu %s:\n", obraz->sciezka_oryginalu != NULL ? obraz->sciezka_oryginalu : "");
            for (k = 0; k <= histogram->maxval; k++)
                printf (" - wartosc koloru: %u, ilosc wystapien: %u/%u\n", k, histogram->ilosc_pikseli[k], histogram->lacznie);
        } else
            wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Zapisuje histogram wybranego obrazu do pliku.
 */
void zapisz_histogram_obrazu (void) {
    printf ("\nZapisz histogram obrazu do pliku.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        char          sciezka_do_histogramu[ROZMIAR_BUFORA];
        numer_bledu   blad;
        ObrazPGM*     obraz;
        HistogramPGM* histogram;

        obraz = wybierz_obraz ();
        histogram = utworz_histogram_pgm (obraz, &blad);
        
        if (!blad) {
            /* Kopiuj do zmiennej œcei¿kê do pliku, dopisuje wymagany suffiks i zapisuje histogram. */
            strcpy (sciezka_do_histogramu, obraz->sciezka_oryginalu);
            strcat (sciezka_do_histogramu, "-hist.csv");
            zapisz_histogram_pgm (histogram, sciezka_do_histogramu, &blad);
        }
        
        usun_histogram_pgm (histogram);
        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Zapisuje obraz histogramu wybranego obrazu do pliku.
 */
void generuj_histogram_obrazu (void) {
    printf ("\nWygeneruj obraz histogramu i zapisz do pliku.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        char          sciezka_do_obrazu[ROZMIAR_BUFORA];
        numer_bledu   blad;
        ObrazPGM*     obraz;
        ObrazPGM*     obraz_histogramu = NULL;
        HistogramPGM* histogram;

        obraz = wybierz_obraz ();
        histogram = utworz_histogram_pgm (obraz, &blad);
        
        if (!blad)
            obraz_histogramu = utworz_obraz_histogramu_pgm (histogram, &blad);
        
        if (!blad) {
            printf ("\nPodaj lokacje do zapisu obrazu histogramu:\n");
            wczytaj_linie (sciezka_do_obrazu, ROZMIAR_BUFORA);
            zapisz_obraz_pgm (obraz_histogramu, sciezka_do_obrazu, &blad);
        }
        
        usun_obraz_pgm (obraz_histogramu);
        usun_histogram_pgm (histogram);
        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Wykrywa krawêdziw wybranego obrazu.
 */
void krawedzie_obrazu (void) {
    printf ("\nWykryj krawedzie.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        numer_bledu blad;
        ObrazPGM*   obraz;

        obraz = wybierz_obraz ();
        wykryj_krawedzie_obrazu_pgm (obraz, &blad);

        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Wyg³adza histogram wybranego obrazu.
 */
void wygladz_histogram_obrazu (void) {
    printf ("\nWygladz histogram obrazu.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        numer_bledu blad;
        ObrazPGM*   obraz;

        obraz = wybierz_obraz ();
        wygladz_obraz_pgm (obraz, &blad);

        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Odbija wybrany obraz w poziomie.
 */
void odbij_obraz (void) {
    printf ("\nOdbij obraz w poziomie.\n");
    
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        numer_bledu blad;
        ObrazPGM*   obraz;

        obraz = wybierz_obraz ();
        odbij_obraz_pgm (obraz, &blad);

        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Powiêksza wybrany obraz dwukrotnie.
 */
void powieksz_obraz (void) {
    printf ("\nPowieksz obraz dwukrotnie.\n");
        
    if (!jest_obrazem_pgm (galeria->pierwszy))
        printf ("\nNie wczytano zadnych obrazow!\n");
    else {
        numer_bledu blad;
        ObrazPGM*   obraz;

        obraz = wybierz_obraz ();
        powieksz_obraz_pgm (obraz, &blad);

        wyswietl_wiadomosc_bledu (blad);
    }
}

/*
 * Wyœwietla listê obrazów umo¿liwiaj¹c wybór jednego z nich.
 *
 * Przy braku jakichkolwiek obrazów zwraca NULLa.
 *
 * Wartoœæ zwracana:
 *  - wskaŸnik na wybrany obraz lub NULL jeœli ¿adnego nie ma do wyboru.
 */
ObrazPGM* wybierz_obraz (void) {
    /* Galeria jest pusta. */
    if (!jest_obrazem_pgm (galeria->pierwszy))
        return NULL;
    
    if (!jest_obrazem_pgm (galeria->pierwszy->nastepny))
    /* W galerii jest tylko jeden obraz - zostaje on wybrany automatycznie. */
        return galeria->pierwszy;
    else {
    /* W galerii jest wiêcej ni¿ 1 obraz - nale¿y wybraæ 1 do operacji. */
        ObrazPGM* obraz;
        int       i,
                  wybor;
        char      bufor[ROZMIAR_BUFORA];

        do {
            /* Prosi o wybranie obrazu z listy, a¿ jakiœ zostanie wybrany poprawnie. */
            i = 0;
            printf ("\nWybierz obraz do operacji:\n");
            for (obraz = galeria->pierwszy; jest_obrazem_pgm (obraz); obraz = obraz->nastepny)
                printf ("%d: %s\n", ++i, obraz->sciezka_oryginalu);

            wybor = wczytaj_liczbe (bufor, ROZMIAR_BUFORA);

            if  (wybor <= 0 || i < wybor)
                printf ("\nNie ma takiego obrazu w galerii!\n\n");
        } while (wybor <= 0 || i < wybor);
        
        /* Wczytuje wybrany obraz. */
        i = 1;
        for (obraz = galeria->pierwszy; jest_obrazem_pgm (obraz->nastepny) && i != wybor; obraz = obraz->nastepny)
            i++;

        return obraz;
    }
}



/* Funkcje pomocniczne menu. */

/*
 * Wyœwietla wiadomoœæ b³êdu.
 *
 * Parametry:
 *  - blad: numer b³êdu do wyœwietlenia.
 */
void wyswietl_wiadomosc_bledu (numer_bledu blad) {
    switch (blad) {
    case BLAD_NIEOBSLUGIWANY_TYP_OBRAZU:
        fprintf (stderr, "\nNieobslugiwany format pliku - obslugiwany jest jedynie format P2!\n");
        return;
    case BLAD_ZAPISU_DO_PLIKU:
        fprintf (stderr, "\nNie powiodla sie proba zapisu do wskazanego pliku!\n");
        return;
    case BLAD_ODCZYTU_PLIKU:
        fprintf (stderr, "\nNie powiodla sie proba odczytu wskazanego pliku!\n");
        return;
    case BRAK_BLEDU:
        printf ("\nOperacja wykonana pomyslnie!\n");
        return;
    case BLAD_NIEPRAWIDLOWEGO_FORMATU:
        fprintf (stderr, "\nPlik nie jest prawidlowym plikiem P2!\n");
        return;
    case BLAD_ALOKACJI_PAMIECI:
        fprintf (stderr, "\nBlad alokacji pamieci!\n");
        return;
    case BLAD_WSKAZNIKA_DO_NULLA:
        fprintf (stderr, "\nJako argumentu uzyto wzkaznika do NULLa!\n");
        return;
    default:
        fprintf (stderr, "\nNieoczekiwany blad!\n");
        return;
    }
}

/*
 * Wyœwietla parametry danego obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 */
void wyswietl_parametry_obrazu (ObrazPGM* obraz) {
    if (!jest_obrazem_pgm (obraz))
        return;

    printf ("\nParametry obrazu:\n");
    printf ("szerokosc:                  %u\n", obraz->szerokosc);
    printf ("wysokosc:                   %u\n", obraz->wysokosc);
    printf ("maksymalna wartosc czerni:  %u\n", obraz->maxval);
    printf ("polozenie pliku zrodlowego: %s\n", obraz->sciezka_oryginalu != NULL ? obraz->sciezka_oryginalu : "--brak--");
}

/*
 * Wczytuje liczbê ca³kowit¹.
 *
 * Parametry:
 *  - bufor:              bufor do którego wczytyane s¹ znaki,
 *  - max_rozmiar_bufora: ograniczenie rozmiaru bufora.
 *
 * Zwracana wartoœæ:
 *  - wczytana liczba lub -1 dla nieprawid³owych danych.
 */
int wczytaj_liczbe (char* bufor, int max_rozmiar_bufora) {
    int liczba;

    wczytaj_linie (bufor, max_rozmiar_bufora);
    liczba = atoi (bufor);

    if (liczba < 0 || liczba == 0 && (strlen(bufor) > 1 || bufor[0] != '0'))
        liczba = -1;

    return liczba;
}

/*
 * Wczytuje liniê do bufora.
 *
 * Parametry:
 *  - bufor:              bufor do którego wczytane s¹ znaki,
 *  - max_rozmiar_bufora: ograniczenie rozmiaru bufora.
 */
void wczytaj_linie (char* bufor, int max_rozmiar_bufora) {
    char ch;
    int  ilosc_znakow = 0;

    ch = getchar();
    while ((ch != '\n') && (ilosc_znakow < max_rozmiar_bufora)) {
        bufor[ilosc_znakow++] = ch;
        ch = getchar();
    }
    bufor[ilosc_znakow] = '\0';
}

/*
 * Sprawdza czy stringi s¹ równe.
 * 
 * Standardowa funkcja strcmp zwraca -1 lub 1 (zazwyczaj) jeœli stringi siê ró¿ni¹ i 0 jeœli s¹ równe,
 * co mo¿e byæ nieintuicyjne i zacieniaæ kod. St¹d ta nak³adka.
 *
 * Parametry:
 *  - str1: pierwszy z ³añcuchów do porównania,
 *  - str2: drugi z ³anuchów do porównania.
 *
 * Zwracana wartoœæ:
 *  - 1 jeœli ³ancuchy s¹ równe, 0 jeœli siê ró¿ni¹.
 */
int stringi_sa_rowne (const char* str1, const char* str2) {
    return !strcmp (str1, str2) ? 1 : 0;
}