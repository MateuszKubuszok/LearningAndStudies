/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.generator.c - definicje funkcji wykorzystywanych do             *
 *                         wygenerowania instrukcji z węzłów.                *
 *                                                                           *
 *  Generuje instrukcje z drzewa składni.                                    *
 *                                                                           *
 *  Rodzaje optymaliacji przeprowadzanych na tym etapie:                     *
 *   - jeśli 2 kolejne instrukcje przypisań wykorzystują te same komórki     *
 *     pamięci jako źródło, po przepermutowaniu drugiej można usunąć zbędne  *
 *     LOADy,                                                                *
 *   - analogicznie z przypisaniem oraz obliczeniem warunku if/else oraz     *
 *     obliczeniem warunku w ogóle wraz z pierwszą instrukcją po nim (blok   *
 *     while'a, bloki ifa/else'a),                                           *
 *   - usuwanie JUMPów po bloku ifa dla pustych bloków else.                 *
 *                                                                           *
 *****************************************************************************/

#include "compiler.node_builder.h"
#include "compiler.generator.h"
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>

extern FILE*    yyout;
int             errno;

/**
 * Czy ma optymalizować kod wynikowy.
 */
int OptimizeGenerated = 1;

/**
 * Zawiera id stałych faktycznie użytych w kodzie źródłowym.
 *
 * Zadeklarowane, ale nie użyte stałe są pominięte.
 * Dodane są stałe wyliczone z wyrażeń (np. zamiast a:=2*3
 * zamieniono na a:=5 a stałą 5 dodano do pamięci).
 */
UsedConstances* usedConstances;


// Deklaracje funkcji lokalnych

Instruction* createInstruction (void);
Instruction* uniI (InstructionType type, int i);
Instruction* binI (InstructionType type, int i, int j);
Instruction* mergeInstructions (Instruction* head, Instruction* tail);

Instruction* definitions (void);
Instruction* resolveNode (ParserNode* node);
Instruction* assignment (ParserNode* target, ParserNode* source);
Instruction* block (ParserNode** commands, int size);
Instruction* ifelse (ParserNode* condition, ParserNode* ifp, ParserNode* elsep);
Instruction* loop (ParserNode* condition, ParserNode* ifp);

int          resolveConst (int id);
int          resolveVar (int id);
int          resolveIdent (ParserNode* node);
Instruction* resolveCondition (ParserNode* condition, int skip);
void         resolveConditionRo (ParserNode* condition, int ro[]);
int          blockSize (Instruction* block);
int          calculatePermutations (int ro[], int ri[], int roo[], int permutation[], int removable[]);
void         swap (int * a, int * b);

/**
 * @brief Generuje kod z drzewa składniowego.
 * @param korzeń drzewa składniowego
 */
void generateCode (ParserNode* root) {
    int i;
    usedConstances = getConstances ();

    printInstructions (
        mergeInstructions (
            mergeInstructions (
                definitions (),
                resolveNode (root)
            ),
            uniI (iHALT, 0)
        )
    );
    
    free (usedConstances->id);
    free (usedConstances->value);
    free (usedConstances);
}

/**
 * @brief  Drukuje instrukcje na wyjście.
 * @param  lista instrukcji do wydrukowania
 */
void printInstructions (Instruction* instructions) {
    char*   s;
    int     a;
    int     PC = 0;

    while (instructions != NULL) {
        s = NULL;
        switch (instructions->type) {
            case iHALT:
                fprintf (yyout, "HALT\n");
                PC++;
                break;
                
            case iSET:
                fprintf (yyout, "SET %d %s\n", instructions->arg1, instructions->constVal);
                PC++;
                break;
             
            case iREAD:  if (s == NULL) s = "READ";
            case iWRITE: if (s == NULL) s = "WRITE";
            case iHALF:  if (s == NULL) s = "HALF";
                fprintf (yyout, "%s %d\n", s, instructions->arg1);
                PC++;
                break;
                
            case iLOAD:  if (s == NULL) s = "LOAD";
            case iSTORE: if (s == NULL) s = "STORE";
            case iADD:   if (s == NULL) s = "ADD";
            case iSUB:   if (s == NULL) s = "SUB";
                fprintf (yyout, "%s %d %d\n", s, instructions->arg1, instructions->arg2);
                PC++;
                break;
            
            case iJUMP:  if (s == NULL) s = "JUMP";
                fprintf (yyout, "%s %d\n", s, instructions->arg1 + PC);
                PC++;
                break;
                
            case iJZ:    if (s == NULL) s = "JZ";
            case iJG:    if (s == NULL) s = "JG"; 
            case iJODD:  if (s == NULL) s = "JODD";
                fprintf (yyout, "%s %d %d\n", s, instructions->arg1, instructions->arg2 + PC);
                PC++;
                break;
        }
        instructions = instructions->n;
    }
}

