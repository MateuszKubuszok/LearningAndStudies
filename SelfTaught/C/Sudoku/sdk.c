#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

/* Version info */
#define Version "4.0"

/* How should be parsed next argument */
#define ExpectAll               0
#define ExpectSudokuString      1
#define ExpectSudokuFile        2
#define ExpectBasement          3
#define ExpectBasement2         4
#define ExpectAllowedSolutions  5
#define ExpectCommentStart      6
#define ExpectOccuringDigits    7
#define ExpectNewSdkFieldsNo    8

/* Output additional information */
#define Comment_Generated           "Sudoku generated:\n"
#define Comment_ParsingFileFinished "Finished parsing \"%s\"\n\n"
#define Comment_ParsingFileStart    "Parsing file \"%s\"\n\n"
#define Comment_SetAllowedSolutions "Set allowed solutions to (%s)\n\n"
#define Comment_SetBasement         "Set basement 1 to (%s)\n\n"
#define Comment_SetBasement2        "Set basement 2 to (%s)\n\n"
#define Comment_SetComment          "Set comment beginning to: %s\n"
#define Comment_SetDigits           "Set digits table to (%s)\n\n"
#define Comment_Solution            AllowedSolutions != 1 ? "Solution no %d\n" : "Solution:\n"
#define Comment_SolutionsInTotal    "%d solutions in total.\n\n"
#define Comment_SolvingSudoku       "Solving sudoku:\n"

/* Error messages */
#define Error_FailToOpenFile        "Couldn't open file \"%s\"!\n\n"
#define Error_MemoryAllocation      "Memory allocation error!\n"
#define Error_MakeFail              "'%s' is not a valid fields number!\n"
#define Error_NoParameterPassed     "No argument was passed! Use -h parameter for help!\n"
#define Error_NotEnoughDigits       "Sudoku's size require more digits than is available!\n\n"
#define Error_UnsolvableSudoku      "Sudoku is impossible to solve!\n\n"
#define Error_WrongCommentFormat    "Wrong comment format - comment cannot begin with digit!\n\n"
#define Error_WrongInputFormat      "Wrong input format, ignoring.\n\n"
#define Error_WrongParameter        "Wrong parameter (%s)!\n\n"

/* Default values */
#define     DefaultAllowedSolutions     1
#define     DefaultBasement             3
#define     DefaultBasement2            3
const char *DefaultCommentStart =       "#";
const char *DefaultDigitsTable =        ".123456789abcdefghijklmnopqrstuvwxyz";
#define     DefaultDisplayComments      true
#define     DefaultDisplayErrors        true
#define     DefaultDisplayOriginal      true
#define     DefaultDisplayRaw           false
#define     DefaultExpect               ExpectAll


/* Global variables used in the program */

bool    DisplayComments =   DefaultDisplayComments, /* Should comments be displayed */
        DisplayErrors =     DefaultDisplayErrors,   /* Should errors be displayed (memory allocation errors are displayed always) */
        DisplayOriginal =   DefaultDisplayOriginal, /* Should original unsolved sudoku be displayed */
        DisplayRaw =        DefaultDisplayRaw;      /* Should sudoku be displayed as table (false) or as string (true) */

char    *CommentStart,  /* How does comment start */
        *DigitTable;    /* How digits (signs) should be related with integers, DigitTable [0] and '0' always means empty field */

int     *AllPossibilities = NULL,   /* Possibilities are stored in a single dimensional array,
                                       it prevents allocatinging/freeing memory constantly, e.g.
                                       AllPossibilities [C*Dimension] is the beginning for a C field */
        *Sudoku = NULL,             /* Contains sudoku (as single-dimensional array) */

        Basement =  DefaultBasement,     /* Basement x Basement2 - blocks; Basement2 x Basement - elements in block */
        Basement2 = DefaultBasement2,

        Dimension = 0,  /* Length of one row/column */
        Elements =  0,   /* Number of fields in a sudoku */

        AllowedSolutions = DefaultAllowedSolutions, /* To how many solutions should be limitted result (0 = all possible solutions) */
        FoundSolutions;                             /* How many solutions was found till now */


/* Declarations of all functions used in the program */

int*    allocSudoku             ();

bool    checkDeterminaiton      (int);

int     checkField              (int);

void    coverFields             (int);

void    displaySudoku           ();

bool    findNext                (int);

bool    fillObvious             ();

bool    iterate                 (int);

