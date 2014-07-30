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
ldi R18, 0b00000101 // liczba n
Restart:
ldi R19, 0b10000000 // 1 na pierwszej pozycji
ldi R20, 0x00       // 0 - do porównañ


/* Wspóltworzy pêtlê sprawdzaj¹c¹ przyciski - ka¿de z rozga³êzieñ
   ostatecznie powraca do SprawdxPrzycisk (chyba, ¿e zakoñczono program). */
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
ldi R17, 0b01000000  // ustawia górn¹ zielon¹ diodê do zapalenia
brcc WykonajGora // jeœli flaga carry jest pusta (nie dotar³o do niej przesuniêciami zainicjalizowane 1 z 0b10000000) omiñ instrukcjê zakoñczenia programu
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
ldi R17, 0b00100000 // ustawia doln¹ zielon¹ diodê do zapalenia
brcc WykonajDol // jeœli flaga carry jest pusta (nie dotar³o do niej przesuniêciami zainicjalizowane 1 z 0b10000000) omiñ instrukcjê zakoñczenia programu
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
cpse R19, R20       // jeœli usuniêto wszystkie bity, ustawia 1 na pierwszej pozycji
rjmp NieZapalajBitu
	ldi R19, 0b10000000
NieZapalajBitu:
ldi R17, 0b00010000 // zapala doln¹ czerwon¹ diodê
rcall Wykonaj
ZablokujLewo:
	rcall Czekaj
	sbis PINB, 6
rjmp ZablokujLewo
rjmp SprawdzPrzycisk


/* Restart - cofniêcie wszystkich wprowadzeñ */
Prawo:
ldi R17, 0b10000000 // zapal górn¹ czerwon¹ diodê
rcall Wykonaj
ZablokujPrawo:
	rcall Czekaj
	sbis PINE, 4
rjmp ZablokujPrawo
ldi R17, 0x00
rcall Wykonaj      // zgaœ wszystkie diody
rjmp Restart


/* Gasi wszystkie diody */
Gasimy:
ldi R17, 0x00
rcall Wykonaj
rjmp SprawdzPrzycisk


/* Zapala wybrane diody (ustalone w R17) */
Wykonaj:
push R16
in R16, PORTD//or, aby oszczedziæ orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R17 
out PORTD, R16
pop R16
ret


/* Konczy program i zapala odp. czerwone b¹dŸ zielone diody */
Zakoncz:
ldi R17, 0b01100000 // zapal zielone
	cpse R18, R19       // jeœli wprowadzona liczba i n siê ró¿ni¹, zapala czerowne
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