/**
 * @brief  Tworzy pustą instrukcję.
 * @return Pusta instrukcja.
 */
Instruction* createInstruction (void) {
    int i;
    Instruction* instruction = (Instruction*) malloc (sizeof (Instruction));
    if (instruction == NULL)
        memoryAllocationError ();
    instruction->type = iNOP;
    instruction->arg1 = 0;
    instruction->arg2 = 0;
    instruction->pMod = -1;
    instruction->p    = NULL;
    instruction->n    = NULL;
    instruction->constVal = NULL;
    for (i = 0; i < RegistersNumber; i++) {
        instruction->ri[i] = -1;
        instruction->ro[i] = -1;
    }
    instruction->optimization = DontOptimize;
    return instruction;
}

/**
 * @brief  Scala 2 listy instrukcji.
 * @param  pierwsza lista
 * @param  druga lista
 * @return połączone listy
 */
Instruction* mergeInstructions (Instruction* head, Instruction* tail) {
    Instruction* helper = head;
    int i, j , found;
    int permutation[RegistersNumber];// które rejestry przechodzą na które
    int removable[RegistersNumber];  // których rejestrów load można opuści w tailu
    int restore[RegistersNumber];    // do przywracania "starego" oznaczenia w instrukcjach bez skutków ubocznych w rejestrach
    
    if (head == NULL || tail == NULL)
        return head == NULL ? tail : head;   
        
    // permutuje ogon jeśli jest to potrzebne    
    if (tail->optimization && (found = calculatePermutations (head->ro, tail->ri, tail->ro, permutation, removable)))
        for (helper = tail; helper != NULL; helper = helper->n)
            switch (helper->type) {
                case iADD:
                case iSUB: helper->arg2 = permutation[helper->arg2];
                    
                case iLOAD:
                case iSTORE:
                case iHALF:
                case iJZ:
                case iJG:
                case iJODD: helper->arg1 = permutation[helper->arg1];
                    break;
            }
    
    // ustawian rejestry wyjściowe całości na rejestry wyjściowe ogona
    for (i = 0; i < RegistersNumber; i++) {
        restore[i] = head->ro[i];
        head->ro[i] = tail->ro[i]; 
    }
      
    // właściwa optymalizacja na rejestrach      
    switch (tail->optimization) {
        case Chaining:
            if (OptimizeGenerated && found) {
                helper = tail;
                        
                // maksymalna głębokość LOADów w przypisaniu to 3
                for (i = 0; i < 3; i++) {
                    if (helper->type == iLOAD && removable[helper->arg1]) {
                        if (helper == tail) {
                            tail = helper->n;
                            tail->p = NULL;
                            free (helper);
                            helper = tail;
                        } else {
                            Instruction *tmp = helper;
                            helper = helper->n;
                            tmp->n->p = tmp->p;
                            tmp->p->n = tmp->n;
                            free (tmp);
                        }   
                    } else
                        helper = helper->n;
                }
            }
            break;  
            
        case Unchanged:
            // instrukcja nie zmienia stanu rejestrów - można zachować poprzednie ustawienia\
            / o ile nie deaktualizuje to wartości rejestrów
            for (i = 0; i < RegistersNumber; i++)
                if (tail->pMod != restore[i])
                    head->ro[i] = restore[i];
                else
                    head->ro[i] = -1;
            break;
    }
    
    // koniec głowy
    helper = head;
    while (helper->n != NULL)
        helper = helper->n;
    
    // sklejanie głowy z ogonem
    helper->n = tail;
    tail->p = helper;
    return head;
}