void    generateBasicSudoku     ();

bool    generateSudoku          (int);

int     getField                (int,   int);

void    printHelp               (char*);

bool    shouldContinue          ();

void    shuffleColumns          ();

void    shuffleRows             ();

int     solveSudoku             ();

bool    solveSudokuFromFile     (char*);

bool    solveSudokuFromInput    (char*);


/**
    Run actuall functions and set options, basing on passed parameters.

    Detailed information about startup parameters can be found in printHelp().
*/
int main (int argc, char **argv) {
    int Expect = DefaultExpect,
        C,
        Container;

    printf ("\n");

    if (argc > 1) {
    /* Executes program only if there is any argument passed */

        /* Sets default digits */
        DigitTable = (char*) DefaultDigitsTable;

        /* Sets default comment beginning */
        CommentStart = (char*) DefaultCommentStart;

        for (C = 1; C < argc; C++) {
            /* Parses arguments. Expect != ExpectAll means that an argument requiring value was passed and that value should appear as a next parameter. */
            switch (Expect) {
                /* Catches paramters */
                default:
                case ExpectAll:
                    if      (!strcmp (argv [C], "-s") || !strcmp (argv [C], "--string"))
                        /* Next value shall contain string with sudoku */
                        Expect = ExpectSudokuString;

                    else if (!strcmp (argv [C], "-f") || !strcmp (argv [C], "--file"))
                        /* Next value shall contain file with sudoku */
                        Expect = ExpectSudokuFile;

                    else if (!strcmp (argv [C], "-b") || !strcmp (argv [C], "--base"))
                        /* Next value shall contain new base */
                        Expect = ExpectBasement;

                    else if (!strcmp (argv [C], "-c") || !strcmp (argv [C], "--base2"))
                        /* Next value shall contain new base2 */
                        Expect = ExpectBasement2;

                    else if (!strcmp (argv [C], "-a") || !strcmp (argv [C], "--allowed"))
                        /* Next value shall contain new amount of allowed solutions */
                        Expect = ExpectAllowedSolutions;

                    else if (!strcmp (argv [C], "-k") || !strcmp (argv [C], "--comment"))
                        /* Next value shall contain new beginning */
                        Expect = ExpectCommentStart;

                    else if (!strcmp (argv [C], "-d") || !strcmp (argv [C], "--digits"))
                        /* Next value shall contain new digits */
                        Expect = ExpectOccuringDigits;

                    else if (!strcmp (argv [C], "-o") || !strcmp (argv [C], "--nooriginal"))
                        /* Turns off displaying original sudoku */
                        DisplayOriginal = false;

                    else if (!strcmp (argv [C], "-r") || !strcmp (argv [C], "--raw"))
                        /* Turns on displaying sudoku as string (not table) */
                        DisplayRaw = true;

                    else if (!strcmp (argv [C], "-e") || !strcmp (argv [C], "--noerrors"))
                        /* Turns off displaying errors */
                        DisplayErrors = false;

                    else if (!strcmp (argv [C], "-n") || !strcmp (argv [C], "--nocomments"))
                        /* Turns off displaying comments */
                        DisplayComments = false;

                    else if (!strcmp (argv [C], "-z") || !strcmp (argv [C], "--reset")) {
                        /* Resets all the settings */
                        AllowedSolutions =  DefaultAllowedSolutions;
                        Basement =          DefaultBasement;
                        Basement2 =         DefaultBasement2;
                        CommentStart =      (char*) DefaultCommentStart;
                        DigitTable =        (char*) DefaultDigitsTable;
                        DisplayComments =   DefaultCommentStart;
                        DisplayErrors =     DefaultDisplayErrors;
                        DisplayOriginal =   DefaultDisplayOriginal;
                        DisplayRaw =        DefaultDisplayRaw;
                    }

                    else if (!strcmp (argv [C], "-h") || !strcmp (argv [C], "--help"))
                        /* Displays help */
                        printHelp (argv [0]);

                    else if (!strcmp (argv [C], "-v") || !strcmp (argv [C], "--version"))
                        /* Displays version */
                        printf ("v.%s", Version);

                    else if (!strcmp (argv [C], "-m") || !strcmp (argv [C], "--make"))
                        /* Next value should be number of visible fields */
                        Expect = ExpectNewSdkFieldsNo;

                    else
                        /* Wrong parameter was passed */
                        fprintf (stderr, Error_WrongParameter, argv [C]);
                break;

                /* Catches string with sudoku (value passed after -s or --string) */
                case ExpectSudokuString:
                    solveSudokuFromInput (argv [C]);

                    Expect = ExpectAll;
                break;

                /* Catches file with sudoku (values passed after -f or --file) */
                case ExpectSudokuFile:
                    if (DisplayComments)
                        printf (Comment_ParsingFileStart, argv [C]);

                    solveSudokuFromFile (argv [C]);

                    if (DisplayComments)
                        printf (Comment_ParsingFileFinished, argv [C]);

                    Expect = ExpectAll;
                break;

                /* Catches new basement (values passed after -b or --base) */
                case ExpectBasement:
                    if (DisplayComments)
                        printf (Comment_SetBasement, argv [C]);

                    Container = atoi (argv [C]);
                    if (Container > 0)
                        Basement = Container;

                    Expect = ExpectAll;
                break;

                /* Catches new basement2 (values passed after -c or --base2) */
                case ExpectBasement2:
                    if (DisplayComments)
                        printf (Comment_SetBasement2, argv [C]);

                    Container = atoi (argv [C]);
                    if (Container > 0)
                        Basement2 = Container;

                    Expect = ExpectAll;
                break;

                /* Catches new amount of allowed solutions (values passed after -a or --allowed) */
                case ExpectAllowedSolutions:
                    if (DisplayComments)
                        printf (Comment_SetAllowedSolutions, argv [C]);

                    Container = atoi (argv [C]);
                    if (Container >= 0)
                        AllowedSolutions = Container;

                    Expect = ExpectAll;
                break;

                /* Catches new comment beginning (values passed after -k or --comment) */
                case ExpectCommentStart:
                    for (Container = 0; Container < strlen (DigitTable); Container++)
                        if (argv [C][0] == DigitTable [Container]) {
                            fprintf (stderr, Error_WrongCommentFormat);
                            break;
                        }

                    if (DisplayComments)
                        printf (Comment_SetComment, argv [C]);

                    if (DigitTable != DefaultCommentStart)
                        free (CommentStart);
                    CommentStart = argv [C];

                    Expect = ExpectAll;
                break;

                /* Catches new digits (values passed after -d or --digits) */
                case ExpectOccuringDigits:
                    if (DisplayComments)
                        printf (Comment_SetDigits, argv [C]);

                    if (DigitTable != DefaultDigitsTable)
                        free (DigitTable);
                    DigitTable = argv [C];

                    Expect = ExpectAll;
                break;

                /* Generates new sudoku with visible given number of fields */
                case ExpectNewSdkFieldsNo:
                    Container = atoi (argv [C]);

                    if (Container > 0) {
                        if (DisplayComments)
                            printf (Comment_Generated);
                        generateSudoku (Container);
                    } else
                        fprintf (stderr, Error_MakeFail, argv [C]);

                    Expect = ExpectAll;
                break;
            }
        }

    } else
        fprintf (stderr, Error_NoParameterPassed);

    return 0;
}

