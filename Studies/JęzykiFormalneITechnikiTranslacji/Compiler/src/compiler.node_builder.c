/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.node_builder.c - definicje funkcji wykorzystywanych do          *
 *                            wygenerowania  węzłów.                         *
 *                                                                           *
 *  Tworzy drzewo składni i przechowuje informacje na temat zadeklarowanych  *
 *  stałych i zmiennych.                                                     *
 *                                                                           *
 *  Przy napotkaniu zmiennych w kodzie programu wywoływana jest funkcja      *
 *  resolve(name) zwracająca wewnętrzny identyfikator. Jednocześnie na       *
 *  liście stałych zostanie zaznaczone, jeśli stała zostanie użyta (dokonane *
 *  zostanie wywołane resolve dla jej nazwy). Po sparsowaniu kodu będziemy   *
 *  dysponować drzewem składni, oraz listą z (użytymi) stałymi i zmiennymi.  *
 *                                                                           *
 *****************************************************************************/

#include "compiler.node_builder.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#define InitialConstancesSize           10
#define ConstancesIncreaseFactor        2
#define InitialVariablesSize            10
#define VariablesIncreaseFactor         2
#define InitialCommandsBlockSize        10
#define CommandsBlockIncrementFactor    2

extern int errno;

/**
 * Czy optymalizować deklaracje stałých.
 */
int OptimizeConstances = 1;

/**
 * Zmienna.
 */
typedef char* Variable;

/**
 * Stała.
 */
typedef struct structConstance {
    char* value;
    char* name;
    int   used;
} Constance;
  
/**
 * Tablica stałych.
 */
int         ConstancesSize    = 0;
int         ConstancesMaxSize = 0;
Constance*  Constances        = NULL;

/**
 * Tablica zmiennych.
 */
int         VariablesSize     = 0;
int         VariablesMaxSize  = 0;
Variable*   Variables         = NULL;

/**
  * Wyświetla błąd alokacji i kończy program.
  */
void memoryAllocationError (void) {
    fprintf (stderr, "Memory allocation error! Program terminated!\n");
    exit (errno);
}

/**
 * @brief  Definiuje stałą.  
 * @param  nazwa stałej
 * @param  wartość stałej
 * @return 0 gdy nie ma błędu, w przeciwnym razie nr błędu 
 */
int defConst (const char* name, char * value) {
    int i;
    
    // błąd dla pustej stałej
    if (name == NULL)
        return EmptyConstanceError;
            
    // alokowanie pamięci dla pustej tablicy
    if (ConstancesMaxSize == 0) {
        Constances = (Constance*) malloc (InitialConstancesSize * sizeof (Constance));
        ConstancesMaxSize = InitialConstancesSize;
        if (Constances == NULL)
            memoryAllocationError ();
    } else {
        // błąd dla dwukrotnie deklarowanej stałej (nie dotyczy "pustych" nazw uzywanych przez kompilator)
        if (strlen (name) > 0)
            for (i = 0; i < ConstancesSize; i++)
                if (!strcmp (name, Constances[i].name))
                    return DuplicatedConstanceError;
        
        // realokacja tablicy jeśli wyczerpano miejsce
        if (ConstancesSize == ConstancesMaxSize) {
            Constance* Temp = (Constance*) malloc (ConstancesMaxSize * ConstancesIncreaseFactor * sizeof (Constance));
            if (Temp == NULL)
                memoryAllocationError ();
            for (i = 0; i < ConstancesSize; i++)
                Temp[i] = Constances[i];
            free (Constances);
            Constances = Temp;
            ConstancesMaxSize *= ConstancesIncreaseFactor;
        }
    }
    
    // Zapisuje stałą w pamięci
    Constances[ConstancesSize].name  = (char*) malloc ((strlen (name)+1)  * sizeof (char));
    Constances[ConstancesSize].value = (char*) malloc ((strlen (value)+1) * sizeof (char));
    if (Constances[ConstancesSize].name == NULL || Constances[ConstancesSize].value == NULL)
        memoryAllocationError ();
    strcpy (Constances[ConstancesSize].name,  name);
    strcpy (Constances[ConstancesSize].value, value);
    Constances[ConstancesSize].used  = 0;
    ConstancesSize++;
    
    return 0;
}

