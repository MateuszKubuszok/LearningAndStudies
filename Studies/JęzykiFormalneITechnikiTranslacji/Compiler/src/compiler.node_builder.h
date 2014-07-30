/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.node_builder.h - deklaracje funkcji wykorzystywanych do         *
 *                            wygenerowania  ęzłów oraz struktur przez nie   *
 *                            wykorzystywanych.                              *
 *                                                                           *
 *****************************************************************************/

#ifndef COMPILER_NODE_BUILDER
#define COMPILER_NODE_BUILDER

#define EmptyConstanceError      1
#define DuplicatedConstanceError 2
#define NegativeConstanceError   3
#define EmptyVariableError       4
#define DuplicatedVariableError  5

/**
 * Definiuje rodzaj węzła parsera.
 */
typedef enum enumNodeType {
    nNOP,
    nERROR,
    nCONST,
    nVAR,
    nPLUS,
    nMINUS,
    nMUL,
    nDIV,
    nMOD,
    nEQL,
    nDIFF,
    nLEQL,
    nGEQL,
    nLESS,
    nGRE,
    nREAD,
    nWRITE,
    nASGN,
    nCOMMANDS,
    nCOND,
    nLOOP
} NodeType;

/**
 * Czy optymalizować deklaracje stałých.
 */
extern int OptimizeConstances;

/**
 * Węzeł parsera.
 *
 * Przechowuje sparsowany kod programu w postaci drzewa - z niego generwany będzie później kod wyjściowy.
 */
typedef struct structParserNode {
    NodeType                    type;            // przechowuje typ bieżącej struktury
    int                         value;           // dla stałych/ zmiennych: adres ich komórki pamięci
    struct structParserNode*    condition;       // dla pętli i instrukcji warunkowych: warunek
    struct structParserNode*    firstArg;        // dla warunków i wyrażeń: pierwszy argument działania; dla pętli i instrukcji warunkowych (block prawdy): blok kodu
    struct structParserNode*    secondArg;       // dla warunków i wyrażeń: drugi argument działania; dla instrukcji warunkowych (block fałszu): blok kodu
    struct structParserNode**   commands;        // dla bloku instrukcji: tablica instrukcji
    int                         commandsSize;    // dla bloku instrukcji: obecny rozmiar bloku
    int                         commandsMaxSize; // dla bloku instrukcji: maksymalny (zaalokowany) rozmiar bloku
} ParserNode;

/**
 * Użycie stałych w programie.
 */
typedef struct structUsedConstances {
    char** value;      // talica wartości (stringi)
    int*   id;         // tablica identyfikatorów
    int*   memory;     // tablica przydzielonych komórek pamięci
    int    size;       // rozmiar tablicy
    int    actualSize; // faktyczna ilość stałych
} UsedConstances;

/**
 * @brief  Definiuje stałą.  
 * @param  nazwa stałej
 * @param  wartość stałej
 * @return 0 gdy nie ma błędu, w przeciwnym razie nr błędu 
 */
int defConst (const char* name, char * value);

/**
 * @brief  Definiuje zmienną.
 * @param  nazwa stałej
 * @param  wartość stałej
 * @return 0 gdy nie ma błędu, w przeciwnym razie nr błędu 
 */
int defVar (const char* name);

/**
 * @brief  Stałe faktycznie użyte w programie wraz z ich id w węźle oraz wartością. 
 * @return strutura stałych
 */
UsedConstances* getConstances (void);

/**
 * @brief  Zwraca wartość stałej dla jej ID.
 * @param  ID stałej
 * @return wartość stałej 
 */
char* getConstByID (int id);

/**
 * @brief  Dealokuje wszystkie zasoby zajęte podczas tworzenia węzła.
 */
void resetN (); 

/**
 * @brief  Zwraca odpowiedni węzeł dla danego identyfikatora.
 * @param  identyfikator
 * @return węzeł dla znalezionego identyfikatora, pusty węzeł jeśli identyfikator nie istnieje
 */
ParserNode* resolve (const char* name);

/**
 * @brief  Tworzy węzeł instrukcji warunkowej.
 * @param  węzeł warunku
 * @param  węzeł bloku dla prawdy
 * @param  węzeł bloku dla fałszu
 * @return węzeł instrukcji warunkowej
 */
ParserNode* condN (ParserNode* cond, ParserNode* ifCode, ParserNode* elseCode);

/**
 * @brief  Tworzy węzeł pętli.
 * @param  węzeł warunku
 * @param  węzeł ciała pętli
 * @return węzeł pętli
 */
ParserNode* loopN (ParserNode* cond, ParserNode* code);

/**
 * @brief  Tworzy węzeł bloku poleceń.
 * @param  pierwsze polecenie do bloku
 * @return węzeł bloku poleceń
 */
ParserNode* createCommandsN (ParserNode* term);

/**
 * @brief  Rozszerza węzeł bloku poleceń.
 * @param  węzeł bloku poleceń
 * @param  kolejne polecenie do bloku
 * @return węzeł bloku poleceń
 */
ParserNode* extendCommandsN (ParserNode* node, ParserNode* term);

/** 
 * @brief  Węzeł wczytywania wartości do zmiennej.
 * @param  węzeł zmiennej
 * @return węzeł odczytu
 */
ParserNode* readN (ParserNode* term);

/** 
 * @brief  Węzeł wczytywania wartości ze zmiennej.
 * @param  węzeł zmiennej
 * @return węzeł zapisu
 */
ParserNode* writeN (ParserNode* term);

/**
 * @brief  Zwraca węzeł "instrukcji prostej".
 * @param  typ instrukcji
 * @param  argument 1
 * @param  argument 2
 * @return węzeł obliczalnej wartości 
 */
ParserNode* inlineN (NodeType type, ParserNode* term1, ParserNode* term2);

/**
 * @brief  Zwraca węzeł błędu.
 * @return węzeł błędu
 */
ParserNode* errorN (void);

/**
 * @brief  Zwraca węzeł pustej instrukcji.
 * @return węzeł pustej instrukcji 
 */
ParserNode* nopN (void);

/**
 * @brief  Ponownie sprawdza które stałe są używane.
 * @param  węzeł
 * @param  czy korzeń
 */
void recheckConstancesFor (ParserNode* node, int root);

/**
 * @brief Rekurencyjnie dealokuje węzeł i wszystkie jego dzieci.
 */
void destroyN (ParserNode* node);

/**
 * @brief  Wyświetla strukturę węzła.
 * @param  węzeł
 */
void debugNodes (ParserNode* node);

/**
  * Wyświetla błąd alokacji i kończy program.
  */
void memoryAllocationError (void);

/**
 * Tablica zmiennych.
 */
extern int      VariablesSize;
extern int      VariablesMaxSize;
extern char**   Variables;

#endif