/**
 * @brief  Instrukcja jednoargumentowa.
 * @param  adres komórki
 * @return instrukcja
 */
Instruction* uniI (InstructionType type, int i) {
    Instruction* instruction = createInstruction ();
    instruction->type = type;
    instruction->arg1 = i;
    if (type == iREAD || type == iSET)
        instruction->pMod = i;
    if (type == iWRITE || type == iREAD || type == iSET)
        instruction->optimization = Unchanged;
    return instruction;
}

/**
 * @brief  Instrukcja dwuargumentowa.
 * @param  adres komórki
 * @return instrukcja
 */
Instruction* binI (InstructionType type, int i, int j) {
    Instruction* instruction = createInstruction ();
    instruction->type = type;
    instruction->arg1 = i;
    instruction->arg2 = j;
    if (type == iREAD || type == iSET)
        instruction->pMod = i;
    if (type == iREAD || iWRITE || iSET)
        instruction->optimization = Unchanged;
    return instruction;
}

/**
 * @breif  Zwraca instrukcje alokujące wszystkie użyte stałe.
 * @return lista ustawień stałych
 */
Instruction* definitions () {
    int i, j;

    Instruction *instructions = NULL, *instruction;
     for (i = 0; i < usedConstances->actualSize; i++)
        for (j = 0; j < usedConstances->size; j++)
            if (usedConstances->memory[j] == i) {
                instruction = uniI (iSET, i);
                instruction->constVal = usedConstances->value[j];
                instructions = mergeInstructions (instructions, instruction);
                break;
            }
        
    return instructions;
}

/**
 * @brief  Generuje instrukcje dla węzła.
 * @param  adres komórki
 * @return instrukcje
 */
Instruction* resolveNode (ParserNode* node) {
    if (node == NULL)
        return NULL;
    switch (node->type) {
        case nREAD:     return uniI (iREAD, resolveVar (node->firstArg->value));
        case nWRITE:    return uniI (iWRITE, resolveIdent (node->firstArg));
        case nASGN:     return assignment (node->firstArg, node->secondArg);
        case nCOMMANDS: return block (node->commands, node->commandsSize);
        case nLOOP:     return loop (node->condition, node->firstArg);
        case nCOND:     return ifelse (node->condition, node->firstArg, node->secondArg);
        case nNOP:
        default:        return NULL;
    }
}

/**
 * @brief  Generuje instrukcje dla przypisania.
 * @param  adres komórki
 * @return instrukcje
 */
