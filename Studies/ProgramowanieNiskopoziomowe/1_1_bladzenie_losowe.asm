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


/* Inicjalizuje rejestry z liczbami na kt�rych b�dziemy pracowa�. */
ldi R18, 0x03 // n := 3
ldi R19, 0x00 // 0 - do por�wnia�


/* Wsp�ltworzy p�tl� sprawdzaj�c� przyciski - ka�de z rozga��zie�
   ostatecznie powraca do SprawdxPrzycisk (chyba, �e zako�czono program). */
SprawdzPrzycisk:
sbis PINE, 5
rjmp Dol
sbis PINB, 7
rjmp Gora
rjmp Gasimy


/* Akcja wychylenia joysticka w d� - ustala zapalenie dolnej diody
   i tworzy zap�tlenie dop�ki przycisk jest wci�ni�ty. */
Dol:
ldi R17, 0x20//0b0001 0000 (dolna zielona)
rcall Wykonaj
dec R18 // n--
ZablokujDol:
rcall Czekaj
sbis PINE, 5
rjmp ZablokujDol
rjmp SprawdzPrzycisk


/* Akcja wychylenia joysticka w g�r� - ustala zapalenie g�rnej diody
   i tworzy zap�tlenie dop�ki przycisk jest wci�ni�ty. */
Gora:
ldi R17, 0x40//0b0010 0000 (g�rna zielona)
rcall Wykonaj
inc R18 // n++
ZablokujGora:
rcall Czekaj
sbis PINB, 7
rjmp ZablokujGora
rjmp SprawdzPrzycisk


/* Akcja gdy joystick NIE jest wychylony w g�r� lub w d� - gasi wszystkie diody. */
Gasimy:
ldi R17, 0x00
rcall Wykonaj
rjmp SprawdzPrzycisk


/* Gasi wszystkie diody i zapala wybrane - chyba, �e spe�niono warunek zako�czenia programu. */
Wykonaj:
/* Ustala zapelanie czerwoyhc diod je�li program ma zosta� zako�czony. */
cpse R18, R19
rjmp PominCzerwone
ldi R17, 0x90 // zapala czerwone lampki dla n = 0
PominCzerwone:
/* Gasi wszystkie diody i zapala wybrane. */
push R16
in R16, PORTD//or, aby oszczedzi� orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R17 
out PORTD, R16
pop R16
/* Dla n ~= 0 wraca, w przeciwnym razie ko�czy program. */
cpse R18, R19
ret

/* P�tla zako�czonego programu. */
Zakoncz:
rcall Czekaj
rjmp Zakoncz

/* Zamiennik sleepa */
Czekaj:
push R16
push R17
ldi R16, 0xFF
ldi R17, 0x0A
	CzekajPetlaZew:
		CzekajPetlaWew:
		nop
		dec R17
		cpse R17, R19
		rjmp CzekajPetlaWew
	dec R16
	cpse R16, R19
	rjmp CzekajPetlaZew
pop R17
pop R16
ret
