%{
/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.parser.y - parser języka.                                       *
 *                                                                           *
 *  Na podstawie gramatyki generuje drzewo składni. Jeśli program nie        *
 *  zawiera błędów przkazuje drzewo generatorowi, który zwóci kod maszyny    *
 *  rejestrowej na wyjście. Jeśli zawiera błędy zwróci je na wyjście błędów. *
 *                                                                           *
 *  Już podczas tworzenia drzewa składni, program jest w stanie dokonać      *
 *  pewnych optymalizacji:                                                   *
 *   - zamiana przypadków a := CONST * CONST; na a := CONST;,                *
 *   - zamiana przypadków typu a := b * 1; albo a := b - 0; na a := b;,      *
 *   - usuwanie przypadków a := a;,                                          *
 *   - zamiana przypadków IF CONST THEN cmd1 ELSE cmd2 END na cmd1/cmd2,     *
 *     w zależności od wartości CONST,                                       *
 *   - usuwanie pętli WHILE false THEN cmd END.                              *
 *                                                                           *
 *****************************************************************************/

#include <stdio.h>
#include <string.h>
#include "compiler.node_builder.h"
#include "compiler.generator.h"
#include "bigd.h"

// wyświetlane wiadomości błędu
#define ERR_MSG_UknownError()                   fprintf(stderr,"Error: line %d: Uknown error occured!\n",yylineno)
#define ERR_MSG_UknownIdentifier(name)          fprintf(stderr,"Error: line %d: Identifier \"%s\" hasn't been declared!\n",yylineno,name)
#define ERR_MSG_ConstanceAlreadyUsed(constance) fprintf(stderr,"Error: line %d: Identificator \"%s\" already used by a constance!\n",yylineno,constance)
#define ERR_MSG_NegativeConstance(constance)    fprintf(stderr,"Error: line %d: Constance \"%s\" was given negative value!\n",yylineno,constance)
#define ERR_MSG_VariableAlreadyUsed(variable)   fprintf(stderr,"Error: line %d: Identificator \"%s\" already used by a variable!\n",yylineno,variable)
#define ERR_MSG_AssigmentToConstance(name)      fprintf(stderr,"Error: line %d: %s is a constance and cannot be lvalue!\n",yylineno,name)
#define ERR_MSG_ErrorInConstancesDeclarations() fprintf(stderr,"Error: line %d: Error in constances declarations section!\n",yylineno)
#define ERR_MSG_ErrorInVariablesDeclarations()  fprintf(stderr,"Error: line %d: Error in variables declarations section!\n",yylineno)
#define WRN_MSG_ConstantCondition()             fprintf(stderr,"Warning: line %d: Constant condition in IF-ELSE block!\n",yylineno)
#define WRN_MSG_ConstantLoop()                  fprintf(stderr,"Warning: line %d: Constant condition in WHILE block!\n",yylineno)

int     error_occured = 0;
FILE*   yyout;
int     yylineno;

/**
 * Czy parser ma optymalizować drzewo składni.
 */
int     OptimizeParser = 1;

/**
 * Powtorne sprawdzenie, które stałe są używane.
 */
int     RecheckConstances = 0;


// deklaracje funkcji lokalnych
void        programHelper       (ParserNode * root);
void        defConstHelper      (const char * name, char * value);
void        defVarHelper        (const char * name);
ParserNode* assignHelper        (const char * lvalue_name, ParserNode* rvalue);
ParserNode* condHelper          (ParserNode* condition, ParserNode* ifblock, ParserNode* elseblock);
ParserNode* loopHelper          (ParserNode* condition, ParserNode* block);
ParserNode* inlineHelper        (NodeType type, const char * name1, const char * name2);
ParserNode* readHelper          (const char * lvalue_name);
ParserNode* resolveHelper       (const char * name);
int         resolveConstCond    (ParserNode* condition);

%}

%code requires {
#include <string.h>
#include "compiler.node_builder.h"
#include "bigd.h"
extern int OptimizeParser;
}

%union {
    char*       text;
    char*       value;
    ParserNode* node;
}