Instruction* assignment (ParserNode* target, ParserNode* source) {
    Instruction* in = NULL;
    ParserNode* one = NULL;
    
    // wynik będzie przechowany w 0
    switch (source->type) {
        case nCONST:
            in = binI (iLOAD,  0, resolveConst (source->value));
            mergeInstructions (in, binI (iSTORE, 0, resolveVar (target->value)));
            in->ri[0] = resolveConst (source->value);
            in->ro[0] = resolveVar (target->value);
            in->optimization = Chaining;
            break;
        case nVAR:
            in = binI (iLOAD,  0, resolveVar (source->value));
            mergeInstructions (in, binI (iSTORE, 0, resolveVar (target->value)));
            in->ri[0] = resolveVar (source->value);
            in->ro[0] = resolveVar (target->value);
            in->optimization = Chaining;
            break;
        case nPLUS:
        case nMINUS:
            in =                   binI (iLOAD, 0, resolveIdent (source->firstArg));
            mergeInstructions (in, binI (iLOAD, 1, resolveIdent (source->secondArg)));
            mergeInstructions (in, binI (iSUB,  2, 2));
            mergeInstructions (in, binI (iADD,  2, 0));
            mergeInstructions (in, binI (source->type == nPLUS ? iADD : iSUB, 2, 1));
            mergeInstructions (in, binI (iSTORE, 2, resolveVar (target->value)));
            in->ri[0] = resolveIdent (source->firstArg);
            in->ri[1] = resolveIdent (source->secondArg);
            if (resolveIdent (source->firstArg) != resolveVar (target->value))
                in->ro[0] = resolveIdent (source->firstArg);
            if (resolveIdent (source->secondArg) != resolveVar (target->value))
                in->ro[1] = resolveIdent (source->secondArg);
            in->ro[2] = resolveVar   (target->value);
            in->optimization = Chaining;
            break;
        case nMUL:
            in =                   binI (iLOAD,  1, resolveIdent (source->firstArg));
            mergeInstructions (in, binI (iLOAD,  2, resolveIdent (source->secondArg)));
            mergeInstructions (in, binI (iSUB,   0, 0));
            mergeInstructions (in, binI (iJZ,    2, 7));
            mergeInstructions (in, binI (iJODD,  2, 2));
            mergeInstructions (in, uniI (iJUMP,  2));
            mergeInstructions (in, binI (iADD,   0, 1));
            mergeInstructions (in, binI (iADD,   1, 1));
            mergeInstructions (in, uniI (iHALF,  2));
            mergeInstructions (in, binI (iJG,    2, -5));
            mergeInstructions (in, binI (iSTORE, 0, resolveVar (target->value)));
            if (resolveIdent (source->firstArg) != resolveVar (target->value))
                in->ri[1] = resolveIdent (source->firstArg);
            if (resolveIdent (source->secondArg) != resolveVar (target->value))
                in->ri[2] = resolveIdent (source->secondArg);        
            in->ro[0] = resolveVar (target->value);
            in->optimization = Chaining;
            break;
        case nDIV:
            one = resolve ("1");
            in  =                  binI (iLOAD,  1, resolveIdent (source->firstArg));
            mergeInstructions (in, binI (iLOAD,  2, resolveIdent (source->secondArg)));
            mergeInstructions (in, binI (iLOAD,  3, resolveIdent (one)));
            mergeInstructions (in, binI (iSUB,   0, 0));
            mergeInstructions (in, binI (iSTORE, 0, resolveVar (target->value)));
            mergeInstructions (in, binI (iJZ,    2, 20));
            mergeInstructions (in, binI (iSUB,   0, 0));
            mergeInstructions (in, binI (iADD,   0, 1));
            mergeInstructions (in, binI (iSUB,   0, 2));
            mergeInstructions (in, binI (iJZ,    0, 4));
            mergeInstructions (in, binI (iADD,   2, 2));
            mergeInstructions (in, binI (iADD,   3, 3));
            mergeInstructions (in, uniI (iJUMP,  -6));
            mergeInstructions (in, binI (iJZ,    3, 12));
            mergeInstructions (in, binI (iSUB,   0, 0));
            mergeInstructions (in, binI (iADD,   0, 2));
            mergeInstructions (in, binI (iSUB,   0, 1));
            mergeInstructions (in, binI (iJG,    0, 5));
            mergeInstructions (in, binI (iSUB,   1, 2));
            mergeInstructions (in, binI (iLOAD,  0, resolveVar (target->value)));
            mergeInstructions (in, binI (iADD,   0, 3));
            mergeInstructions (in, binI (iSTORE, 0, resolveVar (target->value)));
            mergeInstructions (in, uniI (iHALF,  3));
            mergeInstructions (in, uniI (iHALF,  2));
            mergeInstructions (in, uniI (iJUMP,  -11));
            if (resolveIdent (source->firstArg) != resolveVar (target->value))
                in->ri[1] = resolveIdent (source->firstArg);
            if (resolveIdent (source->secondArg) != resolveVar (target->value))
                in->ri[2] = resolveIdent (source->secondArg);
            in->ri[3] = resolveConst (0);
            in->ro[0] = resolveVar (target->value);
            in->optimization = Chaining;
            destroyN (one);
            break;
        case nMOD:
            one = resolve ("1");
            in  =                  binI (iLOAD,  1, resolveIdent (source->firstArg));
            mergeInstructions (in, binI (iLOAD,  2, resolveIdent (source->secondArg)));
            mergeInstructions (in, binI (iLOAD,  3, resolveIdent (one)));
            mergeInstructions (in, binI (iJG,    2, 3));
            mergeInstructions (in, binI (iSUB,   1, 1));
            mergeInstructions (in, uniI (iJUMP,  18));
            mergeInstructions (in, binI (iJZ,    2, 17));
            mergeInstructions (in, binI (iSUB,   0, 0));
            mergeInstructions (in, binI (iADD,   0, 1));
            mergeInstructions (in, binI (iSUB,   0, 2));
            mergeInstructions (in, binI (iJZ,    0, 4));
            mergeInstructions (in, binI (iADD,   2, 2));
            mergeInstructions (in, binI (iADD,   3, 3));
            mergeInstructions (in, uniI (iJUMP,  -6));
            mergeInstructions (in, binI (iJZ,    3, 9));
            mergeInstructions (in, binI (iSUB,   0, 0));
            mergeInstructions (in, binI (iADD,   0, 2));
            mergeInstructions (in, binI (iSUB,   0, 1));
            mergeInstructions (in, binI (iJG,    0, 2));
            mergeInstructions (in, binI (iSUB,   1, 2));
            mergeInstructions (in, uniI (iHALF,  3));
            mergeInstructions (in, uniI (iHALF,  2));
            mergeInstructions (in, uniI (iJUMP,  -8));
            mergeInstructions (in, binI (iSTORE, 1, resolveVar (target->value)));
            if (resolveIdent (source->firstArg) != resolveVar (target->value))
                in->ri[1] = resolveIdent (source->firstArg);
            if (resolveIdent (source->secondArg) != resolveVar (target->value))
                in->ri[2] = resolveIdent (source->secondArg);
            in->ri[3] = resolveConst (0);
            in->ro[1] = resolveVar (target->value);
            in->optimization = Chaining;
            destroyN (one);
            break;
    }
    
    return in;
}

