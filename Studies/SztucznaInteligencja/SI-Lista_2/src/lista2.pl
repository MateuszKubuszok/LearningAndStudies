%%%%%%%%%%%%%%%%%%%%%%%%%% zadanie 1 %%%%%%%%%%%%%%%%%%%%%%%%%%%%

/*mamy dwa fakty: na pocz¹tku wszyscy (farmer,wilk,koza,kapusta),s¹ na pierwszym brzegu,a na koncu s¹ na drugim*/
poczatek(brzeg(pierwszy,pierwszy,pierwszy,pierwszy)).
koniec(brzeg(drugi,drugi,drugi,drugi)).

ograniczenie(brzeg(FARMER,_,KOZA,_)):-
	FARMER = KOZA.
ograniczenie(brzeg(FARMER,WILK,_,KAPUSTA)):-
	FARMER = WILK,
	FARMER = KAPUSTA.

/*relacja miêdzy dwoma brzegami - z pierwszego p³yniemy na drugi i z drugiego na pierwszy*/
brzegi(pierwszy,drugi).
brzegi(drugi,pierwszy).

%przewóz(stan przed,stan po,kto p³ynie)
%FWS - farmer wyp³ywa sam
%FJZW - farmer jest z wilkiem
%FWZW - farmer wyp³ywa z wilkiem
%FWZK - farmer wyp³ywa z koz¹
%FJZK - farmer jest z koz¹
%FWZKA - farmer wyp³ywa z kapust¹
%FJZKA - farmer jest z kapust¹
przewoz(brzeg(FARMER,WILK,KOZA,KAPUSTA),brzeg(FWS,WILK,KOZA,KAPUSTA),farmer_plynie_sam) :-
	brzegi(FARMER,FWS),
	ograniczenie(brzeg(FWS,WILK,KOZA,KAPUSTA)).
przewoz(brzeg(FJZW,FJZW,KOZA,KAPUSTA),brzeg(FWZW,FWZW,KOZA,KAPUSTA),farmer_plynie_z_wilkiem) :-
	brzegi(FJZW,FWZW),
	ograniczenie(brzeg(FWZW,FWZW,KOZA,KAPUSTA)).
przewoz(brzeg(FJZK,WILK,FJZK,KAPUSTA),brzeg(FWZK,WILK,FWZK,KAPUSTA),farmer_plynie_z_koza) :-
	brzegi(FJZK,FWZK).
przewoz(brzeg(FJZKA,WILK,KOZA,FJZKA),brzeg(FWZKA,WILK,KOZA,FWZKA),farmer_plynie_z_kapusta) :-
	brzegi(FJZKA,FWZKA),
	ograniczenie(brzeg(FWZKA,WILK,KOZA,FWZKA)).

rozwiaz([StanKoncowy|_],StanKoncowy,[]).
rozwiaz([StanWejsciowy|A],Stan,[PRZEWOZENIE|B]):-
	przewoz(StanWejsciowy,StanWyjsciowy,PRZEWOZENIE),
	\+ member(StanWyjsciowy,[StanWejsciowy|A]),
	rozwiaz([StanWyjsciowy,StanWejsciowy|A],Stan,B).


zadanie1(PRZEWOZENIE) :-
	poczatek(StanPoczatkowy),
	koniec(StanKoncowy),
	rozwiaz([StanPoczatkowy],StanKoncowy,PRZEWOZENIE).




%%%%%%%%%%%%%%%%%%%%%%%%%% zadanie 2 %%%%%%%%%%%%%%%%%%%%%%%%%%%%
poprawne(brzeg(MISJONARZE,LUDOZERCY)):-
	MISJONARZE >= LUDOZERCY,
	LUDOZERCY >= 0.
poprawne(brzeg(0,LUDOZERCY)):-
	LUDOZERCY >= 0.

%M1 - misjonarze na pierwszym brzegu
%M2 - misjonarze na drugim brzegu
%L1 - ludo¿ercy na pierwszym brzegu
%L2 - ludo¿ercy na drugim brzegu
przepraw(brzeg(2,0),brzeg(M2,LUDOZERCY),pierwszy,wszystkich(MISJONARZE,LUDOZERCY),Poprzednia,[lodka(misjonarz,misjonarz)]):-
	Poprzednia \= lodka(misjonarz,misjonarz),
	M2 is MISJONARZE-2.
