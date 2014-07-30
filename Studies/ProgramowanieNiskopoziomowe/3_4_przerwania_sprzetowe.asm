#include<usb1287def.inc>

.cseg
/* Wektory przerwañ */
.org 0x0      rjmp Ares
.org INT5addr rjmp Dol
.org PCI0addr rjmp Gora
.org OC1Aaddr rjmp ZamienDiody
.org OC0Aaddr rjmp ZapalDiodeZielona
.org OC0Baddr rjmp ZapalDiodeCzerwona

/* Deklaracje sta³ych */
.equ ZielonaGorna  = 6
.equ ZielonaDolna  = 5
.equ CzerwonaGorna = 7
.equ CzerwonaDolna = 4
.equ CzasTimeraH   = 61
.equ CzasTimeraL   = 9

/* Przekazywanie wartoœci w wywo³aniach */
.def DoZapalenia     = R16
.def CzyGornaDioda   = R17
.def Skalowanie      = R18
.def PrzestawLicznik = R19
.def LiczbaLosowa    = R20
.def ObecnaLosowa    = R21

Ares:
/* Wczytuje do rejestrów sta³e - pocz¹tek i koniec pamiêci SRAM,
   ldi pozwala na pobieranie wartoœci ze sta³ych, out z rejestrów. */
ldi R16, low(RAMEND)
ldi R17, high(RAMEND)
/* Wczytuje do portów Stack Pointer High i Stack Pointer Low adresy z rejestrów. */
out SPH, R17
out SPL, R16


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
| CSnx | n = 0,1,2           | n = ??                   |
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
ldi R16, ( (0<<COM0A0)|(0<<COM0B0) | (1<<WGM01)|(0<<WGM00) )
out TCCR0A, R16
ldi R16, ( (0<<WGM02) | (0<<CS02)|(1<<CS01)|(0<<CS00) )
out TCCR0B, R16

/* Ustawienie: Timer1 */
ldi R16, CzasTimeraH  // starsze bity rejestru 16 bitowego
ldi R17, CzasTimeraL // m³odsze bity rejestru 16 bitowego
sts OCR1AH, R16
sts OCR1AL, R17
ldi R16, ( (0<<WGM11)|(0<<WGM10) )
sts TCCR1A, R16
ldi R16, ( (0<<WGM13)|(1<<WGM12) | (0<<CS12)|(1<<CS11)|(1<<CS10) )
sts TCCR1B, R16



/* Obs³uga przerwañ: */
ldi R16, (1<<OCIE0A)|(1<<OCIE0B)
sts TIMSK0, R16 // Ustawia obs³ugê przerwañ dla Timer0/compare
ldi R16, (1<<OCIE1A)
sts TIMSK1, R16 // Ustawia obs³ugê przerwañ dla Timer1/compare

ldi R16, (1<<INT5)
out EIMSK, R16 // Ustawia przerwania dla PE5

ldi R16, (1<<PCIE0)
sts PCICR, R16  // Uruchamian przerwania dla pinów PB
ldi R16, (1<<7)
sts PCMSK0, R16 // Ustawia ob³sugê przerwañ dla PB7
sei // uruchomienie obs³ugi przerwañ


ldi CzyGornaDioda, 0 // 0->dolna dioda, 1->górna dioda
ldi Skalowanie, 1    // skalowanie migania diody

ldi R31, 99
out OCR0A, R31
ldi LiczbaLosowa, 50
mov ObecnaLosowa, LiczbaLosowa
out OCR0B, LiczbaLosowa


/* Petla g³ówna */
PetlaGlowna:
rcall Losuj

sbrc PrzestawLicznik, 0
rcall UstawLicznik

sbis PINE, 5
rjmp PominResetDol
ldi R30, (1<<INT5)
out EIMSK, R30 // Ustawia przerwania dla PE5
PominResetDol:

sbis PINB, 7
rjmp PominResetGora
ldi R30, (1<<7)
sts PCMSK0, R30 // Ustawia ob³sugê przerwañ dla PB7
PominResetGora:

rjmp PetlaGlowna


/* Obluga wychylenia joysticka w dó³ */
Dol:
// wy³¹cza przerwania zewnêtrzne do czasu odblokowania ich w pêtli g³ównej
// zapobiega dwukrotnemu wywo³aniu przerwania (przy wychyleniu i puszczeniu joysticka)
push R31
ldi R31, 0
out EIMSK, R31

cpi Skalowanie, 1
breq ZablokujDol
	dec Skalowanie
	ldi PrzestawLicznik, 1
ZablokujDol:

