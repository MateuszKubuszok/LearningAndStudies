/* (c) Przemys�aw Sadowski */

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
/* Wczytuje do rejestr�w sta�e - pocz�tek i koniec pami�ci SRAM,
   ldi pozwala na pobieranie warto�ci ze sta�ych, out z rejestr�w. */
ldi R16, low(RAMEND)
ldi R17, high(RAMEND)
/* Wczytuje do port�w Stack Pointer High i Stack Pointer Low adresy z rejestr�w. */
out SPH, R17
out SPL, R16

/* Odk�ada u�ywane rejestry na stos. */
push R16
push R17

/* Zapewnia tryb wej�ciowy w liniach 5 do 7 PORTB (odblokowuje SELECT, LEFT, UP). */
ldi R16, 0b11100000//PORTB linie 5 do 7 w trybie wej�ciowym
in R17, PORTB	   //funkcja OR nie zmieni pozosta�ych bit�w PORTx
or R17, R16	       //rejestr DDRB wyzerowany, ustawienie PORTB 	
out PORTB, R17	   //w��cza podci�ganie na liniach wej�ciowych

/* Zapewnia tryb wej�ciowy w liniach 4 i 5 PORTE (odblokowuje RIGHT, DOWN). */
ldi R16, 0b00110000//PORTE linie 4 i 5 w trybie wej�ciowym
in R17, PORTE
or R17, R16
out PORTE, R17

/* Zapewnia tryb wyj�ciowy w liniach 4 do 7 DDRD (odblokowuje diody). */
ldi R16, 0b11110000//linie 4-7 portu D w trybie wyj�ciowym, 
in R17, DDRD
or R17, R16	   //zasilanie diod LED
out DDRD, R17

/* Zdejmujemy rejestry ze stosu w odwrotnej kolejno�ci do tej w jakiej je na nim umieszczali�my. */
pop R17
pop R16





ldi R17, 0xFF // intensywno�� diody zielonej, 0xFF-R17 - intensywno�� diody czerwonej
ldi R19, 0x00 // iterator

PetlaWyswietlacza:

// Zapala diod�
ldi R20, 0x20 // 0b00100000 (dolna zielona)
rcall Wykonaj

mov R18, R17
PetlaZielonejDiody:
cpi R18, 0b00000000
breq KoniecPetliZielonejDiody
nop
//rcall Czekaj
dec R18
rjmp PetlaZielonejDiody
KoniecPetliZielonejDiody:

// Zapala diod�
ldi R20, 0x10// zapala doln� czerwon� diod�
rcall Wykonaj

ldi R18, 0xFF
sub R18, R17 // R18 = 0xFF-R17
PetlaCzerwonejDiody:
cpi R18, 0b00000000
breq KoniecPetliCzerwonejDiody
nop
//rcall Czekaj
dec R18
rjmp PetlaCzerwonejDiody
KoniecPetliCzerwonejDiody:

// Sprawdza joystick raz na 0x0A p�tli wy�wietlania
cpi R19, 0b00000000
brne PominSprawdzanie
ldi R19, 0x0A
rcall SprawdzPrzycisk
PominSprawdzanie:
dec R19

rjmp PetlaWyswietlacza



/* Wsp�ltworzy p�tl� sprawdzaj�c� przyciski - ka�de z rozga��zie�
   ostatecznie powraca do SprawdxPrzycisk (chyba, �e zako�czono program). */
SprawdzPrzycisk:
sbis PINE, 5
rcall Dol
sbis PINB, 7
rcall Gora
ret


/* Akcja wychylenia joysticka w d� -  wzmocnienie R17. */
Dol:
cpi R17, 0xFF
breq ZakonczDol
inc R17
rcall Czekaj
ZakonczDol:
ret


/* Akcja wychylenia joysticka w g�r� - os�abienie R17. */
Gora:
cpi R17, 0x00
breq ZakonczGora
dec R17
rcall Czekaj
ZakonczGora:
ret


/* Gasi wszystkie diody i zapala wybrane - chyba, �e spe�niono warunek zako�czenia programu. */
Wykonaj:
/* Gasi wszystkie diody i zapala wybrane. */
push R16
in R16, PORTD//or, aby oszczedzi� orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R20 
out PORTD, R16
pop R16
ret

/* Zamiennik sleepa */
Czekaj:
push R16
push R17
ldi R16, 0xFF
CzekajPetlaZew:
ldi R17, 0x0A
CzekajPetlaWew:
nop
dec R17
cpi R17, 0x00
brne CzekajPetlaWew
dec R16
cpi R16, 0x00
brne CzekajPetlaZew
pop R17
pop R16
ret
