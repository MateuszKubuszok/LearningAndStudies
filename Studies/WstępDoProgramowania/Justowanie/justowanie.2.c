/******************************************************************************
*                                                                             *
*                                  justowanie.c                               *
*                                                                             *
*                           Mateusz Kubuszok (179956)                         *
*                                                                             *
*     Program  sluzacy do justowania tekstu w dwoch kolumnach  z  ktorych     *
*     jedna  jest  kontynuacja drugiej. Lewy magines, odstep  i  dlugosci     *
*     kolumn          podawane         sa         jako         parametry.     *
*                                                                             *
******************************************************************************/

/* Wymagana do pobierania danych wejsciowych (scanf()) i wyswietlenia wyniku (printf()). */
#include <stdio.h>
/* Wymagana do rzutowania parametrow na wartosci liczbowe (atoi()). */
#include <stdlib.h>
/* Wymagana do mierzenia dlugosci slow (strlen()) */
#include <string.h>

/* Dopuszczalna dlugosc wiersza. Suma wartosci parametrow nie moze byc dluzsza od podanej liczby. */
#define LineLength 126

/*
 Element listy dwukierunkowej slow.

 Zawiera pola:
 - Content:  lancuch zawierajacy samo slowo,
 - Length:   jego dlugosc,
 - Next:     wskaznik na nastepnik,
 - Previous: wskaznik na poprzednik.
 */
typedef struct oneWord
{
    char *Content;

    int Length;

    struct oneWord *Next,
                   *Previous;
} OneWord;

/* Wskaznik na strukture listy slow. */
typedef OneWord* WordH;

/* Struktura pomocnicza.

 Zawera wskazniki:
 - CurrentForward:  do biezacego elementu przy listowaniu "w przod",
 - CurrentBackward: do biezacego elementu przy przegladaniu "od konca",
 - First:           na poczatek listy,
 - Last:            na koniec listy.
 */
typedef struct words {
    WordH CurrentForward,
          CurrentBackward,
          First,
          Last;
} WordsHandler;

/*
 Element listy jednokierunkowej linii/wierszy w kolumnach w kolejnosci w jakiej powinno sie je czytac.

 Zawiera pola:
 - MaxLength:   wartosc maksymalnej dopuszczalnej dlugosci linii,
 - FirstWord:   pierwsze slowo w linii,
 - LastWord:    ostatnie slowo w linii,
 - Next:        nastepnik.
 */
typedef struct line {
    int MaxLength;

    WordH FirstWord,
          LastWord;

    struct line *Next;
} Line;

/* Wskaznik na element listy wierszy. */
typedef Line* LineH;

/*
 Struktura pomocnicza.

 Zawierajaca wskazniki na:
 - Break:           ostatni wiersz pierwszej kolumny,
 - Current:         bierzacy element listy wierszy przy przegladaniu,
 - First:           pierwszy wiersz,
 - Last:            ostatni wiersz.
 */
typedef struct lines {
    LineH Break,
          Current,
          First;
} LinesHandler;


/* Deklaracje wykorzystanych w programie funkcji. */

WordH   addNewWord              (WordsHandler*);

int     calculateMaxLinesNo     (WordsHandler*,         const int,      const int);

int     checkColumnsCorrectness (WordsHandler*,         const int,      const int,    const int);

void    correctWords            (WordsHandler*);

void    displayJustifiedColLine (LineH const);

void    displayLine             (const int,             LineH const,    const int,    LineH const);

void    displayResults          (LinesHandler *const,   const int,      const int);

char*   fillWord                (char*,                 WordH,          const int,    const int);

void    freeMemory              (LinesHandler*,         WordsHandler*);

int     makeColumns             (LinesHandler*,         WordsHandler*,  const int,    const int,    const int);



/*
 Pobiera tekst ze standardowego wejscia, justuje w dwoch kolumnach i wysyla na standardowe wejscie.

 Wymaga 4 parametrow:
 1: dlugosc lewego marginesu,
 2: dlugosc lewej kolumny (niezerowa),
 3: dlugosc odstepu miedzy kolumnami,
 4: dlugosc prawej kolumny (niezerowa).

 Suma wartosci parametrow nie moze byc wieksza niz ustawiona dlugosc wiersza konsoli (stala LineLength).

 Slowa musza miescic sie w przydzielonych im kolumnach. W przeciwnym razie wykonywanie programu zostaje przerwane.

 Wyrazy pobierane sa az do napotkania znaku konca pliku (EOF).
 */
