% Sets stack to a reasonable value.
set_prolog_stack(local, limit(2*10**6)).

% Loop is a list of:
% f - straight forward
% l - left turn
% r - right turn


% 1.1 - Whether or not loop is closed
closed(Loop) :-
	lastCoordinate(Loop,0,0).

lastCoordinate([],0,0).
lastCoordinate([f|Tail],X,Y) :- 
	NewY is Y+2,
	lastCoordinate(Tail,X,NewY).
lastCoordinate([l|Tail],X,Y) :-
	NewX is Y+1,
	NewY is -(X-1),
	lastCoordinate(Tail,NewX,NewY).
lastCoordinate([r|Tail],X,Y) :-
	NewX is -(Y+1),
	NewY is X+1,
	lastCoordinate(Tail,NewX,NewY).
	
	
% 1.2 - Whether or not loop is plain
plain([]).
plain(Loop) :-
	coordinates(Loop,Coordinates),
	!, not(conflicts(Coordinates)).
	
conflicts([]) :- fail.
conflicts([coordinate(0,0)|_]).
conflicts([Coordinate|OtherCoordinates]) :- member(Coordinate,OtherCoordinates); conflicts(OtherCoordinates).

coordinates([],[]).
coordinates([f|Tail],CoordinatesList) :- coordinates(up,Tail,0,1,CoordinatesList).
coordinates([l|Tail],CoordinatesList) :- coordinates(left,Tail,-1,0,CoordinatesList).
coordinates([r|Tail],CoordinatesList) :- coordinates(right,Tail,1,0,CoordinatesList).

coordinates(_,[],_,_,[]).
coordinates(Orientation,[Turn|Tail],X,Y,CoordinatesList) :-
	newCoordinate(Orientation,Turn,X,Y,NewOrientation,NewX,NewY),
	append(NextCoordinatesList,[coordinate(X,Y)],CoordinatesList),
	coordinates(NewOrientation,Tail,NewX,NewY,NextCoordinatesList).
	
newCoordinate(up,f,X,Y,up,X,NewY)      :- NewY is Y+1.
newCoordinate(up,l,X,Y,left,NewX,Y)    :- NewX is X-1.
newCoordinate(up,r,X,Y,right,NewX,Y)   :- NewX is X+1.
newCoordinate(left,f,X,Y,left,NewX,Y)  :- NewX is X-1.
newCoordinate(left,l,X,Y,down,X,NewY)  :- NewY is Y-1.
newCoordinate(left,r,X,Y,up,X,NewY)    :- NewY is Y+1.
newCoordinate(right,f,X,Y,right,NewX,Y):- NewX is X+1.
newCoordinate(right,l,X,Y,up,X,NewY)   :- NewY is Y+1.
newCoordinate(right,r,X,Y,down,X,NewY) :- NewY is Y-1.
newCoordinate(down,f,X,Y,down,X,NewY)  :- NewY is Y-1.
newCoordinate(down,l,X,Y,right,NewX,Y) :- NewX is X+1.
newCoordinate(down,r,X,Y,left,NewX,Y)  :- NewX is X-1.


% 1.3 - Whether or not loop is empty inside
compact([]).
compact(Loop) :-
	rotation(Loop,X),
	member(Y,X),
	change(Y,Reduction),
	compact(Reduction).

change([f,l,l,f|Tail],[l,l|Tail]).
change([f,r,r,f|Tail],[r,r|Tail]).
change([f,l,l,r|Tail],[l,f|Tail]).
change([f,r,r,l|Tail],[r,f|Tail]).
change([l,r,r,f|Tail],[f,r|Tail]).
change([l,r,r,l|Tail],[f,f|Tail]).
change([r,l,l,f|Tail],[f,l|Tail]).
change([r,l,l,r|Tail],[f,f|Tail]).
change([l,l,l,l],[]).
change([r,r,r,r],[]).


