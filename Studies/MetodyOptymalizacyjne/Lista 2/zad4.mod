/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */
set J;
set M;
set Followings := setof { (i,j) in J cross J: i != j } (i,j);
set Placements := setof { (j,m) in J cross M } (j,m);

param Duration{J}, integer, >= 0;
param Dependency{Followings}, binary, default 0;
param MaxExecutionTime := sum{j in J} Duration[j];


/*  variables  */

var Time{Placements}, integer, >= 0;
var Executor{Placements}, binary;
var Order{Followings, M}, binary;
var Completion, integer, >= 0;



/*  cost function  */
minimize CompletionTime: Completion;



/*  constraints  */

s.t. JobsWillFinish{(j,m) in Placements}:
	Time[j,m] + Duration[j] <= Completion;

s.t. OnlyOneExecutor{j in J}:
	sum{m in M} Executor[j,m] = 1;

s.t. OrderDefined{(i,j) in Followings}:
	sum{m in M} Order[i,j,m] <= 1;

s.t. OrderPreserved{(i,j) in Followings: Dependency[i,j] = 1}:
	sum{m1 in M} Time[i,m1] >= sum{m2 in M} (Time[j,m2] + Duration[j]*Executor[j,m2]);

s.t. JobsDontCollide1{(i,j) in Followings, m in M}:
	Time[i,m] + MaxExecutionTime*(1 - Order[i,j,m]) >= Time[j,m] + Duration[j]*Executor[j,m];
s.t. JobsDontCollide2{(i,j) in Followings, m in M}:
	Time[j,m] + MaxExecutionTime*(    Order[i,j,m]) >= Time[i,m] + Duration[i]*Executor[i,m];

/*  end of model generation  */
solve;



/*  beginning of displaying results  */

printf "Jobs \t | Start \t | End \t\t | Machine\n";
printf "---------+---------------+---------------+----------------\n";
for{j in J, m in M: Executor[j,m] = 1}{
	printf "%s \t | %s \t\t | %s \t\t | %d\n", j, Time[j,m], Time[j,m]+Duration[j], m;
}
printf "Total time: %s\n", CompletionTime;

printf "M:";
for {t in {0..Completion}} { printf "\t%s", t; }
for {m in M} {
	printf "\n%s: ", m;
	for {t in {0..Completion}} {
		printf "\t";
		for {j in J: Executor[j,m] = 1 and                   t = Time[j,m] + Duration[j]} { printf "]"; }
		for {j in J: Executor[j,m] = 1 and Time[j,m] < t and t < Time[j,m] + Duration[j]} { printf "%s", j; }
		for {j in J: Executor[j,m] = 1 and Time[j,m] = t}                                 { printf "[ %s", j; }
	}
}
printf "\n";

/*  end of displaying results  */
end;
