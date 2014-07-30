#include<usb1287def.inc>

.cseg
.org 0x0      rjmp Ares
.org OC0Aaddr rjmp ZapalDiodeZielona
.org OC0Baddr rjmp ZapalDiodeCzerwona



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



/*
Skalowanie timera wzg. zegara systemowego:
+------+---------------------+--------------------------+
| CSnx | n = 0               | n = 2                    |
+------+---------------------+--------------------------+
| 000  |              timer wy��czony                   |
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
ldi R16, ((1<<COM0A0)|(1<<COM0B0))
out TCCR0A, R16
ldi R16, ((1<<CS01)|(1<<CS00))
out TCCR0B, R16

/* Obs�uga przerwa�: */
ldi R16, ((1<<OCIE0A)|(1<<OCIE0B))
sts TIMSK0, R16 // Ustawia obs�ug� przewa� dla Timer0/compare
sei // uruchomienie obs�ugi przerwa�



/* Zdejmujemy rejestry ze stosu w odwrotnej kolejno�ci do tej w jakiej je na nim umieszczali�my. */
pop R17
pop R16



ldi R17, 0

/* Petla g��wna */
PetlaGlowna:
sbis PINE, 5
rcall Dol
sbis PINB, 7
rcall Gora
rcall Czekaj
rjmp PetlaGlowna


/* Akcja wychylenia joysticka w d� -  wzmocnienie R17. */
Dol:
cpi R17, 0xFF
breq ZakonczDol
inc R17
ldi R16, 0
out OCR0A, R16
out OCR0B, R17
ZakonczDol:
ret


/* Akcja wychylenia joysticka w g�r� - os�abienie R17. */
Gora:
cpi R17, 0x00
breq ZakonczGora
dec R17
ldi R16, 0
out OCR0A, R16
out OCR0B, R17
ZakonczGora:
ret





/* Przerwanie zapalaj�ce zielon� diod� */
ZapalDiodeZielona:
// zapal diod�
ldi R20, 0x20
cpi R17, 255
breq PominZapalanieZielonego
rcall Wykonaj
PominZapalanieZielonego:
reti


/* Przerwanie zapalajace czerwon� diod� */
ZapalDiodeCzerwona:
// zapal diod�
ldi R20, 0x10
cpi R17, 0
breq PominZapalanieCzerwonego
rcall Wykonaj
PominZapalanieCzerwonego:
reti






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
CzekajPetla2:
ldi R17, 0x0A
CzekajPetla1:
nop
dec R17
cpi R17, 0x00
brne CzekajPetla1
dec R16
cpi R16, 0x00
brne CzekajPetla2

pop R17
pop R16
ret