int main (int argc, char *argv[]) {
    /* Lancuch do pobierania danych z wejscia. */
    char OriginalString [3*LineLength];

    /* Lista wierszy. */
    LinesHandler *Lines;

    int
        /* Zrzutowane wartosci parametrow programu. */
        Margin,
        S1,
        Distance,
        S2,

        /* Ktory znak strumienia wejsciowego jest obecnie przetwarzany. */
        CurrentSign = 0,
        /* Dlugosc biezacego wyrazu (binarna, po uwzglednieniu znakow spoza ASCII moze byc inna). */
        CurrentLength = 0,
        /* Wynik testu poprawnosci sprawdzanej liczby wierszy. */
        LinesNoCorrectness,
        /* Okresla czy kolumny sie "spotkaly" (podczas wypelniania ich wyrazami). */
        LinesMet = 0,
        /* Ostateczna liczba kolumn <= MaxLinesNo - warunek poprawnosci bedzie spr. dla l. wierszy = MaxLinesNo i mniejszych. */
        MaxLinesNo = 0,

        Iterator;


    /* Lista wyrazow. */
    WordsHandler *Words;

    /* Alokowanie pamieci na liste wyrazow. */
    Words = (WordsHandler*) malloc (sizeof (WordsHandler));
    if (Words == NULL) {
        printf ("Memory allocation error!\n");
        return 0;
    }
    Words->First = NULL;
    Words->Last = NULL;

    /* Alokowanie pamieci na liste wierszy. */
    Lines = (LinesHandler*) malloc (sizeof (LinesHandler));
    if (Lines == NULL) {
        printf ("Memory allocation error!\n");
        free (Words);
        return 0;
    }
    Lines->First = NULL;


    /* Sprawdzenie czy podano wymagana liczbe paramentrow. */
    if (argc != 5) {
         printf ("Invalid number of parameters!\n");
         return 0;
    }

    /* Przypisanie wartosci parametrcw do zmiennych. */
    Margin =    (int) atoi (argv [1]);
    S1 =        (int) atoi (argv [2]);
    Distance =  (int) atoi (argv [3]);
    S2 =        (int) atoi (argv [4]);

    /* Sprawdzenie czy wartosci parametrow spelniaja przypisane im wymagania. */
    if (S1 == 0 || S2 == 0) {
         printf ("Lines' length mustn't be egual to 0!\n");
         return 0;
    } else if (Margin < 0 || S1 < 0 || Distance < 0 || S2 < 0) {
         printf ("None parameter can be less than 0!\n");
         return 0;
    } else if (Margin + S1 + Distance + S2 > LineLength) {
         printf ("Margin + Col 1st length + Distance + Col 2nd length must be less or equal to %d!\n", LineLength);
         return 0;
    }

    /* Przygotwanie do pobierania slow */
    Words->First = NULL;
    if (addNewWord (Words) == NULL) {
        printf ("Memory allocation error!\n");
        return 0;
    }

    /* Wczytywanie wyrazow do dynamicznie alokowanej pamieci. */

    while (scanf ("%s", OriginalString) != -1 && OriginalString [0] != EOF) {
        /* Obliczanie wymaganej pamieci wymaganej dla wyrazu i usuwanie ew. bialych znakow z tesktu. */
        for (CurrentSign = 0; OriginalString [CurrentSign] != '\0'; CurrentSign++)
            if (OriginalString [CurrentSign] != '\n' && OriginalString [CurrentSign] != ' ' && OriginalString [CurrentSign] != '\r' && OriginalString [CurrentSign] != '\t')
                CurrentLength++;

        /* Jesli nie udalo sie zaalokowac pamieci, zwalnia dotychczas zaalokowana pamiec i przerywa program. */
        if (CurrentLength != 0)
            if (fillWord (OriginalString, Words->CurrentForward, CurrentLength, CurrentSign) == NULL || addNewWord (Words) == NULL) {
                printf ("Memory allocation error!\n");
                freeMemory (Lines, Words);
                return 0;
            }

        CurrentSign = 0;
        CurrentLength = 0;
    }

    /* Zakonczenie programu jesli nie wczytano zadnych wyrazow. */
    if (Words->First->Length == 0) {
        freeMemory (Lines, Words);
        return 0;
    }

    /* Zabezpiecznie przed pustym ostatnim wyrazem. */
    correctWords (Words);

    /* Obsluga sytuacji gdy wprowadzony tekst to pojednyczy wyraz. */
    if (Words->First->Next == NULL) {
        if (Words->First->Length <= S1)
            printf ("%s\n", Words->First->Content);
        else
            printf ("The word is too long!");
        freeMemory (Lines, Words);
        return 0;
    }

    /* Sprawdza czy wszytkie slowa sa w stanie zmiescic sie, w ktorejkolwiek  kolumn. */
    for (Words->CurrentForward = Words->First; Words->CurrentForward != NULL; Words->CurrentForward = Words->CurrentForward->Next)
        if (Words->CurrentForward->Length > S1 && Words->CurrentForward->Length > S2) {
            printf ("At least one of words was to long!\n");
            freeMemory (Lines, Words);
            return 0;
        }

    /* Obliczanie gornego ograniczenia dla szukanej liczby wierszy. */
    MaxLinesNo = calculateMaxLinesNo (Words, S1, S2);

    do {
        LinesNoCorrectness = checkColumnsCorrectness (Words, S1, S2, MaxLinesNo);

        switch (LinesNoCorrectness) {
            /* Dla podanych danych co najmniej jeden z wyrazow nie miesci sie w wyznaczonym miejscu. */
            case -2:
                printf ("At least one of words was too long!\n");
                freeMemory (Lines, Words);
            return 0;

            /* Nie istnieje rozwiazanie spelniajace podane zalozenia. */
            case -1:
                printf ("Proper justification wasn't possible!\n");
                freeMemory (Lines, Words);
            return 0;

            /* Podana liczba wierszy jest zbyt wysoka - nalezy sprawdzix nizsze wartosci. */
            case 0:
                MaxLinesNo--;
            continue;

            /* Znaleziono szukano liczbe wierszy - przygotowanie kolumn i wyswietleniee wynikow. */
            case 1:
                /* Czasem zachodzi sytuacja gdy funkcja sprawdzajaca daje poprawne wyniki dla 2 wartosci. Wtedy wybierana jest mniejsza. */
                if (checkColumnsCorrectness (Words, S1, S2, MaxLinesNo-1) == 1)
                    MaxLinesNo--;

                if (makeColumns (Lines, Words, S1, S2, MaxLinesNo))
                    displayResults (Lines, Margin, Distance);
                else
                    printf ("Memory allocation error!\n");
            break;
        }
    } while (LinesNoCorrectness == 0);
            /* Poki liczba wierszy jest zbyt duza, nalezy ja zmniejszac. Znalezienie rozwiazania (lub stwierdzenie jego braku) przeywa petle. */

    /* Zwalnia pamiec przed zakonczeniem programu. */
    freeMemory (Lines, Words);

    return 0;
}