pop R31
reti

/* Obluga wychylenia joysticka w górê */
Gora:
// wy³¹cza przerwania na zmianê pinu 7 do czasu odblokowania ich w pêtli g³ównej
// zapobiega tworzeniu siê pêtli dopóki wciœniêty jest joystick
push R31
ldi R31, 0
sts PCMSK0, R31

cpi Skalowanie, 10
breq ZablokujGora
	inc Skalowanie
	ldi PrzestawLicznik, 1
ZablokujGora:
pop R31
reti


/* Przerwanie zapalaj¹ce zielon¹ diodê */
ZapalDiodeZielona:
// zapal diodê
ldi DoZapalenia, 1<<ZielonaGorna
sbrs CzyGornaDioda, 0
ldi DoZapalenia, 1<<ZielonaDolna
cpi ObecnaLosowa, 99
breq PominZapalanieZielonego
rcall Wykonaj
PominZapalanieZielonego:
reti


/* Przerwanie zapalajace czerwon¹ diodê */
ZapalDiodeCzerwona:
// zapal diodê
ldi DoZapalenia, 1<<CzerwonaGorna
sbrs CzyGornaDioda, 0
ldi DoZapalenia, 1<<CzerwonaDolna
cpi ObecnaLosowa, 0
breq PominZapalanieCzerwonego
rcall Wykonaj
PominZapalanieCzerwonego:
reti


/* Zamieñ diody górne/dolne */
ZamienDiody:
push R31

mov ObecnaLosowa, LiczbaLosowa
out OCR0B, ObecnaLosowa
ldi R31, 1
eor CzyGornaDioda, R31

pop R31
reti


/* Losuje liczbê i ustawia przerwanie */
Losuj:
push R30
push R31

ldi R31, 121
mul LiczbaLosowa, R31
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

mov LiczbaLosowa, R30

pop R31
pop R30
ret


.def rd1l = R0 ; LSB 16-bit-number to be divided
.def rd1h = R1 ; MSB 16-bit-number to be divided
.def rd1u = R2 ; interim register
.def rd2 = R3 ; 8-bit-number to divide with
.def rel = R4 ; LSB result
.def reh = R5 ; MSB result
.def rmp = R31; multipurpose register for loading
UstawLicznik:
ldi PrzestawLicznik, 0

push rd1l
push rd1h
push rd1u
push rd2
push rel
push reh
push rmp

divstart:
;
; Load the test numbers to the appropriate registers
;
	ldi rmp, CzasTimeraH
	mov rd1h,rmp
	ldi rmp, CzasTimeraL
	mov rd1l,rmp
	mov rd2,Skalowanie
;
; Divide rd1h:rd1l by rd2
;
div8:
	clr rd1u ; clear interim register
	clr reh ; clear result (the result registers
	clr rel ; are also used to count to 16 for the
	inc rel ; division steps, is set to 1 at start)
;
; Here the division loop starts
;
div8a:
	clc ; clear carry-bit
	rol rd1l ; rotate the next-upper bit of the number
	rol rd1h ; to the interim register (multiply by 2)
	rol rd1u
	brcs div8b ; a one has rolled left, so subtract
	cp rd1u,rd2 ; Division result 1 or 0?
	brcs div8c ; jump over subtraction, if smaller
div8b:
	sub rd1u,rd2; subtract number to divide with
	sec ; set carry-bit, result is a 1
	rjmp div8d ; jump to shift of the result bit
div8c:
	clc ; clear carry-bit, resulting bit is a 0
div8d:
	rol rel ; rotate carry-bit into result registers
	rol reh
	brcc div8a ; as long as zero rotate out of the result
	 ; registers: go on with the division loop
; End of the division reached
divstop:

cli
sts OCR1AH, reh
sts OCR1AL, rel
ldi rmp, 0
sts TCNT1H, rmp
sts TCNT1L, rmp
sei

pop rmp
pop reh
pop rel
pop rd2
pop rd1u
pop rd1h
pop rd1l
ret



/* Gasi wszystkie diody i zapala wybrane. */
Wykonaj:
push R31

in R31, PORTD
andi R31, 0x0F
or R31, DoZapalenia 
out PORTD, R31

pop R31
ret


/* Zamiennik sleepa */
Czekaj:
push R26
push R27
ldi R26, 0xFF
CzekajPetlaZew:
ldi R27, 0x0A
CzekajPetlaWew:
nop
dec R27
cpi R27, 0x00
brne CzekajPetlaWew
dec R26
cpi R26, 0x00
brne CzekajPetlaZew
pop R27
pop R26
ret