// tokeny dla leksera (+ typy wartości przekazywanych dla identyfikatora i liczby)
%token CONST               // początek bloku deklaracji stałych
%token VAR                 // początek bloku deklaracji zmiennych
%token tBEGIN              // początek bloku programu
%token IF                  // początek bloku instrukcji warunkowej (przed samymi warunkami)
%token THEN                // początek bloku instrukcji jeśli prawda
%token ELSE                // początek bloku instrukcji jeśli fałsz
%token WHILE               // początek bloku pętli (przed warunkiem pętli)
%token DO                  // początek bloku instrukcji zawartych w pętli
%token END                 // koniec bloku
%token READ                // odczyt wartości zmiennej ze std. wejścia
%token WRITE               // zapis wartości zmiennej na std. wyjście
%token <text> identifier   // identyfikator stałej/zmiennej
%token <value> num         // liczba naturalna
%token ASGN                // := (przypisanie)
%token EQL                 // = (porównanie)
%token DIFF                // !=
%token LESS                // <
%token GRE                 // >
%token GEQL                // <=
%token LEQL                // >=
%token PLUS                // +
%token MINUS               // -
%token MUL                 // *
%token DIV                 // /
%token MOD                 // %
%token EOC                 // koniec polecenia
%token lexerror            // błąd lexa

// typy tokenów pochodnych
%type <node> commands
%type <node> command
%type <node> condition
%type <node> expression

%%

// gramatyka
program:
   CONST cdeclarations VAR vdeclarations tBEGIN commands END { programHelper ($6); }
 ;

cdeclarations:
   cdeclarations identifier EQL num { defConstHelper ($2, $4); free ($2); }
 | cdeclarations error              { error_occured = 1; ERR_MSG_ErrorInConstancesDeclarations(); }
 | cdeclarations lexerror           { error_occured = 1; ERR_MSG_ErrorInConstancesDeclarations(); }
 | 
 ;

vdeclarations:
   vdeclarations identifier { defVarHelper ($2); free ($2); }
 | vdeclarations error      { error_occured = 1; ERR_MSG_ErrorInVariablesDeclarations(); }
 | vdeclarations lexerror   { error_occured = 1; ERR_MSG_ErrorInVariablesDeclarations(); }
 |
 ;

commands:
   commands command { $$ = extendCommandsN ($1, $2); }
 |                  { $$ = createCommandsN (nopN ()); }
 ;

command:
   identifier ASGN expression EOC               { $$ = assignHelper ($1, $3); free ($1); }
 | IF condition THEN commands ELSE commands END { $$ = condHelper ($2, $4, $6); }
 | WHILE condition DO commands END              { $$ = loopHelper ($2, $4); }
 | READ  identifier EOC                         { $$ = readHelper ($2); }
 | WRITE identifier EOC                         { $$ = writeN (resolveHelper ($2)); free ($2); }
 | error                                        { $$ = errorN (); error_occured = 1; }
 | lexerror                                     { $$ = errorN (); error_occured = 1; }
 ;

condition:
   identifier EQL  identifier   { $$ = inlineHelper (nEQL,   $1,    $3); free ($1); free ($3); }
 | identifier DIFF identifier   { $$ = inlineHelper (nDIFF,  $1,    $3); free ($1); free ($3); }
 | identifier LESS identifier   { $$ = inlineHelper (nLESS,  $1,    $3); free ($1); free ($3); }
 | identifier GRE  identifier   { $$ = inlineHelper (nGRE,   $1,    $3); free ($1); free ($3); }
 | identifier GEQL identifier   { $$ = inlineHelper (nGEQL,  $1,    $3); free ($1); free ($3); }
 | identifier LEQL identifier   { $$ = inlineHelper (nLEQL,  $1,    $3); free ($1); free ($3); }
 ;

expression:
   identifier                   { $$ = resolveHelper ($1); free ($1); }
 | identifier PLUS  identifier  { $$ = inlineHelper (nPLUS,  $1,    $3); free ($1); free ($3); }
 | identifier MINUS identifier  { $$ = inlineHelper (nMINUS, $1,    $3); free ($1); free ($3); }
 | identifier MUL   identifier  { $$ = inlineHelper (nMUL,   $1,    $3); free ($1); free ($3); }
 | identifier DIV   identifier  { $$ = inlineHelper (nDIV,   $1,    $3); free ($1); free ($3); }
 | identifier MOD   identifier  { $$ = inlineHelper (nMOD,   $1,    $3); free ($1); free ($3); }
 ;