/* Funkcje pomocnicze. */

/*
 Alokuje pamiec dla nowego wyrazu.

 Parametry:
 Words: wskaznik na str. pomocnicza wyrazow.

 Modyfikuje liste Words.

 Zwracana wartosc:
 wskaznik na zaalokowana pamiec.
 */
WordH addNewWord (WordsHandler *Words) {
    if (Words->First == NULL) {
        /* Lista pusta - inicjowanie. */

        Words->First = (WordH) malloc (sizeof (OneWord));
        if (Words->First != NULL) {
            Words->CurrentForward = Words->First;
            Words->CurrentForward->Previous = NULL;
            Words->CurrentBackward = NULL;
            Words->Last = NULL;
        }
    } else {
        /* Dopisywanie wyrazu na koniec listy. */

        Words->CurrentForward->Next = (WordH) malloc (sizeof (OneWord));
        if (Words->CurrentForward != NULL) {
            Words->CurrentForward->Next->Previous = Words->CurrentForward;
            Words->CurrentForward = Words->CurrentForward->Next;
        }
    }

    /* Inicjowanie pozostalych pol elementu listy. */
    if (Words->CurrentForward != NULL) {
        Words->CurrentForward->Next = NULL;
        Words->CurrentForward->Length = 0;
    }

    return Words->CurrentForward;
}

