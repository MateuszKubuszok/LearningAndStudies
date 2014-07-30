/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.main.c - funkcja main.                                          *
 *                                                                           *
 *  Wywołuje parser.                                                         *
 *                                                                           *
 *  Można uruchomić z parametrem -no [--nooptimize] aby wyłączyć             *
 *  optymalizację.                                                           *
 *                                                                           *
 *****************************************************************************/

#include <stdio.h>
#include <string.h>
#include "compiler.parser.h"
#include "compiler.generator.h"

int main (int argc, char *argv[]) {
    int i,
        badArg = 0;
    
    // wyłącza domyślną optymalizację
    if (argc > 1)
        for (i = 1; i < argc; i++)
            if (!strcmp ("-no", argv[i]) || !strcmp ("--nooptimize", argv[i])) {
                OptimizeConstances = 0;
                OptimizeParser = 0;
                OptimizeGenerated = 0;
            } else
                badArg = 1;
    
    if (badArg) {
        fprintf (stderr, "Bad argument: program should be runned this way:\n");
        fprintf (stderr, " $ compiler < source > output\n\n");
        fprintf (stderr, "Only argument allowed is -no [--nooptimize]:\n");
        fprintf (stderr, " $ compiler --nooptimize < source > output\n\n");
    } else {
        resetN ();
        yyparse ();
    }
}