/*

    General functions.

*/

/**
    "Refresh" Dimension and Elements values, and reallocate memory for Sudoku (if needed).

    @return NULL if memory couldn't be allocated or wrong basement is given, correct address otherwise.
*/
int* allocSudoku () {
    if (Basement <= 0 && Basement2 <= 0)
        return NULL;

    /* Number of elements in line = (blocks in that line) * (elements in each block) */
    Dimension = Basement * Basement2;

    if (Dimension * Dimension != Elements) {
        Elements = Dimension * Dimension; /* Each sudoku is a square */

        if (Sudoku != NULL)
            free (Sudoku);

        Sudoku = (int*) malloc (sizeof (int) * Elements);

        if (AllPossibilities != NULL)
            free (AllPossibilities);

        AllPossibilities = (int*) malloc (sizeof (int) * Elements * Dimension);
    }

    return AllPossibilities != NULL ? Sudoku : NULL;
}

/**
    Displays sudoku.

    If !DisplayRaw sudoku is displayed as table, displayed as string otherwise.
*/
void displaySudoku () {
    int Column,
        Row;

    for (Row = 0; Row < Dimension; printf ("%s%s", ++Row % Basement == 0 && Row != 0 && Row != Dimension && !DisplayRaw ? "\n" : "", Row != Dimension-1 && !DisplayRaw ? "\n" : ""))
        for (Column = 0; Column < Dimension; printf ("%s", ++Column % Basement2 == 0 && Column != 0 && !DisplayRaw ? " " : ""))
            printf ("%c%s", DigitTable [Sudoku [getField (Column, Row)]], !DisplayRaw ? " " : "");

    printf ("\n\n");
}

