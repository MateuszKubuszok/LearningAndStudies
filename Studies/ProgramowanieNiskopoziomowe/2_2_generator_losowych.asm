/* (c) Przemys³aw Sadowski */

#include<usb1287def.inc>

.cseg
.org 0x0
rjmp Ares


/* Linie joystick'a:
PB5-SELECT
PB6-LEFT
PB7-UP
PE4-RIGHT
PE7-DOWN
Linie LED
PD4-RED1
PD5-GREEN1
PD6-RED2
PD7-GREEN2 */


Ares:
/* Wczytuje do rejestrów sta³e - pocz¹tek i koniec pamiêci SRAM,
   ldi pozwala na pobieranie wartoœci ze sta³ych, out z rejestrów. */
ldi R16, low(RAMEND)
ldi R17, high(RAMEND)
/* Wczytuje do portów Stack Pointer High i Stack Pointer Low adresy z rejestrów. */
out SPH, R17
out SPL, R16

/* Odk³ada u¿ywane rejestry na stos. */
push R16
push R17

/* Zapewnia tryb wejœciowy w liniach 5 do 7 PORTB (odblokowuje SELECT, LEFT, UP). */
ldi R16, 0b11100000//PORTB linie 5 do 7 w trybie wejœciowym
in R17, PORTB	   //funkcja OR nie zmieni pozosta³ych bitów PORTx
or R17, R16	       //rejestr DDRB wyzerowany, ustawienie PORTB 	
out PORTB, R17	   //w³¹cza podci¹ganie na liniach wejœciowych

/* Zapewnia tryb wejœciowy w liniach 4 i 5 PORTE (odblokowuje RIGHT, DOWN). */
ldi R16, 0b00110000//PORTE linie 4 i 5 w trybie wejœciowym
in R17, PORTE
or R17, R16
out PORTE, R17

/* Zapewnia tryb wyjœciowy w liniach 4 do 7 DDRD (odblokowuje diody). */
ldi R16, 0b11110000//linie 4-7 portu D w trybie wyjœciowym, 
in R17, DDRD
or R17, R16	   //zasilanie diod LED
out DDRD, R17

/* Zdejmujemy rejestry ze stosu w odwrotnej kolejnoœci do tej w jakiej je na nim umieszczaliœmy. */

pop R17
pop R16





ldi R17, 0x0A // x0 - intensywnoœæ diody zielonej, 0x64-R17 - intensywnoœæ diody czerwonej
ldi R19, 0x00 // iterator
ldi R20, 0x00 // iterator





PetlaWyswietlacza:

cpi R17, 0x63
breq KoniecPetliZielonejDiody

// Zapala zielone diody
ldi R21, 0b10010000
rcall Wykonaj

mov R18, R17
PetlaZielonejDiody:
cpi R18, 0x00
breq KoniecPetliZielonejDiody
nop
//rcall Czekaj
dec R18
rjmp PetlaZielonejDiody
KoniecPetliZielonejDiody:


cpi R17, 0x00
breq KoniecPetliCzerwonejDiody

// Zapala czerwone diody
ldi R21, 0b01100000
rcall Wykonaj

ldi R18, 0x63 // 99
sub R18, R17 // R18 = 0x63-R17
PetlaCzerwonejDiody:
cpi R18, 0x00
breq KoniecPetliCzerwonejDiody
nop
dec R18
rjmp PetlaCzerwonejDiody
KoniecPetliCzerwonejDiody:


// Losuje now¹ liczbê
cpi R19, 0x00
brne PominSprawdzanie1
ldi R19, 0xA0
cpi R20, 0x00
brne PominSprawdzanie2
ldi R20, 0x0A
rcall Losuj
PominSprawdzanie2:
dec R20
PominSprawdzanie1:
dec R19


rjmp PetlaWyswietlacza










/* Gasi wszystkie diody i zapala wybrane - chyba, ¿e spe³niono warunek zakoñczenia programu. */
Wykonaj:
/* Gasi wszystkie diody i zapala wybrane. */
push R16
in R16, PORTD//or, aby oszczedziæ orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R21 
out PORTD, R16
pop R16
ret






Losuj:
ldi R22, 0x79
mul R17, R22
movw R30, R0 // R31:R30 = R17*121
adiw R30, 0x0D // R31:R30 = R17*121+13

Odejmij:
sbiw R30, 0x32
brcs Dodaj50
sbiw R30, 0x32
brcs Dodaj100
rjmp Odejmij

Dodaj100:
adiw R30, 0x32
Dodaj50:
adiw R30, 0x32

mov R17, R30
ret