przepraw(brzeg(0,2),brzeg(MISJONARZE,L2),pierwszy,wszystkich(MISJONARZE,LUDOZERCY),Poprzednia,[lodka(ludozerca,ludozerca)]):-
	Poprzednia \= lodka(ludozerca,ludozerca),
	L2 is LUDOZERCY-2.
przepraw(brzeg(1,1),brzeg(M2,L2),pierwszy,wszystkich(MISJONARZE,LUDOZERCY),Poprzednia,[lodka(misjonarz,ludozerca)]):-
	Poprzednia \= lodka(misjonarz,ludozerca),
	L2 is LUDOZERCY-1,
	M2 is MISJONARZE-1.

przepraw(brzeg(M1a,L1),brzeg(M2a,L2),pierwszy,WSZYSCY,Poprzednia,[lodka(misjonarz,misjonarz)|PRZEPRAWA]):-
      Poprzednia \= lodka(misjonarz,misjonarz),
      M1b is M1a -2,
      M2b is M2a+2,
      poprawne(brzeg(M1b,L1)),
      poprawne(brzeg(M2b,L2)),
      przepraw(brzeg(M1b,L1),brzeg(M2b,L2),drugi,WSZYSCY,lodka(misjonarz,misjonarz),PRZEPRAWA).

przepraw(brzeg(M1a,L1a),brzeg(M2a,L2a),pierwszy,WSZYSCY,Poprzednia,[lodka(misjonarz,ludozerca)|PRZEPRAWA]):-
      Poprzednia \= lodka(misjonarz,ludozerca),
      M1b is M1a-1,L1b is L1a-1,
      M2b is M2a+1,L2b is L2a+1,
      poprawne(brzeg(M1b,L1b)),
      poprawne(brzeg(M2b,L2b)),
      przepraw(brzeg(M1b,L1b),brzeg(M2b,L2b),drugi,WSZYSCY,lodka(misjonarz,ludozerca),PRZEPRAWA).

przepraw(brzeg(M1,L1a),brzeg(M2,L2a),pierwszy,WSZYSCY,Poprzednia,[lodka(ludozerca,ludozerca)|PRZEPRAWA]):-
      Poprzednia \= lodka(ludozerca,ludozerca),
      L1b is L1a-2,
      L2b is L2a+2,
      poprawne(brzeg(M1,L1b)),
      poprawne(brzeg(M2,L2b)),
      przepraw(brzeg(M1,L1b),brzeg(M2,L2b),drugi,WSZYSCY,lodka(ludozerca,ludozerca),PRZEPRAWA).

przepraw(brzeg(M1a,L1),brzeg(M2a,L2),drugi,WSZYSCY,_,[lodka(misjonarz,nikt)|PRZEPRAWA]):-
	M2b is M2a-1,
	M1b is M1a+1,
	poprawne(brzeg(M2b,L2)),
	poprawne(brzeg(M1b,L1)),
	przepraw(brzeg(M1b,L1),brzeg(M2b,L2),pierwszy,WSZYSCY,lodka(misjonarz,nikt),PRZEPRAWA).

przepraw(brzeg(M1,L1a),brzeg(M2,L2a),drugi,WSZYSCY,_,[lodka(ludozerca,nikt)|PRZEPRAWA]):-
	L2b is L2a-1,
	L1b is L1a+1,
	poprawne(brzeg(M2,L2b)),
	poprawne(brzeg(M1,L1b)),
	przepraw(brzeg(M1,L1b),brzeg(M2,L2b),pierwszy,WSZYSCY,lodka(ludozerca,nikt),PRZEPRAWA).

przepraw(brzeg(M1a,L1),brzeg(M2a,L2),drugi,WSZYSCY,Poprzednia,[lodka(misjonarz,misjonarz)|PRZEPRAWA]):-
	Poprzednia \= lodka(misjonarz,misjonarz),
	M2b is M2a-2,
	M1b is M1a+2,
	poprawne(brzeg(M2b,L2)),
	poprawne(brzeg(M1b,L1)),
	przepraw(brzeg(M1b,L1),brzeg(M2b,L2),pierwszy,WSZYSCY,lodka(misjonarz,misjonarz),PRZEPRAWA).

