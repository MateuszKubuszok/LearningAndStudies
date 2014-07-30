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
ldi R18, 0b00000101 // liczba n
Restart:
ldi R19, 0b10000000 // 1 na pierwszej pozycji
ldi R20, 0x00       // 0 - do por�wna�


/* Wsp�ltworzy p�tl� sprawdzaj�c� przyciski - ka�de z rozga��zie�
   ostatecznie powraca do SprawdxPrzycisk (chyba, �e zako�czono program). */
SprawdzPrzycisk:
sbis PINE, 4
rjmp Prawo
sbis PINE, 5
rjmp Dol
sbis PINB, 6
rjmp Lewo
sbis PINB, 7
rjmp Gora
rjmp Gasimy


/* Wprowadza 1 jako kolejny bit do liczby */
Gora:
lsr R19              // przesuwa bity w prawo
ori R19, 0b10000000  // zapala pierwszy bit
ldi R17, 0b01000000  // ustawia g�rn� zielon� diod� do zapalenia
brcc WykonajGora // je�li flaga carry jest pusta (nie dotar�o do niej przesuni�ciami zainicjalizowane 1 z 0b10000000) omi� instrukcj� zako�czenia programu
	rjmp Zakoncz
WykonajGora:
rcall Wykonaj
ZablokujGora:
	rcall Czekaj
	sbis PINB, 7
rjmp ZablokujGora
rjmp SprawdzPrzycisk


/* Wprowadza 0 jako kojelny bit do liczby */
Dol:
lsr R19             // przesuwa bity w prawo
ldi R17, 0b00100000 // ustawia doln� zielon� diod� do zapalenia
brcc WykonajDol // je�li flaga carry jest pusta (nie dotar�o do niej przesuni�ciami zainicjalizowane 1 z 0b10000000) omi� instrukcj� zako�czenia programu
	rjmp Zakoncz
WykonajDol:
rcall Wykonaj
ZablokujDol:
	rcall Czekaj
	sbis PINE, 5
rjmp ZablokujDol
rjmp SprawdzPrzycisk


/* Kasuje ostatnie wprowadzenie */
Lewo:
lsl R19                 // przesuwa bity w lewo
cpse R19, R20       // je�li usuni�to wszystkie bity, ustawia 1 na pierwszej pozycji
rjmp NieZapalajBitu
	ldi R19, 0b10000000
NieZapalajBitu:
ldi R17, 0b00010000 // zapala doln� czerwon� diod�
rcall Wykonaj
ZablokujLewo:
	rcall Czekaj
	sbis PINB, 6
rjmp ZablokujLewo
rjmp SprawdzPrzycisk


/* Restart - cofni�cie wszystkich wprowadze� */
Prawo:
ldi R17, 0b10000000 // zapal g�rn� czerwon� diod�
rcall Wykonaj
ZablokujPrawo:
	rcall Czekaj
	sbis PINE, 4
rjmp ZablokujPrawo
ldi R17, 0x00
rcall Wykonaj      // zga� wszystkie diody
rjmp Restart


/* Gasi wszystkie diody */
Gasimy:
ldi R17, 0x00
rcall Wykonaj
rjmp SprawdzPrzycisk


/* Zapala wybrane diody (ustalone w R17) */
Wykonaj:
push R16
in R16, PORTD//or, aby oszczedzi� orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R17 
out PORTD, R16
pop R16
ret


/* Konczy program i zapala odp. czerwone b�d� zielone diody */
Zakoncz:
ldi R17, 0b01100000 // zapal zielone
	cpse R18, R19       // je�li wprowadzona liczba i n si� r�ni�, zapala czerowne
	ldi R17, 0b10010000
rcall Wykonaj
	PetlaKoncowa:
	rcall Czekaj
	rjmp PetlaKoncowa


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