/*
 Oblicza pewne ograniczenie gorne liczby wierszy znjadujace sie w poblizu ostatcznej wartosci.

 Parametry:
 - wskaznik na str. pomocnicza wyrazow,
 - dlugosc pierwszej kolumny,
 - dlugosc drugiej kolumny.

 Zwracana warosc:
 Oszacowana wartosc liczby wierszy.
*/
int calculateMaxLinesNo (WordsHandler *Words, int const S1, int const S2) {
    int CurrentlyRightColumn = 0,
        LinesMet = 0,
        MaxLinesNo = 0,
        SpaceUsedInLine;


    Words->CurrentForward   = Words->First;
    Words->CurrentBackward  = Words->Last;

    /* Szacuje pewne ograniczenie gorne dla liczby wierszy, symulujac wypelnianie kolumn po 1 wierszu, raz z lewej, raz z prawej strony. */
    while (!LinesMet) {
        MaxLinesNo++;

        SpaceUsedInLine = 0;

        if (CurrentlyRightColumn) {
            /* Wypelnianie symulowanego wiersza prawej kolumny. Wypelnianie odbywa sie od konca. */

            do {
                /* Przy poczatku iteracji CurrentForward wskazuje na pierwszy wyraz po ostatnim wykorzystanym - tak wiec mozna go jeszcze uzyc. */
                if (Words->CurrentForward == Words->CurrentBackward)
                    LinesMet = 1;

                /* Oblicza ilosc jaka zajma wyrazy w linii po dodaniu obecnego wyrazu (dl. wyrazu + wymagana spacja). */
                SpaceUsedInLine += Words->CurrentBackward->Length + 1;

                Words->CurrentBackward = Words->CurrentBackward->Previous;

                if (Words->CurrentBackward == NULL)
                    LinesMet = 1;
            } while (SpaceUsedInLine <= (S2 + 1) && !LinesMet);
                    /* Przy obliczaniu zajetego miejsca dochodzi jedna nadmiarowa spacja i trzeba ja uwglednic przy porownywaniu. */
            Words->CurrentBackward = Words->CurrentBackward->Next;
                    /* Po przerwaniu iteracji obecny wyraz jest przesuniety o jeden za daleko - trzeba go cofnac. */

            /* Ew. kolejna iteracja odbedzie sie dla lewej kolumny. */
            CurrentlyRightColumn = 0;
        } else {
            /* Wypelnianie symulowanego wiersza lewej kolumny. Wypelnianie odbywa sie od poczatku. */

            do {
                /* Przy poczatku iteracji CurrentBackward wskazuje na pierwszy wyraz przed ostatnim wykorzystanym - tak wiec mozna go jeszcze uzyc. */
                if (Words->CurrentForward == Words->CurrentBackward)
                    LinesMet = 1;

                /* Oblicza ilosc jaka zajma wyrazy w linii po dodaniu obecnego wyrazu (dl. wyrazu + wymagana spacja). */
                SpaceUsedInLine += Words->CurrentForward->Length + 1;

                Words->CurrentForward = Words->CurrentForward->Next;

                if (Words->CurrentForward == NULL)
                    LinesMet = 1;
            } while (SpaceUsedInLine <= (S1 + 1) && !LinesMet);
                    /* Przy obliczaniu zajetego miejsca dochodzi jedna nadmiarowa spacja i trzeba ja uwglednic przy porownywaniu. */
            if (Words->CurrentForward != NULL)
                Words->CurrentForward = Words->CurrentForward->Previous;
                    /* Po przerwaniu iteracji obecny wyraz jest przesuniety o jeden za daleko - trzeba go cofnac. */

            /* Ew. kolejna iteracja odbedzie sie dla prawej kolumny. */
            CurrentlyRightColumn = 1;
        }
    }

    /* MaxLinesNo obecnie jest wartoscia lini w obu kolumnach lacznie. Zostanie zastapiona przez liczbe wierszy w jednej kolumnie (lewej). */
    return (MaxLinesNo / 2) + (MaxLinesNo % 2) + 1;
                                /* Jezeli liczba kolumn jest nieparzysta MaxLinesNo po podzieleniu zostanie zaokrglone w dol, podczas gdy potrzebne jest zokraglenie w gore. */
}

