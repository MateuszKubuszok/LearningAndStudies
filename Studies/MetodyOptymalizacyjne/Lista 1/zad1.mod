/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */
param n, integer, >= 1;                                      /*  parameter n definig matrix size */

set I := {1..n};
set J := {1..n};

param A{i in I, j in J} := 1 / (i + j - 1);                  /*  A matrix definiton  */
param b{i in I} := sum{j in J} A[i,j];                       /*  b vector definition */
param p{i in I} := 1;                                        /*  perfect solution */



/*  variables  */
var x{I} >= 0;



/*  cost function  */
minimize Cost: sum{i in I} b[i] * x[i];                      /*  = tr[b]*x  */



/*  constraints  */
s.t. requirements{i in I}: sum{j in J} A[i,j] * x[j] = b[i]; /*  A*x = b  */

/*  end of model generation  */
solve;



/*  calculating values to display  */
param relativeError :=
    ( sum{i in I} ( p[i] - x[i] )^2 )^( 1/2 )
    /
    ( sum{i in I} p[i] )^( 1/2 );


/*  beginning of displaying results  */

printf "Optimal x for Ax = b:\n";
for{i in I} {
	printf "x[%s] = %s\n", i, x[i];
}
printf "Cost function value = %s\n", Cost;
printf "Relative error: %s\n", relativeError;

/*  end of displaying results  */
end;