/**
    Turns a 2-dimensional field posiition into the possition in single-dimensional array.

    @param  field's column
    @param  field's row

    @return field's position in a single-dimensional array
*/
int getField (int column, int row) {
    return column + row * Dimension;
}

/**
    Prints manual on output.
*/
void printHelp (char *self) {
    printf ("Each Sudoku should be entered as string made of digits. A dot sign or 0 can be\n");
    printf ("used as a replacement for an empty field, e.g.:\n\n");
    printf ("..8.2.4.1.354..8..7..85.3..6...47.....4.8.2..8.71..6454....8..3.8...576.2.639.5..\n\n");
    printf ("stands for:\n\n");

    printf (". . 8  . 2 .  4 . 1\n");
    printf (". 3 5  4 . .  8 . .\n");
    printf ("7 . .  8 5 .  3 . .\n\n");
    printf ("6 . .  . 4 7  . . .\n");
    printf (". . 4  . 8 .  2 . .\n");
    printf ("8 . 7  1 . .  6 4 5\n\n");
    printf ("4 . .  . . 8  . . 3\n");
    printf (". 8 .  . . 5  7 6 .\n");
    printf ("2 . 6  3 9 .  5 . .\n\n\n");

    printf ("If content is read from a file, the only allowed signs are digits and blank\n");
    printf ("characters (separating queries). All other signs will cause displaying error.\n");
    printf ("The only exception are comment beginning and characters that appear between\n");
    printf ("comment beginning marker and the end of a line.\n\n\n");

    printf ("Available options:\n\n");

    printf ("-h                            Displays this content.\n");
    printf ("--help\n\n\n");

    printf ("-s [string]                   Solves typed sudokus.\n");
    printf ("--string [string]\n\n");
    printf ("-f [file]                     Solving each sudoku in each given file,\n");
    printf ("--file [file]                 each Sudoku must be typed in separate line.\n\n\n");

    printf ("-m [value]                    Creates new sudoku instead of solving one,\n");
    printf ("--make [value]                [value] describes number of visible fields.\n\n\n");

    printf ("-b [value]                    Base x base2 - blocks in a sudoku\n");
    printf ("--base                        base2 x base - fields in a block.\n");
    printf ("-c [value]                    Default base: %d, Default base2: %d\n", DefaultBasement, DefaultBasement2);
    printf ("--base2 [value]\n\n\n");

    printf ("-a [value]                    Ammounts of solutions to display (if there is\n");
    printf ("--amount [value]              more than one). By default %d (0 = all).\n\n", DefaultAllowedSolutions);
    printf ("-d [string]                   Ordered signs used as digits passed as one string,");
    printf ("--digits [string]             beginning with a sign standing for 0/empty field.\n\n");
    printf ("-k [string]                   String marking beginning of a comment line\n");
    printf ("--comment [string]            (file only), default: %s.\n\n\n", DefaultCommentStart);

    printf ("-e                            Don't display errors.\n");
    printf ("--noerrors                    Affect all sudokus after the parameter.\n\n");
    printf ("-n                            Displays only solutions (and maybe errors)\n");
    printf ("--nocomments                  without additional information (works as -e).\n\n");
    printf ("-o                            Don't display original (unsolved) sudoku.\n");
    printf ("--noorignal\n\n");
    printf ("-r                            Displays sudokus in a \"raw\" form\n");
    printf ("--raw                         (the same as input).\n\n\n");

    printf ("-z                            Resets all settings to default.\n");
    printf ("--reset                       The only way to turn off -n, -o and -r.\n\n\n");

    printf ("-v                            Display version.\n");
    printf ("--version\n\n\n");

    printf ("Options can be used together e.g.:\n\n");

    printf ("%s -s [stream1] -f [file1] -e -f [file2] -s [stream3]\n", self);
    printf ("\treads 1 sudoku from input, all sudokus from file, then turns off\n");
    printf ("\terror displaying for next sudokus, sudokus from string (1)\n");
    printf ("\tand from file are displayed without errors;\n\n");

    printf ("%s -a 2 -f [file1] -f [file2] -s [stream1] -n -s [stream2]\n", self);
    printf ("\trequire 2 solutions (if possible) from all solved,\n");
    printf ("\tthen reads 2 files and 1 stream, turns off comments,\n");
    printf ("\tand solves the last sudoku from string.\n");
}

