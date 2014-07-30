/*
 * main.c - Wyœwietla menu bêd¹cem interfejsem u¿ytkownika do bilioteki PGM.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "pgm.h"

#define ROZMIAR_BUFORA 1024



/* Przechowuje galeriê (listê) obrazów. Poniewa¿ w programie u¿ywana jest tylko jedna mo¿na dla wygody u¿yæ zmiennej globalnej. */
ObrazPGM* obraz;



/* Deklaracje funkcji tworz¹cych interfejs. */

void        menu_glowne              (void);
void        wyswietl_opcje_menu      (void);
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

void    wyswietl_parametry_obrazu (ObrazPGM*   obraz);
int     wczytaj_liczbe            (char*       bufor,  int         max_rozmiar_bufora);
void    wczytaj_linie             (char*       bufor,  int         max_rozmiar_bufora);
int     stringi_sa_rowne          (const char* str1,   const char* str2);



/*
 * Inicjalizuje program i tworzy menu.
 */
int main (int argc, int *argv[]) {
    obraz = NULL;

    menu_glowne ();

    usun_obraz_pgm (obraz);
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
    ObrazPGM*   obraz2;

    printf ("\nWczytaj obraz z pliku:\n");
    wczytaj_linie (sciezka_do_obrazu, ROZMIAR_BUFORA);

    if ((obraz2 = wczytaj_obraz_pgm (sciezka_do_obrazu)) != NULL) {
        usun_obraz_pgm (obraz);
        obraz = obraz2;
        printf ("\Wczytano pomyslnie!\n");
        wyswietl_parametry_obrazu (obraz);
    } else
        printf ("\Nie udalo sie wczytac obrazu!\n");
}

/*
 * Zapisuje wybrany obraz z pamiêci do pliku.
 */
void zapisz_obraz (void) {
    printf ("\nZapisz obraz do pliku.\n");

    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        char sciezka_do_obrazu[ROZMIAR_BUFORA];

        printf ("\nPodaj lokacje do zapisu obrazu:\n");
        wczytaj_linie (sciezka_do_obrazu, ROZMIAR_BUFORA);

        if (zapisz_obraz_pgm (obraz, sciezka_do_obrazu)) {
            ustaw_sciezke_obrazu_pgm (obraz, sciezka_do_obrazu);
            printf ("\Udalo sie zapisac obraz do pliku!\n");
        } else
            printf ("\nNie udalo sie zapisac pliku!\n");
    }
}

/*
 * Usuwa wybrany obraz z pamiêci.
 */
void usun_obraz (void) {
    printf ("\nUsun obraz z pamieci.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        usun_obraz_pgm (obraz);
        obraz = NULL;

        printf ("\nUsunieto obraz z pamieci!\n");
    }
}

/*
 * Wyœwietla parametry wybranego obrazu.
 */
void parametry_obrazu (void) {
    printf ("\nWyswietl parametry obrazu.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else
        wyswietl_parametry_obrazu (obraz);
}


/*
 * Wyœwietla histogram wybranego obrazu.
 */
void histogram_obrazu (void) {
    printf ("\nWyswietl histogram obrazu.\n");

    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        HistogramPGM*  histogram;
        
        if ((histogram = utworz_histogram_pgm (obraz)) != NULL) {
            kolor k;
            printf ("\nHistogram obrazu %s:\n", obraz->sciezka_oryginalu != NULL ? obraz->sciezka_oryginalu : "");
            for (k = 0; k <= histogram->maxval; k++)
                printf (" - wartosc koloru: %u, ilosc wystapien: %u/%u\n", k, histogram->ilosc_pikseli[k], histogram->lacznie);
        } else
            printf ("\nNie udalo sie obliczyc histogramu!\n");
    }
}

/*
 * Zapisuje histogram wybranego obrazu do pliku.
 */