/**
 * @brief  Generuje instrukcje dla bloku poleceń.
 * @param  polecania
 * @param  ilość poleceń
 * @return instrukcje
 */
Instruction* block (ParserNode** commands, int size) {
    Instruction* in = NULL;
    int i;
    
    for (i = 0; i < size; i++)
        in = mergeInstructions (in, resolveNode (commands[i]));
    
    return in;
}

/**
 * @brief  Generuje instrukcje dla konstrukcji if-else.
 * @param  warunek
 * @param  blok prawdy
 * @param  blok fałszu
 * @return instrukcje
 */
Instruction* ifelse (ParserNode* condition, ParserNode* ifn, ParserNode* elsen) {
    int         ro[RegistersNumber],
                i;
    Instruction *helper = createInstruction (),
                *ifin = resolveNode (ifn),
                *elsin = resolveNode (elsen);
    
    resolveConditionRo (condition, ro);        
    
    for (i = 0; i < RegistersNumber; i++)
        helper->ro[i] = ro[i];
    
    // optymalizacja bloku elsa
    elsin = mergeInstructions (helper, elsin);
    elsin = elsin->n;
    
    // przywracanie helpera do poprzedniego stanu
    helper->n = NULL;
    for (i = 0; i < RegistersNumber; i++)
        helper->ro[i] = ro[i];
    
    // optymalizacja bloku ifa
    ifin = mergeInstructions (helper, ifin);
    ifin = ifin->n;
    
    // zapobiega błędom podczas scalania bloków w jeden
    if (ifin != NULL)            
        for (i = 0; i < RegistersNumber; i++)
            ifin->ri[i] = ifin->ro[i] = -1;
    if (elsin != NULL)            
        for (i = 0; i < RegistersNumber; i++)
            elsin->ri[i] = elsin->ro[i] = -1;

    // łączenie bloku ifa elsa i warunku
    if (OptimizeGenerated && blockSize (elsin) > 0)
        mergeInstructions (ifin, uniI (iJUMP, blockSize (elsin) + 1));
    ifin = mergeInstructions (resolveCondition (condition, blockSize (ifin)), ifin);

    // chainowanie bloku - nadal powoduje błędy
    resolveConditionRo (condition, ro);
    for (i = 0; i < RegistersNumber; i++)
        ifin->ri[i] = ro[i];
    ifin->optimization = Chaining;
       
    return mergeInstructions (ifin, elsin);
}