/*

    Solving functions.

*/

/**
    Checks whether this field is the only one in column/row/block with some possibility.

    Fills that field automaticaly.

    Should be predecessed checkField (field) calling.

    @param  checked field number

    @return true if field is found, false otherwise
*/
bool checkDetermination (int field) {
    int C, C2, D, F,
        BeginC, BeginR, EndC, EndR;


    for (D = 0; D < Dimension; D++) {
        if (AllPossibilities [field*Dimension + D]) {
        /* Obtains possible value for field */

            BeginC = field % Dimension;             /* Index of first element in the field's column */
            EndC = BeginC + (Elements - Dimension); /* Index of last element in the fields column */

            for (C = BeginC; C <= EndC; C += Dimension) {
            /* Moves down the column - distance between each two "neighbour" fields in a column = Dimension */
                if (Sudoku [C])
                    continue; /* Ommits filled field */

                checkField (C);
                if (C != field && AllPossibilities [field*Dimension + D] == AllPossibilities [C*Dimension + D])
                    /* There is another field in a column with the same possibility - jump out of loop */
                    goto RowTest;
            }
            /* There wasn't another field with the same possibility - field has to contain this value */
            Sudoku [field] = D+1;
            return true;


            RowTest:

            BeginR = (field / Dimension) * Dimension;   /* Index of first element in the field's row */
            EndR =  BeginR + Dimension;                 /* Index of last element in the field's row */

            for (C = BeginR; C < EndR; C++) {
            /* Moves down the row - distance between each two "neighbour" fields in a column = 1 */
                if (Sudoku [C])
                    continue; /* Ommits filled field */

                checkField (C);
                if (C != field && AllPossibilities [field*Dimension + D] == AllPossibilities [C*Dimension + D])
                    /* There is another field in a row with the same possibility - jump out of loop */
                    goto BlockTest;
            }
            /* There wasn't another field with the same possibility - field has to contain this value */
            Sudoku [field] = D+1;
            return true;


            BlockTest:

            BeginC = ((BeginC) / Basement2) * Basement2;                /* Number of first block's column */
            EndC = BeginC + Basement2;                                  /* Number greater by 1 than the last block's column's */
            BeginR = (((BeginR) / Dimension) / Basement) * Basement;    /* Number of first block's row */
            EndR = BeginR + Basement;                                   /* Number greater by 1 than the last block's row's */

            for (C = BeginC; C < EndC; C++) {
                for (C2 = BeginR; C2 < EndR; C2++) {
                    F = getField (C, C2);

                    if (Sudoku [F])
                        continue; /* Ommits filled field */

                    if (field != F) {
                        checkField (F);

                        if (AllPossibilities [field*Dimension + D] == AllPossibilities [F*Dimension + D])
                            /* There is another field in a block with the same possibility - jump out of loop */
                            goto NextPossibleDigit;

                    }
                }
            }
            /* There wasn't another field with the same possibility - field has to contain this value */
            Sudoku [field] = D+1;
            return true;

            NextPossibleDigit: ;
        }
    }

    return false;
}

