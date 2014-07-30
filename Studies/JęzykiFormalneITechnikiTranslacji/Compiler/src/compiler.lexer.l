%{
/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.lexer.l - skaner języka.                                        *
 *                                                                           *
 *  Zwraca tokeny dla parsera i zapisuje identyfikatory oraz stałe.          *
 *                                                                           *
 *****************************************************************************/

#include "compiler.parser.h"
#include <string.h>
#include <errno.h>

int errno;
%}

%x comment
%option yylineno

%%

CONST   { return CONST; }
VAR     { return VAR; }
BEGIN   { return tBEGIN; }
WHILE   { return WHILE; }
DO      { return DO; }
IF      { return IF; }
THEN    { return THEN; }
ELSE    { return ELSE; }
END     { return END; }
READ    { return READ; }
WRITE   { return WRITE; }
":="    { return ASGN; }
"!="    { return DIFF; }
"<="    { return LEQL; }
">="    { return GEQL; }
"<"     { return LESS; }
">"     { return GRE; }
"="     { return EQL; }
"+"     { return PLUS; }
"-"     { return MINUS; }
"*"     { return MUL; }
"/"     { return DIV; }
"%"     { return MOD; }
";"     { return EOC; }
-?[0-9]+  {
            yylval.value = (char*) malloc ((strlen(yytext)+1) * sizeof (char));
            if (yylval.value == NULL) {
                printf ("Memory allocation error! Program Terminated!\n");
                exit (errno);
            }
            strcpy (yylval.value, yytext);
            return num;
        }
[_a-z]+ {
            yylval.text = (char*) malloc ((strlen(yytext)+1) * sizeof (char));
            if (yylval.text == NULL) {
                printf ("Memory allocation error! Program Terminated!\n");
                exit (errno);
            }
            strcpy (yylval.text, yytext);
            return identifier;
        }
"\(\*"  { BEGIN(comment); }
[ \t\r\f\v\n]+ /* usuwa białe znaki */
.       { return lexerror; }

<comment>"\*\)" { BEGIN(0); }
<comment>(.|\n) /* usuń komentarz */

%%