/**
 * @brief  Definiuje zmienną.  
 * @param  nazwa zmiennej
 * @return 0 gdy nie ma błędu, w przeciwnym razie nr błędu 
 */
int defVar (const char* name) {
    int i;
    
    // błąd dla pustej stałej
    if (name == NULL)
        return EmptyVariableError;
        
    // błąd jeśli ist. stała o tej nazwie
    for (i = 0; i < ConstancesSize; i++)
        if (!strcmp (name, Constances[i].name))
            return DuplicatedConstanceError;
            
    // alokowanie pamięci dla pustej tablicy
    if (VariablesMaxSize == 0) {
        Variables = (Variable*) malloc (InitialVariablesSize * sizeof (Variable));
        VariablesMaxSize = InitialVariablesSize;
        if (Variables == NULL)
            memoryAllocationError ();
    } else {
        // błąd jeśli ist. zmienna o tej nazwie
        for (i = 0; i < VariablesSize; i++)
            if (!strcmp (name, Variables[i]))
                return DuplicatedVariableError;
    
        // realokacja tablicy jeśli wyczerpano miejsce
        if (VariablesSize == VariablesMaxSize) {
            Variable* Temp = (Variable*) malloc (VariablesMaxSize * VariablesIncreaseFactor * sizeof (Variable));
            if (Temp == NULL)
                memoryAllocationError ();
            for (i = 0; i < VariablesSize; i++)
                Temp[i] = Variables[i];
            free (Variables);
            Variables = Temp;
            VariablesMaxSize *= VariablesIncreaseFactor;
        }
    }
    
    // Zapisuje zmienną w pamięci
    Variables[VariablesSize] = (char*) malloc ((strlen (name)+1) * sizeof (char));
    if (Variables[VariablesSize] == NULL)
        memoryAllocationError ();
    strcpy (Variables[VariablesSize], name);
    VariablesSize++;
    
    return 0;
}

/**
 * @brief  Stałe faktycznie użyte w programie wraz z ich id w węźle oraz wartością. 
 * @return strutura stałych
 */
UsedConstances* getConstances (void) {
    UsedConstances* used = (UsedConstances*) malloc (sizeof (UsedConstances));
    int i, j, k;
    
    if (used == NULL)
        memoryAllocationError ();
    
    // oblicz ile jest używanych wartości
    used->size = 0;
    if (OptimizeConstances) {
        for (i = 0; i < ConstancesSize; i++)
            if (Constances[i].used)
                used->size++;
    } else
        used->size = ConstancesSize;
    
    used->id     = (int*) malloc (used->size * sizeof (int));
    used->value  = (char**) malloc (used->size * sizeof (char*));
    used->memory = (int*) malloc (used->size * sizeof (int));
    if (used->id == NULL || used->value == NULL || used->memory == NULL)
        memoryAllocationError ();
    
    if (OptimizeConstances) {
        // oblicz wartości
        for (i = j = 0; i < ConstancesSize; i++)
            if (Constances[i].used) {
                used->id[j]      = i;
                used->value[j++] = Constances[i].value;
            }

        // przelicz komórki pamięci
        for (i = 0; i < used->size; i++)
            used->memory[i] = -1;
        k = -1;
        for (i = 0; i < used->size; i++) {
            if (used->memory[i] < 0) {
                k++;
                for (j = 0; j < used->size; j++)
                    if (!strcmp (used->value[i], used->value[j]))
                        used->memory[j] = k;
            }
        }
        used->actualSize = k+1;
    } else {
        for (i = 0; i < ConstancesSize; i++) {
            used->id[i]     = i;
            used->value[i]  = Constances[i].value;
            used->memory[i] = i;
        }
        used->actualSize = ConstancesSize;
    }
        
    return used;
}

