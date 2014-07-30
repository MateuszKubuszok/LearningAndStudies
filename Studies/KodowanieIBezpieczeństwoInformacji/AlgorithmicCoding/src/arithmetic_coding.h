#ifndef ARITHMETIC_CODING_H_INCLUDED
#define ARITHMETIC_CODING_H_INCLUDED

#include <math.h>
#include <stdio.h>
#include <stdlib.h>

#define CharsNo 256
#define charStartsWith1(var) var>127

typedef double range;

typedef struct {
    char    Buffer;
    int     BufferStage;

    range   Lower,
            Upper,
            CharacterOccurance [CharsNo],
            OverallCharacters,
            Possibilities [CharsNo+1];

    int     CenterScalling;

    long unsigned Bits;

    char    *InputFileName;
    FILE    *Input;
    FILE    *Output;
} CodeData;

void        displayInfo     (CodeData*);
CodeData*   acEncodeFile    (char*,     char*);

#endif /* ARITHMETIC_CODING_H_INCLUDED */