/*
 Sprawdza czy dla podanych dlugosci kolumn i liczby wierszy otrzymuje sie poprawny wynik.

 Parametry:
 - wskaznik na str. pomocnicza wyrazow,
 - dlugosc pierwszej kolumny,
 - dlugosc drugiej kolumny,
 - sprawdzana liczba wierszy w kolumnach

 Zwracana wartosc:
  1 - jesli otrzymany wynik jest poprawny,
  0 - jesli wierszy jst zbyt duzo i nalezy zmniejszych ich ilos,
 -1 - jesli wierszy jest zbyt malo; w kontekscie calego programu oznacza to ze nie istnieje rozwiazanie dla podanych zalozen,
 -2 - co najmniej jeden z wyrazow nie miesci sie w linii.
*/
int checkColumnsCorrectness (WordsHandler *Words, int const S1, int const S2, int const LinesNo) {
    int CurrentLinesNo = 0,
        SpaceUsedInLine;

    Words->CurrentForward = Words->First;

    /* Symuluje wypelnianie lewej kolumny dla liczby wierszy rowenej LinesNo. */
    while (CurrentLinesNo < LinesNo && Words->CurrentForward != NULL) {
        CurrentLinesNo++;
        SpaceUsedInLine = 0;

        do {
            /* Oblicza ilosc jaka zajma wyrazy w linii po dodaniu obecnego wyrazu (dl. wyrazu + wymagana spacja). */
            SpaceUsedInLine += Words->CurrentForward->Length + 1;

            if (Words->CurrentForward->Length > S1)
                /* Wyraz nie miesci sie w linii! */
                return -2;

            Words->CurrentForward = Words->CurrentForward->Next;
        } while (SpaceUsedInLine <= (S1 + 1) && Words->CurrentForward != NULL);

        if (Words->CurrentForward != NULL)
            Words->CurrentForward = Words->CurrentForward->Previous;
        else {
            if (SpaceUsedInLine > (S1 + 1))
                CurrentLinesNo++;
            break;
        }
    }

    CurrentLinesNo = 0;
    /* Symuluje wypelnianie prawej kolumny dla liczby wierszy rowenej LinesNo. */
    while (CurrentLinesNo < LinesNo && Words->CurrentForward != NULL) {
        CurrentLinesNo++;
        SpaceUsedInLine = 0;

        do {
            /* Oblicza ilosc jaka zajma wyrazy w linii po dodaniu obecnego wyrazu (dl. wyrazu + wymagana spacja). */
            SpaceUsedInLine += Words->CurrentForward->Length + 1;

            if (Words->CurrentForward->Length > S2)
                /* Wyraz nie miesci sie w linii! */
                return -2;

            Words->CurrentForward = Words->CurrentForward->Next;
        } while (SpaceUsedInLine <= (S2 + 1) && Words->CurrentForward != NULL);


        if (Words->CurrentForward != NULL)
            Words->CurrentForward = Words->CurrentForward->Previous;
        else {
            if (SpaceUsedInLine > (S2 + 1))
                CurrentLinesNo++;
            break;
        }
    }

    /* Sprawdzanie czy roznica ilosci wierszy jest rowna 1 lub 0. */
    if (CurrentLinesNo == LinesNo || CurrentLinesNo + 1 == LinesNo ) {
        if (Words->CurrentForward == NULL)
            /* Ustalona liczba wierszy jest wlasciwa. */
            return 1;
        else
            /* Liczba jest zbyt niska - z algorytmu wynika wiec, ze niemozliwe jest poprawne justowanie dla danego przypadku. */
            return -1;
    } else if (CurrentLinesNo > LinesNo)
        /* Liczba jest zbyt niska - z algorytmu wynika wiec, ze niemozliwe jest poprawne justowanie dla danego przypadku. */
        return -1;
    else
        /* Ustalowa liczba wierszy jest zbyt wysoka (nie moze byc zbyt niska bo te ewentualnosc wyklucza kod powyzej). */
        return 0;

}