/**
 * @brief  Tworzy pusty węzeł parsera.
 * @return pusty węzeł
 */
ParserNode* nopN (void) {
    ParserNode* empty = (ParserNode*) malloc (sizeof (ParserNode));
    if (empty == NULL)
        memoryAllocationError ();
    
    empty->type      = nNOP;
    empty->condition = NULL;
    empty->firstArg  = NULL;
    empty->secondArg = NULL;
    empty->commands  = NULL;
    
    return empty;
}

/**
 * @brief  Zwraca węzeł błędu.
 * @return węzeł błędu
 */
ParserNode* errorN (void) {
    ParserNode* error = nopN ();
    
    error->type = nERROR;
    
    return error;
}

/**
 * @brief  Zwraca odpowiedni węzeł dla danego identyfikatora.
 * @param  identyfikator
 * @return węzeł dla znalezionego identyfikatora, pusty węzeł jeśli identyfikator nie istnieje
 */
ParserNode* resolve (const char* name) {
    int i;
    for (i = ConstancesSize-1; i >= 0; i--)
        if (!strcmp (Constances[i].name, name)) {
            Constances[i].used = 1;
            ParserNode* id = nopN ();
            id->type  = nCONST;
            id->value = i;
            return id;
        }
    for (i = 0; i < VariablesSize; i++)
        if (!strcmp (Variables[i], name)) {
            ParserNode* id = nopN ();
            id->type  = nVAR;
            id->value = i;
            return id;
        }
    return NULL;
}

/**
 * @brief  Zwraca wartość stałej dla jej ID.
 * @param  ID stałej
 * @return wartość stałej 
 */
char* getConstByID (int id) {
    return Constances[id].value;
}

/**
 * @brief  Tworzy węzeł instrukcji warunkowej.
 * @param  węzeł warunku
 * @param  węzeł bloku dla prawdy
 * @param  węzeł bloku dla fałszu
 * @return węzeł instrukcji warunkowej
 */
ParserNode* condN (ParserNode* cond, ParserNode* ifCode, ParserNode* elseCode) {
    ParserNode* node;
    
    // zwraca NULL jeśli składnik jest NULLem 
    if (cond == NULL || ifCode == NULL || elseCode == NULL)
        return NULL;
        
    node = nopN ();
    node->type      = nCOND;
    node->condition = cond;
    node->firstArg  = ifCode;
    node->secondArg = elseCode;
    
    return node;
}

/**
 * @brief  Tworzy węzeł pętli.
 * @param  węzeł warunku
 * @param  węzeł ciała pętli
 * @return węzeł pętli
 */
ParserNode* loopN (ParserNode* cond, ParserNode* code) {
    ParserNode* node;
    
    // zwraca NULL jeśli składnik jest NULLem 
    if (cond == NULL || code == NULL)
        return NULL;
        
    node = nopN ();
    node->type      = nLOOP;
    node->condition = cond;
    node->firstArg  = code;
    
    return node;
}

/**
 * @brief  Tworzy węzeł bloku poleceń.
 * @param  pierwsze polecenie do bloku
 * @return węzeł bloku poleceń
 */
ParserNode* createCommandsN (ParserNode* term) {
    ParserNode* node;
       
    node = nopN ();
    node->type      = nCOMMANDS;
    node->commands  = (ParserNode**) malloc (InitialCommandsBlockSize * sizeof (ParserNode*));
    if (node->commands == NULL)
        memoryAllocationError ();
    node->commandsSize      = 0;
    node->commandsMaxSize   = InitialCommandsBlockSize;
    
    if (term != NULL) {
        node->commands[node->commandsSize] = term;
        node->commandsSize++;
    }
    
    return node;
}

/**
 * @brief  Rozszerza węzeł bloku poleceń.
 * @param  węzeł bloku poleceń
 * @param  kolejne polecenie do bloku
 * @return węzeł bloku poleceń
 */