void zapisz_histogram_obrazu (void) {
    printf ("\nZapisz histogram obrazu do pliku.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        char          sciezka_do_histogramu[ROZMIAR_BUFORA];
        HistogramPGM* histogram;

        if ((histogram = utworz_histogram_pgm (obraz)) != NULL) {
            /* Kopiuj do zmiennej œcei¿kê do pliku, dopisuje wymagany suffiks i zapisuje histogram. */
            strcpy (sciezka_do_histogramu, obraz->sciezka_oryginalu);
            strcat (sciezka_do_histogramu, "-hist.csv");
            if (zapisz_histogram_pgm (histogram, sciezka_do_histogramu))
                printf ("\nUdalo sie zapisac histogram!\n");
            else
                printf ("\nNie udalo sie zapisac histogramu!\n");

            usun_histogram_pgm (histogram);
        } else
            printf ("\nNie udalo sie obliczyc histogramu!\n");
    }
}

/*
 * Zapisuje obraz histogramu wybranego obrazu do pliku.
 */
void generuj_histogram_obrazu (void) {
    printf ("\nWygeneruj obraz histogramu i zapisz do pliku.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        char          sciezka_do_obrazu[ROZMIAR_BUFORA];
        ObrazPGM*     obraz_histogramu = NULL;
        HistogramPGM* histogram;

        if ((histogram = utworz_histogram_pgm (obraz)) != NULL && (obraz_histogramu = utworz_obraz_histogramu_pgm (histogram)) != NULL) {
            printf ("\nPodaj lokacje do zapisu obrazu histogramu:\n");
            wczytaj_linie (sciezka_do_obrazu, ROZMIAR_BUFORA);
            if (zapisz_obraz_pgm (obraz_histogramu, sciezka_do_obrazu))
                printf ("\nUdalo sie zapisac obraz histogramu!\n");
            else
                printf ("\nNie udalo sie zapisac obrazu histogramu!\n");
        } else
            printf ("\nNie udalo sie stworzyc obrazu histogramu!\n");
        
        usun_obraz_pgm (obraz_histogramu);
        usun_histogram_pgm (histogram);
    }
}

/*
 * Wykrywa krawêdziw wybranego obrazu.
 */
void krawedzie_obrazu (void) {
    printf ("\nWykryj krawedzie.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        if (wykryj_krawedzie_obrazu_pgm (obraz))
            printf ("\nUdalo sie wykryc krawedzie!\n");
        else
            printf ("\nNie udalo sie wykryc krawedzi!\n");
    }
}

/*
 * Wyg³adza histogram wybranego obrazu.
 */
void wygladz_histogram_obrazu (void) {
    printf ("\nWygladz histogram obrazu.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        if (wygladz_obraz_pgm (obraz))
            printf ("\nUdalo sie wygladzic obrazu!\n");
        else
            printf ("\nNie udalo sie wygladzic obrazu!\n");
    }
}

/*
 * Odbija wybrany obraz w poziomie.
 */
void odbij_obraz (void) {
    printf ("\nOdbij obraz w poziomie.\n");
    
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        if (odbij_obraz_pgm (obraz))
            printf ("\nUdalo sie odbic obraz w poziomie!\n");
        else
            printf ("\nNie udalo sie odbic obrazu w poziomie!\n");
    }
}

/*
 * Powiêksza wybrany obraz dwukrotnie.
 */
void powieksz_obraz (void) {
    printf ("\nPowieksz obraz dwukrotnie.\n");
        
    if (obraz == NULL)
        printf ("\nNie wczytano zadnego obrazu!\n");
    else {
        if (powieksz_obraz_pgm (obraz))
            printf ("\nUdalo sie powiekszyc obraz!\n");
        else
            printf ("\nNie udalo sie powiekszyc obrazu!\n");
    }
}



/* Funkcje pomocniczne menu. */

/*
 * Wyœwietla parametry danego obrazu.
 *
 * Parametry:
 *  - obraz: wskaŸnik do obrazu.
 */
void wyswietl_parametry_obrazu (ObrazPGM* obraz) {
    if (obraz == NULL)
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