%%

/**
 * @brief  Generuje kod, a następnie czyści pamięć.
 * @param  korzeń drzewa składniowego
 */
void programHelper (ParserNode* root) {
    if (RecheckConstances)
        recheckConstancesFor (root, 1);
    if (!error_occured)
        generateCode (root);
    else {
        fprintf (stderr, "\nCheck whether program has all obligatory sections: CONST VAR BEGIN END.\n");
        fprintf (stderr, "Make sure, that:\n");
        fprintf (stderr, " - CONST section looks like: first_const = 1 second_const = 2 etc.,\n");
        fprintf (stderr, " - VAR section looks like: first_var second_var etc.,\n");
        fprintf (stderr, " - all IF ELSE and WHILE blocks are closed, and IF SECTION has ELSE label,\n");
        fprintf (stderr, " - condition in IF is closed by THEN, and condition in WHILE is closed by DO label.\n");
#ifdef DEBUG
        debugNodes (root);
#endif
    }
    destroyN (root);
    resetN ();
    error_occured = 0;
    RecheckConstances = 0;
}

/**
 * @brief  Deklaruje stałą programu.
 * @param  nazwa stałej
 * @param  wartość stałej
 */
void defConstHelper (const char * name, char * value) {
    BIGD n    = bdNew (),
         zero = bdNew ();
        
    if (n == NULL || zero == NULL)
        memoryAllocationError ();
         
    bdConvFromDecimal (n, value);
    bdConvFromDecimal (zero, "0");
    
    if (value[0] == '-') {
        error_occured = 1;
        ERR_MSG_NegativeConstance(name);
    } else
        switch (defConst (name, value)) {
            case 0:
            break;
        case DuplicatedConstanceError:
            error_occured = 1;
            ERR_MSG_ConstanceAlreadyUsed(name);
            break;
        default:
            error_occured = 1;
            ERR_MSG_UknownError();
            break;
        }
    
    bdFree (&n);
    bdFree (&zero);
}

/**
 * @brief  Deklaruje zmienną programu.
 * @param  nazwa zmiennej
 */
void defVarHelper (const char * name) {
    switch (defVar (name)) {
        case 0:
        break;
    case DuplicatedConstanceError:
        error_occured = 1;
        ERR_MSG_ConstanceAlreadyUsed(name);
        break;
    case DuplicatedVariableError:
        error_occured = 1;
        ERR_MSG_VariableAlreadyUsed(name);
        break;
    default:
        error_occured = 1;
        ERR_MSG_UknownError();
        break;
    }
}

/**
 * @brief  Tworzy węzeł przypisania.
 * @param  lwartość
 * @param  pwartość
 * @return węzeł przypisania
 */