/*
 Usuwa ostatnie slowo z listy, jesli jest ono puste.

 Modyfikuje liste Words.

 Parametry:
 Words: wskaznik na str. pomocnicza wyrazow.
 */
void correctWords (WordsHandler* Words) {
    /* Wskaznik na usuwany wyraz. */
    WordH ToDelete;

    /* Przewijanie do ostatniego wyrazu. */
    for (ToDelete = Words->First; ToDelete->Next != NULL; ToDelete = ToDelete->Next);;

    Words->Last = ToDelete;

    if (Words->Last->Length == 0) {
        Words->CurrentBackward = Words->Last->Previous;
        Words->Last = Words->Last->Previous;
        Words->Last->Next = NULL;

        if (ToDelete->Content != NULL)
            free (ToDelete->Content);

        free (ToDelete);
    }
}

/*
 Wyswietla wyjustowany wiersz jednej kolumny.

 Parametry:
 ColLine: wskaznik na wiersz.
 */
void displayJustifiedColLine (LineH const ColLine)
{
    int /* Liczba dodatkowych spacji potrzebnych do uzupelnienia wiersza kolumny. */
        AdditionalSpaces = 0,
        /* Wolne miejsce w wierszu. */
        EmptySpace = ColLine->MaxLength,
        Iterator,
        /* Minimalna liczba spacji miedzy wyrazami. */
        MinSpaces = 0,
        /* Iterator przy drukowniu spacji. */
        Spaces,
        /* Liczba slow w wierszu. */
        WordsNo = 0;

    WordH LineWord;


    /* Obliczanie liczby wyrazow oraz ilosci spacji. */
    for (LineWord = ColLine->FirstWord; LineWord != ColLine->LastWord; LineWord = LineWord->Next) {
        EmptySpace -= LineWord->Length;
        WordsNo++;
    }
    EmptySpace -= LineWord->Length;
    WordsNo++;

    /* Obliczanie min. ilosci spacji miedzy wyrazami oraz ilosci spacji dodatkowych. */
    AdditionalSpaces = WordsNo > 1 ? EmptySpace % (WordsNo-1) : 0;
    MinSpaces = (WordsNo > 1) ? EmptySpace / (WordsNo-1) : 0;

    Iterator = 1;
    /* Wyswietlanie wyniku dla wyrazow od pierwszego do przedostatniego. */
    for (LineWord = ColLine->FirstWord; LineWord != ColLine->LastWord; LineWord = LineWord->Next) {
        printf ("%s%s", LineWord->Content, AdditionalSpaces != 0 && (Iterator <= AdditionalSpaces/2 || WordsNo-Iterator <= AdditionalSpaces/2 || (Iterator-1 <= AdditionalSpaces/2 && AdditionalSpaces % 2 != 0)) ? " " : "");
        for (Spaces = 0; Spaces < MinSpaces; Spaces++)
            printf (" ");
        Iterator++;
    }
    /* Wyswietlanie ostatniego wyrazu. */
    printf ("%s", LineWord->Content);
    /* Jesli linia liczy 1 wyraz jest ona dopalniana spacjami do pelna. */
    if (WordsNo == 1)
        for (Spaces = 0; Spaces < ColLine->MaxLength - ColLine->FirstWord->Length; Spaces++)
            printf (" ");
}

/*
 Wyswietla jeden wiersz w konsoli - wyjustowane wiersze lewej i prawej kolumny wraz z odstepami.

 Korzysta z displayJustifiedColLines().

 Parametry:
 - M:       lewy margines,
 - Left:    wskaznik na wiersz lewej kolumny,
 - D:       odstep miedzy kolumnami,
 - Right:   wskaznik na wiersz prawej kolumny.
 */