% 1.4 Generate loop with N turns

gen(0,[]).
gen(4,[l,l,l,l]).
gen(4,[r,r,r,r]).
gen(N,Loop) :-
	N > 4,
	NewN is N-2,
	gen(NewN,A),
	rotation(A,X),
	member(Y,X),
	change(Loop,Y),
	plain(Loop).

% 1.5 Draws
draw(N,A) :-
	free(@p),
	new(@p, picture(N)),send(@p, open),
	drastart(A,n,0,0,[]).

drastart([],n,0,0,[_|_]).

drastart([f|A],n,X,Y,L) :-
	send(@p, display,new(bitmap('pic/f0.xbm')), point(75*X,75*Y)),
	NY is Y-1,
	drastart(A,n,X,NY,[[X,Y]|L]).
drastart([f|A],s,X,Y,L) :-
	send(@p, display,new(bitmap('pic/f2.xbm')), point(75*X,75*Y)),
	NY is Y+1,
	drastart(A,s,X,NY,[[X,Y]|L]).
drastart([f|A],w,X,Y,L) :-
	send(@p, display,new(bitmap('pic/f1.xbm')), point(75*X,75*Y)),
	NX is X-1,
	drastart(A,w,NX,Y,[[X,Y]|L]).
drastart([f|A],e,X,Y,L) :-
	send(@p, display,new(bitmap('pic/f3.xbm')), point(75*X,75*Y)),
	NX is X+1,
	drastart(A,e,NX,Y,[[X,Y]|L]).

drastart([l|A],n,X,Y,L) :-
	send(@p, display,new(bitmap('pic/l0.xbm')), point(75*X,75*Y)),
	NX is X-1,
	drastart(A,w,NX,Y,[[X,Y]|L]).
drastart([l|A],s,X,Y,L) :-
	send(@p, display,new(bitmap('pic/l2.xbm')), point(75*X,75*Y)),
	NX is X+1,
	drastart(A,e,NX,Y,[[X,Y]|L]).
drastart([l|A],w,X,Y,L) :-
	send(@p, display,new(bitmap('pic/l3.xbm')), point(75*X,75*Y)),
	NY is Y+1,
	drastart(A,s,X,NY,[[X,Y]|L]).
drastart([l|A],e,X,Y,L) :-
	send(@p, display,new(bitmap('pic/l1.xbm')), point(75*X,75*Y)),
	NY is Y-1,
	drastart(A,n,X,NY,[[X,Y]|L]).

drastart([r|A],n,X,Y,L) :-
	send(@p, display,new(bitmap('pic/r0.xbm')), point(75*X,75*Y)),
	NX is X+1,
	drastart(A,e,NX,Y,[[X,Y]|L]).
drastart([r|A],s,X,Y,L) :-
	send(@p, display,new(bitmap('pic/r2.xbm')), point(75*X,75*Y)),
	NX is X-1,
	drastart(A,w,NX,Y,[[X,Y]|L]).
drastart([r|A],w,X,Y,L) :-
	send(@p, display,new(bitmap('pic/r3.xbm')), point(75*X,75*Y)),
	NY is Y-1,
	drastart(A,n,X,NY,[[X,Y]|L]).
drastart([r|A],e,X,Y,L) :-
	send(@p, display,new(bitmap('pic/r1.xbm')), point(75*X,75*Y)),
	NY is Y+1,
	drastart(A,s,X,NY,[[X,Y]|L]).


% Helpers

rotate([Head|Tail],Loop) :- append(Tail,[Head],Loop).

rotation(Original,Rotations) :-
	rotate(Rotation,Original),
	rotation(Original,Rotation,Rotations).
rotation(Original,Original,[Rotation]) :-
	rotate(Rotation,Original).
rotation(Original,Rotated,[Rotation|Tail]) :-
	Rotated \= Original,
	rotate(Rotation,Rotated),
	rotation(Original,Rotation,Tail).