/**
    Checks, what numbers are possible to put in a given field.

    @param  checked field number

    @return amount of possibilities
*/
int checkField (int field) {
    int C, C2, C3, F,
        BeginC, EndC,
        BeginR, EndR,
        StartField;

    StartField = field * Dimension;

    /* Setting all fields' values to true */
    for (C = 0; C < Dimension; C++)
        AllPossibilities [StartField + C] = true;


    /* Checks horizontal (columns) */

    BeginC = field % Dimension; /* Index of first element in the field's column */
    EndC = BeginC + (Elements - Dimension); /* Index of last element in the fields column */

    for (C = BeginC; C <= EndC; C += Dimension)
    /* Moves down the column - distance between each two "neighbour" fields in a column = Dimension */
        if (C != field && Sudoku [C] > 0)
            /* Set false on all values that already appeared in field's column */
            AllPossibilities [StartField + Sudoku [C]-1] = false;


    /* Checks vertical (rows) */

    BeginR = (field / Dimension) * Dimension; /* Index of first element in the field's row */
    EndR =  BeginR + Dimension; /* Index of last element in the field's row */

    for (C = BeginR; C < EndR; C++)
    /* Moves down the row - distance between each two "neighbour" fields in a column = 1 */
        if (C != field && Sudoku [C] > 0)
            /* Set false on all values that already appeared in field's row */
            AllPossibilities [StartField + Sudoku [C]-1] = false;


    /* Checks the block */

    BeginC = (BeginC / Basement2) * Basement2; /* Number of first block's column */
    EndC = BeginC + Basement2; /* Number greater by 1 than the last block's column's */
    BeginR = (((BeginR) / Dimension) / Basement) * Basement; /* Number of first block's row */
    EndR = BeginR + Basement; /* Number greater by 1 than the last block's row's */

    for (C = BeginC; C < EndC; C++)
        for (C2 = BeginR; C2 < EndR; C2++) {
            F = getField (C, C2);
            if (F != field && Sudoku [F] > 0)
                /* Set false on all values that already appeared in field's block */
                AllPossibilities [StartField + Sudoku [F]-1] = false;
        }

    /* Count possibilities */
    for (C = C2 = 0; C < Dimension; C++)
        if (AllPossibilities [StartField + C])
            C2++;

    return C2;
}

/**
    Checks if current field is the last one in sudoku and if field is empty (thus shall be iterated).

    @param  which filed is currently checked

    @return true if searching can be stopped (found all required solutions), false otherwise
*/
bool findNext (int Current) {
    if (!Sudoku [Current])
        /* Field is empty - all possible fillings are checked */
        return iterate (Current);

    else {
        if (Current >= Elements-1) {
            /* Last field in the sudoku is filled = sudoku is completely filled */
            FoundSolutions++;

            if (DisplayComments)
                printf (Comment_Solution, FoundSolutions);

            displaySudoku ();

            /* If all required solutions are found searching can be stopped (shouldContinue() == false) */
            return !shouldContinue ();
        } else
            /* Filled field isn't last one - moving on to the next field */
            return findNext (Current+1);
    }
}

/**
    Fills empty fields with only one possible number till only those with more that one possibilities are left.

    @return false if sudoku appears to be impossible to solve, true otherwise
*/
bool fillObvious () {
    int C,
        C2,
        P;

    bool Changed;

    do {
        Changed = false; /* If, after an iteration, none field is changed, all obvious fields are filled */

        for (C = 0; C < Elements; C++) {
            if (!Sudoku [C]) {
            /* Found empty field in sudoku */
                P = checkField (C);

                if (P == 1) {
                /* Unfilled field with one possible number is found */
                    for (C2 = 0; C2 < Dimension; C2++) {
                        if (AllPossibilities [C*Dimension + C2]) {
                            Sudoku [C] = C2+1;
                            Changed = true; /* Some new fields are filled - that afftcts other fields possibilities */
                            break;
                        }
                    }
                }

                else if (!P)
                /* Some field hasn't got any possiblility - sudoku is impossible to solve */
                    return false;

                else if (checkDetermination (C))
                /* Some fields are the only one in block that can contain some value - than thet have to contain it to be a proper sudoku */
                    Changed = true;
            }
        }
    } while (Changed);
    /* As long as some fields are being filled, some other fileds' possiblilities change - thus they shall be checked as well */

    return true;
}

/**
    Checkes all possibibilities for a given field.

    @param  which filed is currently iterated

    @return true if searching can be stopped (found all required solutions), false otherwise
*/
bool iterate (int Current) {
    int  C;

    if (!checkField (Current)) {
        /* No possibilities - getting back to the previous iteration */
        return false;
    }

    for (C = 0; C < Dimension; C++)
        if (AllPossibilities [Current*Dimension + C]) {
        /* Checking possiblility for a field */

            Sudoku [Current] = C+1; /* Filling field (Possibilities indexes goes from 0 to Dimension-1, while sudoku's values goes from 1 to Dimension) */

            if (Current >= Elements-1) {
            /* Filled field is the last one - found the solution */
                FoundSolutions++;

                if (DisplayComments)
                    printf (Comment_Solution, FoundSolutions);

                displaySudoku ();

                /* It's impossible to exist more that one solution differing by one (the last) field, so it's safe to stop here */
                return !shouldContinue ();
            }

            /* Field isn't last one - moves on to the next field */
            if (findNext (Current + 1)) {
                /* Required amount of solutions was found - stops searching */
                return !shouldContinue ();
            }
        }

    Sudoku [Current] = 0; /* Getting back - field should be erased to make code (possibilities checking) work properly */

    /* If required amount of solutions was found, program wouldn't get here - thus false */
    return false;
}

