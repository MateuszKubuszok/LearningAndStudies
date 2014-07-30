#include "arithmetic_coding.h"
#include "huffman_coding.h"

void        displayInfo     (CodeData*);
CodeData*   acEncodeFile    (char*,     char*);
CodeData*   initializeAC    (char*,     char*);
FILE*       rewindInputFile (CodeData*);
void        scaleWriterRange(CodeData*);
void        writeBit        (CodeData*, int);
void        writeEnd        (CodeData*);
void        writePrefix     (CodeData*, int);
void        writeRange      (CodeData*, double);

void displayInfo (CodeData *data) {
    int     i;
    range   CharProbability,
            Entropy = 0.0L;


    for (i = 0; i < CharsNo; i++)
        if ((CharProbability = data->Possibilities [i+1] - data->Possibilities [i]) > 0.0L)
            Entropy += CharProbability * -log (CharProbability) / log (2);


    printf ("Kodowanie pliku: %s\n", data->InputFileName);
    printf ("Entropy:                          %.5f\n", Entropy);
    printf ("Avarage Arithmetic Coding length: %.5f\n", ((double) data->Bits)/data->OverallCharacters);
    printf ("Avarage Huffman Coding length:    %.5f\n", countHCAvarageLength (data));
}

CodeData* acEncodeFile (char *inputFileName, char *outputFileName) {
                    /* alokuje i zeruje wszystkie zmienne */
    CodeData *Data = initializeAC (inputFileName, outputFileName);

    char    CurrentChar;
    int     i;
    range   Length;

    if (Data == NULL) {
        printf ("File %s not found!\n", inputFileName);
        return NULL;
    }


    /* zlicza wystapienia znakow */
    while ((CurrentChar = getc (Data->Input)) != EOF)
        Data->CharacterOccurance [(int) CurrentChar] += 1.0L;

    /* liczba wszystkich znakow */
    for (i = 0; i < CharsNo; i++)
        Data->OverallCharacters += Data->CharacterOccurance [i];

    /* prawdopodobienstwa poszczegolnych znakow */
    Data->Possibilities [0] = 0.0L;
    for (i = 0; i < CharsNo; i++)
        Data->Possibilities [i+1] = Data->Possibilities [i] + Data->CharacterOccurance [i]/Data->OverallCharacters;

    /* zapisuje prawdopodobienstwa do pliku */
    for (i = 1; i < CharsNo; i++)
        writeRange (Data, Data->Possibilities [i]);
    Data->Bits = 0; /* ustawia liczbe bitow kodu na 0 */


    /* 'przewija' do poczatku pliku wejsciowego */
    rewindInputFile (Data);


    /* wlasciwe wykonywanie algorytmu */
    while ((CurrentChar = getc (Data->Input)) != EOF) {
        Length = Data->Upper - Data->Lower;

        Data->Upper = Data->Lower + Length * Data->Possibilities [((int) CurrentChar) + 1];
        Data->Lower = Data->Lower + Length * Data->Possibilities [((int) CurrentChar)];

        scaleWriterRange (Data);
    }

    /* wysyla znacznik i zwalnia uchwyty */
    writeEnd (Data);


    return Data;
}

CodeData* initializeAC (char *inputFileName, char *outputFileName) {
    CodeData    *Data = (CodeData*) malloc (sizeof (CodeData));
    int         i;

    if (Data != NULL) {
        if ((Data->Input = fopen (inputFileName, "r")) != NULL && (Data->Output = fopen (outputFileName, "w")) != NULL) {
            Data->Buffer = 0;
            Data->BufferStage = 0;
            Data->CenterScalling = 0;

            Data->Lower = 0.0L,
            Data->Upper = 1.0L;
            Data->CenterScalling = 0;

            Data->InputFileName = inputFileName;

            for (i = 0; i < CharsNo; i++)
                Data->CharacterOccurance [i] = 0.0L;
            Data->OverallCharacters = 0.0L;
        } else {
            free (Data);
            return NULL;
        }
    }

    return Data;
}

FILE* rewindInputFile (CodeData* data) {
    fclose (data->Input);
    return (data->Input = fopen (data->InputFileName, "r"));
}

void scaleWriterRange (CodeData *data) {
    while (data->Upper - data->Lower < 0.5L) {
        if (data->Lower >= 0.0L && data->Upper < 0.5L) {

            data->Lower *= 2.0L;
            data->Upper *= 2.0L;

            /* wysyla 0 po przeskalowaniu zakresow */
            writePrefix (data, 0);
        } else if (data->Lower >= 0.5L && data->Upper < 1.0L) {
            /* przedzial w prawej polowce */

            data->Lower = (data->Lower - 0.5L) * 2.0L;
            data->Upper = (data->Upper - 0.5L) * 2.0L;

            /* wysyla 1 po przeskalowaniu zakresow */
            writePrefix (data, 1);
        } else if (data->Lower >= 0.25L && data->Lower < 0.5L && data->Upper >= 0.5L && data->Upper < 0.75L) {
            /* przedzial zawiera sie w [0,25;0.75) i przecina srodek */

            data->Lower = (data->Lower - 0.25L) * 2.0L;
            data->Upper = (data->Upper - 0.25L) * 2.0L;

            /* zwieksza o 1 liczbe dopelnien wysylanych po 0/1 wysylanych w powyzszych przypadkach */
            data->CenterScalling++;
        } else
            break;
    }
}

void writeBit (CodeData *data, int bit) {
    data->Buffer <<= 1;
    data->Buffer += bit == 0 ? 0 : 1;

    if ((++(data->BufferStage)) == 8) {
        if (data->Output != NULL)
            fputc (data->Buffer, data->Output);
        data->BufferStage = 0;
    }

    data->Bits++;
}

void writeEnd (CodeData *data) {
    range FinalMark = (data->Lower + data->Upper)/2.0L;

    while (FinalMark > 0.0) { /* wysyla znacznik */
        writeBit (data, FinalMark >= 0.5);
        FinalMark *= 2.0;
        FinalMark -= (double) floor (FinalMark);
    }

    while (data->BufferStage != 0) /* dopelnia zerami koncowke bajtu */
        writeBit (data, 0);

    fclose (data->Input);
    fclose (data->Output);
}

void writePrefix (CodeData *data, int firstIs1) {
    int i,
        First = firstIs1 ? 1 : 0,
        Rest = firstIs1 ? 0 : 1;

    writeBit (data, First);
    for (i = 0; i < data->CenterScalling; i++)
        writeBit (data, Rest);

    data->CenterScalling = 0;
}

void writeRange (CodeData *data, range value) {
    char            *DoubleTypeParts = (char*) &value;
    unsigned char   Temp;
    int             i, j;


    if (data->Output != NULL)
        for (i = 0; i < sizeof value; i++) {
            Temp = DoubleTypeParts [i];

            for (j = 0; j < 8; j++) {/* wrzuca po kolei wszystkie bity az oprozni Temp */
                if (Temp != 0) {
                    writeBit (data, charStartsWith1(Temp));
                    Temp <<= 1;
                } else
                    writeBit (data, 0);
            }
        }
}