przepraw(brzeg(M1a,L1a),brzeg(M2a,L2a),drugi,WSZYSCY,Poprzednia,[lodka(misjonarz,ludozerca)|PRZEPRAWA]):-
	Poprzednia \= lodka(misjonarz,ludozerca),
	M2b is M2a-1,L2b is L2a-1,
	M1b is M1a+1,L1b is L1a+1,
	poprawne(brzeg(M2b,L2b)),
	poprawne(brzeg(M1b,L1b)),
	przepraw(brzeg(M1b,L1b),brzeg(M2b,L2b),pierwszy,WSZYSCY,lodka(misjonarz,ludozerca),PRZEPRAWA).

przepraw(brzeg(M1,L1a),brzeg(M2,L2a),drugi,WSZYSCY,Poprzednia,[lodka(ludozerca,ludozerca)|PRZEPRAWA]):-
	Poprzednia \= lodka(ludozerca,ludozerca),
	L2b is L2a-2,
	L1b is L1a+2,
	poprawne(brzeg(M2,L2b)),
	poprawne(brzeg(M1,L1b)),
	przepraw(brzeg(M1,L1b),brzeg(M2,L2b),pierwszy,WSZYSCY,lodka(ludozerca,ludozerca),PRZEPRAWA).

zadanie2(PRZEPRAWA,MISJONARZE,LUDOZERCY):-
	poprawne(brzeg(MISJONARZE,LUDOZERCY)),
	przepraw(brzeg(MISJONARZE,LUDOZERCY),brzeg(0,0),pierwszy,wszystkich(MISJONARZE,LUDOZERCY),lodka(nikt,nikt),PRZEPRAWA).
	
	
%%%%%%%%%%%%%%%%%%%%%%%%%% zadanie 3 %%%%%%%%%%%%%%%%%%%%%%%%%%%%

% do usuwania nawrotów (np. obrót w prawo, i z powrotem w lewo) oraz nadmiernych obrotów (np. 3 obroty w lewo zamiast 1 w prawo)
powrot(aLewo,aPrawo).
powrot(bLewo,bPrawo).
powrot(cLewo,cPrawo).
powrot(dLewo,dPrawo).
powrot(eLewo,ePrawo).
powrot(aPrawo,aLewo).
powrot(bPrawo,bLewo).
powrot(cPrawo,cLewo).
powrot(dPrawo,dLewo).
powrot(ePrawo,eLewo).

filtr([],_).
filtr(Wynik,Ruch) :-
	not(prefix([Ruch,Ruch],Wynik)),
	powrot(Ruch,Powrot),
	not(prefix([Powrot],Wynik)).

przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P2,P6,P3,P4,P1,P5,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),aLewo).
przekrec((P2,P6,P3,P4,P1,P5,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),aPrawo).
przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P4,P8,P5,P6,P3,P7,P9,P10,P11,P12,P13,P14,P15,P16),bLewo).
przekrec((P1,P2,P4,P8,P5,P6,P3,P7,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),bPrawo).
przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P7,P11,P8,P9,P6,P10,P12,P13,P14,P15,P16),cLewo).
przekrec((P1,P2,P3,P4,P5,P7,P11,P8,P9,P6,P10,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),cPrawo).
przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P10,P14,P11,P12,P9,P13,P15,P16),dLewo).
przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P10,P14,P11,P12,P9,P13,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),dPrawo).
przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P12,P16,P13,P14,P11,P15),eLewo).
przekrec((P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P11,P12,P13,P14,P15,P16),(P1,P2,P3,P4,P5,P6,P7,P8,P9,P10,P12,P16,P13,P14,P11,P15),ePrawo).

rozwiazWAHO(X,X,[]).
rozwiazWAHO(X,Y,[Ruch|Wynik]) :-
	rozwiazWAHO(Z,Y,Wynik),
	przekrec(X,Z,Ruch),
	filtr(Wynik,Ruch).
	
zadanie3(WAHO,Obroty) :-
	rozwiazWAHO(WAHO,(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),Obroty).