ParserNode* assignHelper (const char * lvalue_name, ParserNode* rvalue) {
    ParserNode* lvalue = resolveHelper (lvalue_name);
    
    if (lvalue->type == nCONST) {
        error_occured = 1;
        ERR_MSG_AssigmentToConstance(lvalue_name);
    }
    
    // jeśli mamy do czynienia z przypisaniem wyrażenia, które trzeba obliczyć
    if (OptimizeParser && rvalue->type != nCONST && rvalue->type != nVAR) {
        // zamiast działań na stałych przypisujemy ich wynik - obliczamy wartość i używamy obliczonej stałej
        if (rvalue->firstArg->type == nCONST && rvalue->secondArg->type == nCONST) {
            BIGD left   = bdNew (),
                 right  = bdNew (),
                 result = bdNew (),
                 reminder = bdNew ();
            char *newConst;
            int  size;
            
            if (left == NULL || right == NULL || result == NULL || reminder == NULL)
                memoryAllocationError ();
            
            bdConvFromDecimal (left,  getConstByID (rvalue->firstArg->value));
            bdConvFromDecimal (right, getConstByID (rvalue->secondArg->value));
            
            // oblicza wartość stałego wyrażenia
            switch (rvalue->type) {
                case nPLUS:
                    bdAdd (result, left, right);
                    break;
                case nMINUS:
                    if (bdCompare (left, right) >= 0)
                        bdSubtract (result, left, right);
                    else
                        bdConvFromDecimal (result, "0");
                    break;
                case nMUL:
                    bdMultiply (result, left, right);
                    break;
                case nDIV:
                    bdDivide (result, reminder, left, right);
                    break;
                case nMOD:
                    bdModulo (result, left, right);
                    break;
            }
            
            // zapisuje wartość do stringa
            size = bdConvToDecimal (result, NULL, 0);
            newConst = (char*) malloc (sizeof (char) * (size + 1));
            bdConvToDecimal (result, newConst, size + 1);
            
            // zapisuje nową stałą
            defConst ("", newConst);
            destroyN (rvalue);
            rvalue = resolve ("");
            
            RecheckConstances = 1;
            
            bdFree (&left);
            bdFree (&right);
            bdFree (&result);
            bdFree (&reminder);
        }
        // przypadek stała . zmienna
        else if (rvalue->firstArg->type == nCONST && rvalue->secondArg->type == nVAR) {
            BIGD        constVal = bdNew (),
                        one = bdNew ();
            ParserNode* helper;
            char*       string;
            
            if (constVal == NULL || one == NULL)
                memoryAllocationError ();
            
            // oblicz wartości big intów
            bdConvFromDecimal (constVal,  getConstByID (rvalue->firstArg->value));
            bdConvFromDecimal (one, "1");
            
            if (bdIsZero (constVal)) {
                switch (rvalue->type) {
                    case nPLUS: // 0 + zmienna = zmienna - zbędne obliczenia
                        helper = rvalue->secondArg;
                        free (rvalue);
                        rvalue = helper;
                        break;
                    case nMINUS:// 0 - zmienna = 0
                    case nMUL:  // 0 * zmienna = 0
                    case nDIV:  // 0 / zmienna = 0
                    case nMOD:  // 0 % zmienna = 0
                        destroyN (rvalue);
                        rvalue = resolve ("0");
                        break;
                }
            } else if (bdIsEqual (constVal, one), rvalue->type == nMUL) {
                // 1 * zmienna = zmienna - zbędna instrukcja
                helper = rvalue->secondArg;
                free (rvalue);
                rvalue = helper;
            }
            
            RecheckConstances = 1;
            
            bdFree (&constVal);
            bdFree (&one);
        }
        // przypadek zmienna . stała
        else if (rvalue->firstArg->type == nVAR && rvalue->secondArg->type == nCONST) {
            BIGD        constVal = bdNew (),
                        one = bdNew ();
            ParserNode* helper;
            
            if (constVal == NULL || one == NULL)
                memoryAllocationError ();
            
            // oblicz wartości big intów
            bdConvFromDecimal (constVal,  getConstByID (rvalue->secondArg->value));
            bdConvFromDecimal (one, "1");
            
            if (bdIsZero (constVal)) {
                switch (rvalue->type) {
                    case nPLUS: // zmienna + 0 = zmienna - zbędna instrukcja
                    case nMINUS:// zmienna - 0 = zmienna - zbędna instrukcja
                        helper = rvalue->firstArg;
                        free (rvalue);
                        rvalue = helper;
                        break;
                    case nMUL:  // zmienna * 0 = 0
                    case nDIV:  // zmienna / 0 = 0
                    case nMOD:  // zmienna % 0 = 0 - zbędna instrukcja
                        destroyN (rvalue);
                        rvalue = resolve ("0");
                        break;
                }
            } else if (bdIsEqual (constVal, one)) {
                switch (rvalue->type) {
                    case nMUL:  // zmienna * 1 = zmienna - zbędna instrukcja
                    case nDIV:  // zmienna / 1 = zmienna - zbędna instrukcja
                        helper = rvalue->firstArg;
                        free (rvalue);
                        rvalue = helper;
                        break;
                    case nMOD:  // zmienna % 1 = 0
                        destroyN (rvalue);
                        rvalue = resolve ("0");
                        break;
                }
            }
            
            RecheckConstances = 1;
            
            bdFree (&constVal);
            bdFree (&one);
        }
        
    }
    // przypadek a := a
    if (OptimizeParser && lvalue->type == nVAR && rvalue->type == nVAR && lvalue->value == rvalue->value) {
        destroyN (lvalue);
        destroyN (rvalue);
        return nopN ();
    }
    
    return inlineN (nASGN, lvalue, rvalue);
}