/**
    Defines whether program should continue to search solutions.

    @return true if program should continue, false otherwise
*/
bool shouldContinue () {
    return (bool) (!AllowedSolutions || (FoundSolutions < AllowedSolutions));
}

/**
    Finds required amounts of solutions for given sudoku.

    @return amount of found solutions
*/
int solveSudoku () {
    if (DisplayComments)
        printf (Comment_SolvingSudoku);
    if (DisplayOriginal)
        displaySudoku ();

    /* Resets solution counter */
    FoundSolutions = 0;

    /* Fills all "obvious" fields */
    if (!fillObvious ()) {
        if (DisplayErrors)
            fprintf (stderr, Error_UnsolvableSudoku);

        return false;
    }

    /* Initiates finding fields with more than one possible number */
    findNext (0);

    if (!FoundSolutions && DisplayErrors)
        fprintf (stderr, Error_UnsolvableSudoku);
    else if (AllowedSolutions != 1 && DisplayComments)
        printf (Comment_SolutionsInTotal, FoundSolutions);

    return FoundSolutions;
}

/**
    Reads sudokus from file, and solves them.

    @param  checked file

    @return true if file was opened successfully, false otherwise
*/
bool solveSudokuFromFile (char *FileName) {
    char Container;

    int C,
        D,
        Digit;

    FILE *FilePointer;


    if (strlen (DigitTable) < Dimension) {
        if (DisplayErrors)
            fprintf (stderr, Error_NotEnoughDigits);
        return false;
    }

    if (allocSudoku () == NULL) {
        fprintf (stderr, Error_MemoryAllocation);
        return false;
    }

    if ((FilePointer = fopen (FileName, "r")) == NULL) {
        if (DisplayErrors)
            fprintf (stderr, Error_FailToOpenFile, FileName);
        return false;
    }

    C = 0; /* Resets position in Sudoku array */
    D = 0; /* Resets position in tested Comment string */

    while ((Container = getc (FilePointer)) != EOF ) {
    /* Reads all characters from file till the EOF */

        /* Comment line testing */
        if (Container == CommentStart [D] && CommentStart [D] != '\0') {
            /* Now D != 0 => string MIGHT be comment */
            C = 0;
            D++;
            continue;
        } else if ((D > 0 && D < strlen (CommentStart) && CommentStart [D] != '\0') || Container == '\n') {
            /* Line was tested as comment but appeared to be not */
            D = 0;
            continue;
        }

        if (D < strlen (CommentStart) && (Container == '\n' || Container == '\t' || Container == ' ')) {
        /* String with sudoku doesn't have any blank characters inside */
            if (C > 0 && DisplayErrors)
                /* Character is neither the first sign in a line nor an end of a comment line */
                fprintf (stderr, Error_WrongInputFormat);

            C = 0;
            continue;

        } else if (Container == '0')
        /* '0' is always treated as empty field */
            Sudoku [C] = 0;

        else if (D < strlen (CommentStart)) {
        /* If comment is tested charakters aren't entered, since comment cannot begins with a "digit" it prevents errors */

            /* Search int value for character inside DigitTable array. */
            for (Digit = 0; DigitTable [Digit] != '\0' && Digit <= Dimension; Digit++)
                if (DigitTable [Digit] == Container) {
                /* Value was found - in the next iteration new character can be read */

                    if (C < 0) /* C == -1 if wrong input error occured and blank charakter haven't appeared since that time */
                        C = 0;

                    Sudoku [C] = Digit;
                    C++;
                    goto SkipError; /* Replaces break; and if() condition */
                }

            /* Character isn't declared as "Digit" - string is incorrect, thus sudoku is reset */
            if (C >=0 && DisplayErrors)
                fprintf (stderr, Error_WrongInputFormat);
            C = -1; /* C == -1 if wrong input error occured and blank charakter haven't appeared since that time */
        }

        SkipError: ;

        /* If sudoku is successfully loaded, solution can be sought */
        if (C >= Elements) {
            solveSudoku ();
            C = 0; /* Search is complete, next sudoku is loaded */
        }
    }

    fclose (FilePointer);
    return true;
}