ParserNode* extendCommandsN (ParserNode* node, ParserNode* term) {
    // zwraca NULL jeśli składnik jest NULLem 
    if (node == NULL)
        return createCommandsN (term);
    
    // realokacja pamięci
    if (node->commandsMaxSize == node->commandsSize) {
        ParserNode** temp = (ParserNode**) malloc (node->commandsMaxSize * CommandsBlockIncrementFactor * sizeof (ParserNode*));
        int i;
        if (temp == NULL)
            memoryAllocationError ();
        for (i = 0; i < node->commandsSize; i++)
            temp[i] = node->commands[i];
        free (node->commands);
        node->commands = temp;
        node->commandsMaxSize *= CommandsBlockIncrementFactor;
    }
    
    node->commands[node->commandsSize] = term;
    node->commandsSize++;
    
    return node;
}

/** 
 * @brief  Węzeł wczytywania wartości do zmiennej.
 * @param  węzeł zmiennej
 * @return węzeł odczytu
 */
ParserNode* readN (ParserNode* term) {
    ParserNode* node;
    
    // zwraca NULL jeśli argument jest NULLem 
    if (term == NULL)
        return NULL;
    
    node = nopN ();    
    node->type     = nREAD;
    node->firstArg = term;
    
    return node;    
}

/** 
 * @brief  Węzeł wypisywania wartości ze zmiennej.
 * @param  węzeł zmiennej
 * @return węzeł zapisu
 */
ParserNode* writeN (ParserNode* term) {
    ParserNode* node;
    
    // zwraca NULL jeśli argument jest NULLem 
    if (term == NULL)
        return NULL;
    
    node = nopN ();    
    node->type     = nWRITE;
    node->firstArg = term;
    
    return node;
}

/**
 * @brief  Zwraca węzeł "instrukcji prostej".
 * @param  typ instrukcji
 * @param  argument 1
 * @param  argument 2
 * @return węzeł obliczalnej wartości 
 */
ParserNode* inlineN (NodeType type, ParserNode* term1, ParserNode* term2) {
    ParserNode* node;
    
    // zwraca NULL jeśli co najmniej jeden argument jest NULLem 
    if (term1 == NULL || term2 == NULL)
        return NULL;
    
    // stała = 1 jest używana przy dzieleniu
    if (type == nDIV || type == nMOD)
        resolve ("1");
    
    node = nopN ();
    node->type      = type;
    node->firstArg  = term1;
    node->secondArg = term2;
    
    return node;
}

/**
 * @brief  Ponownie sprawdza które stałe są używane.
 * @param  węzeł
 * param   czy korzeń
 */
void recheckConstancesFor (ParserNode* node, int root) {
    int i;//
    
    if (root)
        for (i = 0; i < ConstancesSize; i++)
            Constances[i].used = 0;
    
    if (node != NULL)
        switch (node->type) {
            case nCOND:
                recheckConstancesFor (node->condition, 0);
                recheckConstancesFor (node->firstArg, 0);
                recheckConstancesFor (node->secondArg, 0);
                break;
                
            case nLOOP:
                recheckConstancesFor (node->condition, 0);
                recheckConstancesFor (node->firstArg, 0);
                break;
                
            case nCOMMANDS:
                for (i = 0; i < node->commandsSize; i++)
                    recheckConstancesFor (node->commands[i], 0);
                break;
                
            case nASGN:
                recheckConstancesFor (node->secondArg, 0);
                break;
                
            case nDIV:
            case nMOD:
                {
                    ParserNode* one = resolve ("1");
                    recheckConstancesFor (one, 0);
                    destroyN (one);
                }
            case nPLUS:
            case nMINUS:
            case nMUL:
            case nEQL:
            case nDIFF:
            case nGRE:
            case nLESS:
            case nGEQL:
            case nLEQL:
                recheckConstancesFor (node->firstArg, 0);
                recheckConstancesFor (node->secondArg, 0);
                break;
                
            case nWRITE:
                recheckConstancesFor (node->firstArg, 0);
                break;
            case nCONST:
                //debugNodes (node);
                Constances[node->value].used = 1;
                break;
        }//
}

