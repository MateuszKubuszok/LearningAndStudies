/*****************************************************************************
 *                                                                           *
 *  Projekt Kompilatora                                                      *
 *                                                                           *
 *  autor: Mateusz Kubuszok                                                  *
 *                                                                           *
 *  compiler.generator.h - deklaracje funkcji wykorzystywanych do            *
 *                         wygenerowania instrukcji z węzłów, oraz           *
 *                         struktur przez nie wykorzystywanych.              *
 *                                                                           *
 *****************************************************************************/

#ifndef COMPILER_GENERATOR_HEADER
#define COMPILER_GENERATOR_HEADER

#include "compiler.node_builder.h"

#define RegistersNumber 4

/**
 * Czy ma optymalizować kod wynikowy.
 */
extern int OptimizeGenerated;

/**
 * Typy instrukcji.
 */
typedef enum enumInstructionType {
    iNOP,
    iREAD,
    iWRITE,
    iSET,
    iLOAD,
    iSTORE,
    iADD,
    iSUB,
    iHALF,
    iJUMP,
    iJZ,
    iJG,
    iJODD,
    iHALT
} InstructionType;

/**
 * Rodzaj możliwej do przeprowadzenia optymalizacji na rejestrach.
 */
typedef enum enumOptimizationType {
    DontOptimize,
    Chaining,
    Unchanged
} OptimizationType;

/**
 * Instrukcja programu.
 */
typedef struct structInstruction {
    InstructionType             type;
    int                         arg1;
    int                         arg2;
    struct structInstruction*   p;
    struct structInstruction*   n;
    char*                       constVal;
    int                         ri[RegistersNumber];
    int                         ro[RegistersNumber];
    int                         pMod;
    OptimizationType            optimization;
} Instruction;

/**
 * @brief Generuje kod z drzewa składniowego.
 * @param korzeń drzewa składniowego
 */
void generateCode (ParserNode* node);

/**
 * @brief  Drukuje instrukcje na wyjście.
 * @param  lista instrukcji do wydrukowania
 */
void printInstructions (Instruction* instructions);

/**
 * @brief  Wyświetla wygenerowane dla bieżącego programu pamięciowe adresy stałych i zmiennych.
 */
void memoryState (void);
#endif