/**
    Reads sudoku from input (passed as parameter), and solves it.

    @param  string with sudoku

    @return true if sudoku was loaded and solved successfully, false otherwise
*/
bool solveSudokuFromInput (char *Input) {
    int C,
        Digit;

    if (strlen (DigitTable) < Dimension) {
        if (DisplayErrors)
            fprintf (stderr, Error_NotEnoughDigits);
        return false;
    }

    if (allocSudoku () == NULL) {
        fprintf (stderr, Error_MemoryAllocation);
        return false;
    }

    for (C = 0; C < Elements; C++) {
        if (Input [C] == '0')
        /* '0' is always treated as empty field */
            Sudoku [C] = 0;

        else {
        /* Search int value for character inside DigitTable array. */
            for (Digit = 0; DigitTable [Digit] != '\0' && Digit <= Dimension; Digit++)
                if (DigitTable [Digit] == Input [C]) {
                /* Value was found - in the next iteration new character can be read */
                    Sudoku [C] = Digit;
                    goto NextDigit; /* Replaces break; and condition within next if() */
                }

            /* Character isn't declared as "Digit" - string is incorrect */
            if (DisplayErrors)
                fprintf (stderr, Error_WrongInputFormat);
            return false;
        }

        NextDigit: ;
    }

    /* If sudoku is successfully loaded, solution can be sought */
    return (bool) solveSudoku ();
}

/*

    Generating functions.

*/

/**
    Covers random coverNo-number fields.

    @param  how many fields should be covered
*/
void coverFields (int coverNo) {
    int i,
        tmp;

    for (i = 0; i < coverNo; i++) {
        tmp = rand () % Elements;

        while (Sudoku [tmp] == 0)
            tmp = (++tmp) % Elements;

        Sudoku [tmp] = 0;
    }
}

/**
    Generates basic sudoku.

    All other sudokus are its permutations.
*/
void generateBasicSudoku () {
    int Column, Row, Block, CopyColumn, CopyRow;

    for (Row = 0; Row < Basement; Row++)
        for (Column = 0; Column < Dimension; Column++)
            Sudoku [getField (Column, Row)] = ((Row * Basement2 + Column) % Dimension) + 1;

    for (Block = 1; Block < Basement2; Block++) {
        for (Column = 0; Column < Dimension; Column++) {
            CopyColumn = (Column + Block) % Dimension;

            for (Row = 0; Row < Basement; Row++) {
                CopyRow = (Row + Block*Basement);

                Sudoku [getField (CopyColumn, CopyRow)] = Sudoku [getField (Column, Row)];
            }
        }
    }
}

/**
    Generates sudoku with visibleNo visible fields.

    @param  number of visible fields

    @return true if succeed, false otherwise
*/
bool generateSudoku (int visibleNo) {
    if (allocSudoku () == NULL) {
        fprintf (stderr, Error_MemoryAllocation);
        return false;
    }

    generateBasicSudoku ();

    srand (time (0));

    shuffleColumns ();
    shuffleRows ();

    coverFields (Elements - (visibleNo % (Elements)));

    displaySudoku ();

    return true;
}

/**
    Shuffles random columns in order to create another sudoku.
*/
void shuffleColumns () {
    int Block,
        Trial,
        Row,
        i,
        j,
        tmp;

    for (Block = 0; Block < Basement; Block++)
        for (Trial = 0; Trial < Basement; Trial++) {
            i = (rand () % Basement2) + (Block * Basement2);
            j = (rand () % Basement2) + (Block * Basement2);

            if (i != j)
                for (Row = 0; Row < Dimension; Row++) {
                    tmp = Sudoku [getField (i, Row)];
                    Sudoku [getField (i, Row)] = Sudoku [getField (j, Row)];
                    Sudoku [getField (j, Row)] = tmp;
                }
        }
}

/**
    Shuffles random rows in order to create another sudoku.
*/
void shuffleRows () {
    int Block,
        Trial,
        Column,
        i,
        j,
        tmp;

    for (Block = 0; Block < Basement2; Block++)
        for (Trial = 0; Trial < Basement2; Trial++) {
            i = (rand () % Basement) + (Block * Basement);
            j = (rand () % Basement) + (Block * Basement);

            if (i != j)
                for (Column = 0; Column < Dimension; Column++) {
                    tmp = Sudoku [getField (Column, i)];
                    Sudoku [getField (Column, i)] = Sudoku [getField (Column, j)];
                    Sudoku [getField (Column, j)] = tmp;
                }
        }
}
