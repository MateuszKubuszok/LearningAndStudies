#include<usb1287def.inc>

.cseg
.org 0x0      rjmp Ares
.org OC2Baddr rjmp PodmienLiczbe
.org OC0Aaddr rjmp ZapalDiodeZielona
.org OC0Baddr rjmp ZapalDiodeCzerwona



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



/*
Skalowanie timera wzg. zegara systemowego:
+------+---------------------+--------------------------+
| CSnx | n = 0               | n = 2                    |
+------+---------------------+--------------------------+
| 000  |              timer wy³¹czony                   |
| 001  | timer systemowy     | systemowy/asynchroniczny |
| 010  |                  clock/8                       |
| 011  | clock/64            | clock/32                 |
| 100  | clock/256           | clock/64                 |
| 101  | clock/1024          | clock/128                |
| 110  | Pin Tx rising edge  | clock/256                |
| 111  | Pin Tx falling edge | clock/1024               |
+------+---------------------+--------------------------+
*/

/* Ustawienie: Timer0 */
ldi R16, ( (0<<COM0A0)|(0<<COM0B0)|(1<<WGM01)|(0<<WGM00) )
out TCCR0A, R16
ldi R16, ( (0<<WGM02)|(0<<CS02)|(1<<CS01)|(0<<CS00) )
out TCCR0B, R16

/* Ustawienie: Timer2 */
ldi R16, (1<<COM2B0)
sts TCCR2A, R16
ldi R16, (1<<CS22)|(1<<CS20)
sts TCCR2B, R16



/* Obs³uga przerwañ: */
ldi R16, (1<<OCIE0A)|(1<<OCIE0B)
sts TIMSK0, R16 // Ustawia obs³ugê przewañ dla Timer0/compare
ldi R16, (1<<OCIE2B)
sts TIMSK2, R16 // Ustawia obs³ugê przewañ dla Timer2/compare
sei // uruchomienie obs³ugi przerwañ



/* Zdejmujemy rejestry ze stosu w odwrotnej kolejnoœci do tej w jakiej je na nim umieszczaliœmy. */
pop R17
pop R16

ldi R17, 50
ldi R30, 99
out OCR0A, R30
out OCR0B, R17

/* Petla g³ówna */
PetlaGlowna:
//ldi R17, 50
rcall Losuj
rjmp PetlaGlowna


/* Przerwanie zapalaj¹ce zielon¹ diodê */
ZapalDiodeZielona:
// zapal diodê
ldi R20, 0x20
cpi R17, 99
breq PominZapalanieZielonego
rcall Wykonaj
PominZapalanieZielonego:
reti


/* Przerwanie zapalajace czerwon¹ diodê */
ZapalDiodeCzerwona:
// zapal diodê
ldi R20, 0x10
cpi R17, 0
breq PominZapalanieCzerwonego
rcall Wykonaj
PominZapalanieCzerwonego:
reti


/* Podmien wartosc */
PodmienLiczbe:
inc R22
cpi R22, 30
brne PominPodmiane

ldi R22, 0
out OCR0B, R17

PominPodmiane:
reti


/* Losuje liczbê i ustawia przerwanie */
Losuj:
push R21
push R30
push R31

ldi R21, 121
mul R17, R21
movw R30, R0 // R31:R30 = R17*121
adiw R30, 13 // R31:R30 = R17*121+13

// begin modulo dla ubogich
Odejmij:
sbiw R30, 50
brcs Dodaj50
sbiw R30, 50
brcs Dodaj100
rjmp Odejmij

Dodaj100:
adiw R30, 50
Dodaj50:
adiw R30, 50
// end modulo dla ubogich 

mov R17, R30

pop R31
pop R30
pop R21
ret



Wykonaj:
/* Gasi wszystkie diody i zapala wybrane. */
push R16
in R16, PORTD//or, aby oszczedziæ orginalne ustawienia reszty lini portu B
andi R16, 0x0F
or R16, R20 
out PORTD, R16
pop R16
ret