/**
 * @brief  Tworzy węzeł warunku.
 * @param  warunek
 * @param  blok ifa
 * @param  block else'a
 * @return węzeł warunku
 */
ParserNode* condHelper (ParserNode* condition, ParserNode* ifblock, ParserNode* elseblock) {
    // jeśli stały warunek
    if (condition->firstArg->type == nCONST && condition->secondArg->type == nCONST) {
        WRN_MSG_ConstantCondition();
        if (OptimizeParser) {
            ParserNode* result;
            
            if (resolveConstCond (condition)) {
                result = ifblock;
                destroyN (elseblock);    
            } else {
                result = elseblock;
                destroyN (ifblock);
            }
            destroyN (condition);
            
            RecheckConstances = 1;
            
            return result;
        }
    }

    return condN (condition, ifblock, elseblock);
}

/**
 * @brief  Tworzy węzeł pętli.
 * @param  warunek
 * @param  ciało pętli
 * @return węzeł warunku
 */
ParserNode* loopHelper (ParserNode* condition, ParserNode* block) {
    // jeśli stały warunek
    if (condition->firstArg->type == nCONST && condition->secondArg->type == nCONST) {
        WRN_MSG_ConstantLoop();
        if (OptimizeParser) { 
            // usuwa pętle WHILE false
            if (!resolveConstCond (condition)) {
                destroyN (condition);
                destroyN (block);
                return nopN ();
            }
            
            RecheckConstances = 1;
        }
    }

    return loopN (condition, block);
}

/**
 * @brief  Tworzy węzeł wyrażenia lub warunku.
 * @param  pierwszy argument
 * @param  drugi argument
 * @return węzeł 
 */
ParserNode* inlineHelper (NodeType type, const char * name1, const char * name2) {
    return inlineN (type, resolveHelper (name1), resolveHelper (name2));
}

/**
 * @brief  Tworzy węzeł wczytywania wartości zmiennej.
 * @param  nazwa zmiennej
 * @return węzeł odczytu
 */
ParserNode* readHelper (const char * lvalue_name) {
    ParserNode* lvalue = resolveHelper (lvalue_name);
    
    if (lvalue->type == nCONST) {
        error_occured = 1;
        ERR_MSG_AssigmentToConstance(lvalue_name);
    }
    
    return readN (lvalue);
}

/**
 * @brief  Zwraca stałą/zeminną.
 * @param  nazwa zmiennej/stałej
 * @return węzeł
 */
ParserNode* resolveHelper (const char * name) {
    ParserNode* resolved = resolve (name);
    if (resolved == NULL) {
        error_occured = 1;
        resolved = errorN ();
        ERR_MSG_UknownIdentifier(name);
    }
    return resolved;
}

/**
 * @breif  Zwraca wynik stałego porównania.
 * @param  warunek do obliczenia
 * @result wartość stałego warunku  
 */
int resolveConstCond (ParserNode* condition) {
    BIGD left = bdNew (),
         right = bdNew ();
    int  result = 0;
            
    if (left == NULL || right == NULL)
        memoryAllocationError ();
            
    bdConvFromDecimal (left,  getConstByID (condition->firstArg->value));
    bdConvFromDecimal (right, getConstByID (condition->secondArg->value));
    
    switch (condition->type) {
        case nEQL:   result = (bdCompare (left, right) == 0); break;
        case nDIFF:  result = (bdCompare (left, right) != 0); break;
        case nGRE:   result = (bdCompare (left, right)  > 0); break;
        case nLESS:  result = (bdCompare (left, right)  < 0); break;
        case nGEQL:  result = (bdCompare (left, right) >= 0); break;
        case nLEQL:  result = (bdCompare (left, right) <= 0); break;
    }
                 
    bdFree (&left);
    bdFree (&right);
    
    return result;
}

int yyerror (char *s) {
    if (!strcmp (s, "syntax error"))
        ERR_MSG_UknownError();
    else
        fprintf (stderr, "%s:%d!\n", s, yylineno);
    error_occured = 1;
    return 1;
}