void displayLine (int const M, LineH const Left, int const D, LineH const Right) {
    int Iterator;


    for (Iterator = 0; Iterator < M; Iterator++)
        printf (" ");

    if (Left != NULL)
        displayJustifiedColLine (Left);

    for (Iterator = 0; Iterator < D; Iterator++)
        printf (" ");

    if (Right != NULL)
        displayJustifiedColLine (Right);

    printf ("\n");
}

/*
 Wyswietla wyjustowany tekst w dwcch kolumnach.

 Korzysta z displayLine().

 Parametry:
 - Lines:   wskaznik na str. pomocnicza wierszy,
 - M:       lewy margines,
 - D:       odstep miedzy kolumnami.
 */
void displayResults (LinesHandler *const Lines, int const M, int const D) {
    LineH Left,  /* Wiersz lewej kolumny. */
          Right; /* Wiersz prawej kolumny. */

    /* Oznaczanie pierwszych wierszy obu kolumn. */
    Left = Lines->First;
    Right = Lines->Break->Next;

    /* Wyswietlanie wierszy od pierwszego do przedostatniego. */
    while (Left != Lines->Break && Left != NULL && Right != NULL) {
        displayLine (M, Left, D, Right);
        Left = Left->Next;
        Right = Right->Next;
    }
    /* Wyswietlanie ostatniego wiersza. */
    displayLine (M, Left, D, Right);
}

/*
 Przepisuje slowo z pobranego lancucha do elementu listy wyrazow.

 Parametry:
 - OriginalString:  wskaznik na pobrane slowo,
 - Word:            wskaznik na wypelniany element listy wyrazow,
 - CurrentLength:   dlugosc przepisywanego slowa (binarna),
 - CurrentSign:     pozycja w lancuchu (ostatnia litera wyrazu).

 Zwracana wartosc:
 wskaznik na lancuch w elemencie listy wyrazow.
 */
char* fillWord (char* OriginalString, WordH Word, int const CurrentLength, int const CurrentSign) {
    int Iterator;


    Word->Content = (char*) malloc ((CurrentLength+1) * sizeof (char));
    if (Word->Content != NULL) {
        for (Iterator = 0; Iterator < CurrentLength; Iterator++)
            Word->Content [Iterator] = OriginalString [CurrentSign-CurrentLength+Iterator];
        Word->Content [CurrentLength] = '\0';

        /* Dlugosc CurrentLength okresla ilosc zajmowanego przez slowo miejsca (bez znaku '\0'). Dla slow z np. polskimi literami moze sie ona roznic od rzeczywistej dlugosci slowa. */
        Word->Length = strlen (Word->Content);
    }

    return Word->Content;
}


/*
 Zwalnianie pamieci przed zakonczeniem programu.

 Parametry:
 - Lines:   wskaznik na str. pomocnicza wierszy,
 - Words:   wskaznik na str. pomocnicza wyrazow.
 */
void freeMemory (LinesHandler *Lines, WordsHandler *Words) {
    /* Wskaznik na usuwany wiersz. */
    LineH DeleteLine;

    /* Wskaznik na usuwany wyraz. */
    WordH DeleteWord;

    /* Zwalnianie pamieci listy wierszy. */
    if (Lines != NULL) {
        Lines->Current = Lines->First;
        while (Lines->Current != NULL) {
            DeleteLine = Lines->Current;
            Lines->Current = Lines->Current->Next;
            free (DeleteLine);
        }

        free (Lines);
    }

    /* Zwalnianie pamieci listy wyrazow. */
    if (Words != NULL) {
        Words->CurrentForward = Words->First;
        while (Words->CurrentForward != NULL) {
            DeleteWord = Words->CurrentForward;
            Words->CurrentForward = Words->CurrentForward->Next;
            if (DeleteWord->Content != NULL)
                free (DeleteWord->Content);
            free (DeleteWord);
        }

        free (Words);
    }
}