/**
 * @brief  Generuje instrukcje dla pętli.
 * @param  warunek pętli
 * @param  ciało pętli
 * @return instrukcje
 */
Instruction* loop (ParserNode* condition, ParserNode* block) {
    Instruction *in = resolveNode (block),
                *helper = createInstruction ();
    int         i,
                ro[RegistersNumber];
    
    // optymalizacja bloku
    resolveConditionRo (condition, ro);
    for (i = 0; i < RegistersNumber; i++)
        helper->ro[i] = ro[i];
    in = mergeInstructions (helper, in);
    in = in->n;
    
    in = mergeInstructions (resolveCondition (condition, blockSize (in)+1), in);    
    return mergeInstructions (in, uniI (iJUMP, -blockSize (in)));
}

/**
 * @brief  Używany przy optymalizacji bloków do których przeskakuje instrukcja warunkowa.
 * @param  warunek
 * #param  tablica na rejestry
 */
void resolveConditionRo (ParserNode* node, int ro[]) {
    int i;
    
    for (i = 0; i < RegistersNumber; i++)
        ro[i] = -1;
   
    ro[1] = resolveIdent (node->firstArg);
    ro[2] = resolveIdent (node->secondArg);
    
    if (node->type == nLESS || node->type == nLEQL)
        swap (&ro[1], &ro[2]);
}

/**
 * @brief  Generuje instrukcje dla warunku.
 * @param  węzeł warunku
 * @param  ilość instrukcji do ominięcia
 * @return instrukcje
 */
Instruction* resolveCondition (ParserNode* node, int skip) {
    Instruction*    in = NULL;
    int             arg1 = resolveIdent (node->firstArg),
                    arg2 = resolveIdent (node->secondArg),
                    i;
                    
    switch (node->type) {
        case nEQL:
            in =                   binI (iLOAD, 1, arg1);
            mergeInstructions (in, binI (iLOAD, 2, arg2));
            mergeInstructions (in, binI (iSUB,  0, 0));
            mergeInstructions (in, binI (iADD,  0, 1));
            mergeInstructions (in, binI (iSUB,  0, 2));
            mergeInstructions (in, binI (iJG ,  0, skip + 5));
            mergeInstructions (in, binI (iSUB,  0, 0));
            mergeInstructions (in, binI (iADD,  0, 2));
            mergeInstructions (in, binI (iSUB,  0, 1));
            mergeInstructions (in, binI (iJG ,  0, skip + 1));
            break;
            
        case nDIFF:
            in =                   binI (iLOAD, 1, arg1);
            mergeInstructions (in, binI (iLOAD, 2, arg2));
            mergeInstructions (in, binI (iSUB,  0, 0));
            mergeInstructions (in, binI (iADD,  0, 1));
            mergeInstructions (in, binI (iSUB,  0, 2));
            mergeInstructions (in, binI (iJG ,  0, 6));
            mergeInstructions (in, binI (iSUB,  0, 0));
            mergeInstructions (in, binI (iADD,  0, 2));
            mergeInstructions (in, binI (iSUB,  0, 1));
            mergeInstructions (in, binI (iJG ,  0, 2));
            mergeInstructions (in, uniI (iJUMP, skip + 1));
            break;
            
        case nLESS:
            swap (&arg1, &arg2);
        case nGRE:
            in =                   binI (iLOAD, 1, arg1);
            mergeInstructions (in, binI (iLOAD, 2, arg2));
            mergeInstructions (in, binI (iSUB,  0, 0));
            mergeInstructions (in, binI (iADD,  0, 1));
            mergeInstructions (in, binI (iSUB,  0, 2));
            mergeInstructions (in, binI (iJZ ,  0, skip + 1));
            break;
            
        case nLEQL:
            swap (&arg1, &arg2);
        case nGEQL:
            in =                   binI (iLOAD, 1, arg1);
            mergeInstructions (in, binI (iLOAD, 2, arg2));
            mergeInstructions (in, binI (iSUB,  0, 0));
            mergeInstructions (in, binI (iADD,  0, 2));
            mergeInstructions (in, binI (iSUB,  0, 1));
            mergeInstructions (in, binI (iJG ,  0, skip + 1));
            break;
    }
    
    return in;
}

