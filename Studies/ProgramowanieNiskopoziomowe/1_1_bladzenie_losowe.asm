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


/* Inicjalizuje rejestry z liczbami na których bêdziemy pracowaæ. */
ldi R18, 0x03 // n := 3
ldi R19, 0x00 // 0 - do porówniañ


/* Wspóltworzy pêtlê sprawdzaj¹c¹ przyciski - ka¿de z rozga³êzieñ
   ostatecznie powraca do SprawdxPrzycisk (chyba, ¿e zakoñczono program). */
SprawdzPrzycisk:
sbis PINE, 5
rjmp Dol
sbis PINB, 7
rjmp Gora
rjmp Gasimy


/* Akcja wychylenia joysticka w dó³ - ustala zapalenie dolnej diody
   i tworzy zapêtlenie dopóki przycisk jest wciœniêty. */
Dol:
ldi R17, 0x20//0b0001 0000 (dolna zielona)
rcall Wykonaj
dec R18 // n--
ZablokujDol:
rcall Czekaj
sbis PINE, 5
rjmp ZablokujDol
rjmp SprawdzPrzycisk


/* Akcja wychylenia joysticka w górê - ustala zapalenie górnej diody
   i tworzy zapêtlenie dopóki przycisk jest wciœniêty. */
Gora:
ldi R17, 0x40//0b0010 0000 (górna zielona)
rcall Wykonaj
inc R18 // n++
ZablokujGora:
rcall Czekaj
sbis PINB, 7
rjmp ZablokujGora
rjmp SprawdzPrzycisk


/* Akcja gdy joystick NIE jest wychylony w górê lub w dó³ - gasi wszystkie diody. */
Gasimy:
ldi R17, 0x00
rcall Wykonaj
rjmp SprawdzPrzycisk


/* Gasi wszystkie diody i zapala wybrane - chyba, ¿e spe³niono warunek zakoñczenia programu. */
Wykonaj:
/* Ustala zapelanie czerwoyhc diod jeœli program ma zostaæ zakoñczony. */
cpse R18, R19
rjmp PominCzerwone
ldi R17, 0x90 // zapala czerwone lampki dla n = 0
PominCzerwone:
/* Gasi wszystkie diody i zapala wybrane. */
push R16
in R16, PORTD//or, aby oszczedziæ orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R17 
out PORTD, R16
pop R16
/* Dla n ~= 0 wraca, w przeciwnym razie koñczy program. */
cpse R18, R19
ret

/* Pêtla zakoñczonego programu. */
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