/*
 Przydziela wyrazy do wierszy na podstawie wiedzy o docelowej liczbie kolumn.

 Parametry:
 - Lines:   wskaznik na str. pomocnicza wierszy,
 - Words:   wskaznik na str. pomocnicza wyrazow,
 - S1:      dlugosc pierwszej kolumny,
 - S2:      dlugosc drugiej kolumny,
 - LinesNo: liczba wierszy w kolumnach.

 Zwracana wartosc:
 1 w przypadku suksesu, 0 w przypadku niepowodzenia (blad alokacji).
*/
int makeColumns (LinesHandler *Lines, WordsHandler *Words, int const S1, int const S2, int const LinesNo) {
    int CurrentLinesNo = 0,
        SpaceUsedInLine;

    LineH BreakPoint;


    Words->CurrentForward = Words->First;

    /* Inicjowanie listy wierszy. */
    Lines->Current = (Line*) malloc (sizeof (Line));
    Lines->First = Lines->Current;
    if (Lines->Current == NULL)
        return 0;
    Lines->Current->Next = NULL;
    Lines->Current->MaxLength = S1;
    Lines->Current->FirstWord = Words->CurrentForward;
    Lines->Current->LastWord = Words->CurrentForward;

    /* Inicjowanie licznikow: 1 wiersz z 1 wyrazem. */
    CurrentLinesNo = 1;
    SpaceUsedInLine = Words->CurrentForward->Length;

    /* Wypelnianie lewej kolumny. */
    do {
        Words->CurrentForward = Words->CurrentForward->Next;

        if (Words->CurrentForward != NULL) {
            /* Sprawdzanie zajetego miejsca po dodanie nastepnego slowa (wraz z poprzedzajaca je spacja). */
            SpaceUsedInLine += Words->CurrentForward->Length + 1;

            if (SpaceUsedInLine <= Lines->Current->MaxLength)
                /* W wierszu jest dosc miejsca na kolejne slowo. Zostaje ono dodane. */
                Lines->Current->LastWord = Words->CurrentForward;
            else {
                /* Wiersz zostal zapelniony. Inicjowanie nowej linii. */

                Lines->Current->Next = (Line*) malloc (sizeof (Line));

                /* Blad alokacji. */
                if (Lines->Current->Next == NULL)
                    return 0;

                Lines->Current = Lines->Current->Next;
                Lines->Current->Next = NULL;
                Lines->Current->MaxLength = S1;
                Lines->Current->FirstWord = Words->CurrentForward;
                Lines->Current->LastWord = Words->CurrentForward;

                /* Ustawienie licznikow dla nowego wiersza. */

                CurrentLinesNo++;

                SpaceUsedInLine = Words->CurrentForward->Length;
            }
        }
    } while (Words->CurrentForward != NULL && CurrentLinesNo < LinesNo);


    /* Wypelnianie prawej kolumny. */
    do {
        Words->CurrentForward = Words->CurrentForward->Next;

        if (Words->CurrentForward != NULL) {
            /* Sprawdzanie zajetego miejsca po dodanie nastepnego slowa (wraz z poprzedzajaca je spacja). */
            SpaceUsedInLine += Words->CurrentForward->Length + 1;

            if (SpaceUsedInLine <= Lines->Current->MaxLength)
                /* W wierszu jest dosc miejsca na kolejne slowo. Zostaje ono dodane. */
                Lines->Current->LastWord = Words->CurrentForward;
            else {
                /* Wiersz zostal zapelniony. Inicjowanie nowej linii. */

                Lines->Current->Next = (Line*) malloc (sizeof (Line));

                /* Blad alokacji. */
                if (Lines->Current->Next == NULL)
                    return 0;

                Lines->Current = Lines->Current->Next;
                Lines->Current->Next = NULL;
                Lines->Current->MaxLength = S2;
                Lines->Current->FirstWord = Words->CurrentForward;
                Lines->Current->LastWord = Words->CurrentForward;

                /* Ustawienie licznikow dla nowego wiersza. */

                CurrentLinesNo++;

                SpaceUsedInLine = Words->CurrentForward->Length;
            }
        }
    } while (Words->CurrentForward != NULL);

    /* Oznaczanie ostatniej linii pierwszego wiersza - pkt. przelamania kolumn. */
    CurrentLinesNo = 1;
    for (Lines->Break = Lines->First; CurrentLinesNo < LinesNo; CurrentLinesNo++)
        Lines->Break = Lines->Break->Next;

    return 1;
}