/**
 * @brief  Zwraca adres komórki pamięci ze stałą o podanym id.
 * @param  id stałej
 * @return adres komórki pamięci
 */
int resolveConst (int id) {
    int i;
    for (i = 0; i < usedConstances->size; i++)
        if (usedConstances->id[i] == id)
            return usedConstances->memory[i];
    return 0;
}

/**
 * @brief  Zwraca adres komórki pamięci ze zmiennej o podanym id.
 * @param  id zmiennej
 * @return adres komórki pamięci
 */
int resolveVar (int id) {
    return id + usedConstances->actualSize;
}

/**
 * @brief  Zwraca adres komórki pamięci ze stałą/zmienną.
 * @param  węzeł
 * @return adres komórki pamięci
 */
int resolveIdent (ParserNode* node) {
    return node->type == nCONST ? resolveConst (node->value) : resolveVar (node->value);
}

/**
 * @brief  Zwraca wielkość bloku instrukcji.
 * @param  lista instrukcji
 * @return ilość instrukcji
 */
int blockSize (Instruction* block) {
    int size = 0;
    
    while (block != NULL) {
        block = block->n;
        size++;
    }
    
    return size;
}

/**
 * @brief  Wyświetla wygenerowane dla bieżącego programu pamięciowe adresy stałych i zmiennych.
 */
void memoryState (void) {
    int i, j;
    
    printf ("Constances (p->v):\n");
    for (i = 0; i < usedConstances->actualSize; i++)
        for (j = 0; j < usedConstances->size; j++)
            if (usedConstances->memory[j] == i) {
                printf ("%d -> %s\n", i, usedConstances->value[j]);
                break;
            }
    
    printf ("Variables (p->n)\n");
    for (i = 0; i < VariablesSize; i++)
        printf ("%d -> %s\n", i + usedConstances->actualSize, Variables[i]);
}

/**
 * @brief  Sprawdza czy permutacja rejestrów jest konieczna, oraz czy jakieś LOADy można usunąć, jeśli tak jest ona zawarta w tablicy permutation.
 * @param  wyjście poprzednika
 * @param  wejście następnika
 * @param  wyjście następnika
 * @param  tablica na parmutację
 * @param  usuwalne rejestry
 * @return czy istnieją usuwalne rejestry
 */
int calculatePermutations (int ro[], int ri[], int roo[], int permutation[], int removable[]) {
    int i, j, found = 0;
    int helper[RegistersNumber];
    
    for (i = 0; i < RegistersNumber; i++) {
        helper[i] = i;
        removable[i] = 0;
    }
        
    // szukanie części wspólnych
    for (i = 0; i < RegistersNumber; i++)
        for (j = 0; j < RegistersNumber; j++)
            if (ro[i] == ri[j]) {
                if (i != j && ro[j] != ri[j]) {
                    swap (&ri[i],     &ri[j]);
                    swap (&roo[i],    &roo[j]);
                    swap (&helper[i], &helper[j]);
                }
                if (ro[i] >= 0) {
                    removable[i] = 1;
                    found = 1;
                }
                break;
            }
            
    // permutacja rejestrów jest permutacją odwrotną do helpera    
    for (i = 0; i < RegistersNumber; i++)
        permutation[helper[i]] = i; 
            
    return found;      
}

/**
 * @brief Zamienia dwie liczby miejscami.
 * @param pierwsza liczba
 * @param druga liczba
 */
void swap (int * a, int * b) {
    int tmp;
    tmp = *a;
    *a = *b;
    *b = tmp;
}


