/**
 * @brief Rekurencyjnie dealokuje węzeł i wszystkie jego dzieci.
 */
void destroyN (ParserNode* node) {
    if (node == NULL)
        return;
    
    switch (node->type) {
        case nLOOP:
            destroyN (node->condition);
        case nREAD:
        case nWRITE:
            destroyN (node->firstArg);
            break;
            
        case nCOND:
            destroyN (node->condition);
        case nPLUS:
        case nMINUS:
        case nMUL:
        case nDIV:
        case nMOD:
        case nEQL:
        case nDIFF:
        case nLEQL:
        case nGEQL:
        case nLESS:
        case nGRE:
            destroyN (node->firstArg);
            destroyN (node->secondArg);
            break;
    }
    
    free (node);
}

/**
 * @brief  Dealokuje wszystkie zasoby zajęte podczas tworzenia węzła.
 */
void resetN () {
    int i;
    char * loader;
    for (i = 0; i < ConstancesSize; i++)
        free (Constances[i].name);
    if (Constances != NULL)
        free (Constances);
    ConstancesSize = 0;
    ConstancesMaxSize = 0;
    for (i = 0; i < VariablesSize; i++)
        free (Variables[i]);
    if (Variables != NULL)
        free (Variables);
    VariablesSize = 0;
    VariablesMaxSize = 0;
    // stałe
    defConst ("0", "0");
    defConst ("1", "1");
}

/**
 * @brief  Wyświetla strukturę węzła.
 * @param  węzeł
 */
void debugNodes (ParserNode* node) {
    char* c = NULL;
    int i;
    
    if (node == NULL)
        printf ("NULL NODE!\n");
    else switch (node->type) {
        case nCOND:
            printf ("IF (");
            debugNodes (node->condition);
            printf (")\n");
            debugNodes (node->firstArg);
            printf ("ELSE");
            debugNodes (node->secondArg);
            printf ("[IF]END\n");
            break;
        case nLOOP:
            printf ("WHILE (");
            debugNodes (node->condition);
            printf (")\n");
            debugNodes (node->firstArg);
            printf ("[WHILE]END\n");
            break;
        case nCOMMANDS:
            printf ("{\n");
            for (i = 0; i < node->commandsSize; i++)
                debugNodes (node->commands[i]);
            printf ("}\n");
            break;
        case nREAD:
            printf ("READ into ");
            debugNodes (node->firstArg);
            printf (";\n");
            break;
        case nWRITE:
            printf ("WRITE from ");
            debugNodes (node->firstArg);
            printf (";\n");
            break;
        case nCONST:
        case nVAR:
            printf ("#%d", node->value);
            break;
        case nASGN:  if (c == NULL) c = ":=";
            debugNodes (node->firstArg);
            printf (" = ");
            debugNodes (node->secondArg);
            printf (";\n");
            break;
        case nPLUS: if (c == NULL) c = "+";
        case nMINUS: if (c == NULL) c = "-";
        case nMUL: if (c == NULL) c = "*";
        case nDIV: if (c == NULL) c = "/";
        case nMOD: if (c == NULL) c = "%";
        case nEQL: if (c == NULL) c = "=";
        case nDIFF: if (c == NULL) c = "!=";
        case nGEQL: if (c == NULL) c = ">=";
        case nLEQL: if (c == NULL) c = "<=";
        case nGRE: if (c == NULL) c = ">";
        case nLESS: if (c == NULL) c = "<";
            printf ("(");
            debugNodes (node->firstArg);
            printf ("%s", c);
            debugNodes (node->secondArg);
            printf (")");
            break;
        case nERROR:
            printf ("[ERROR]");
            break;
    }
}

