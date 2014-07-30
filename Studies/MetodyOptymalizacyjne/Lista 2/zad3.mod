
/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */
set J;

param Duration{J}, integer, >= 0;
param Wage{J};
param Readiness{J};

param EarliestStart := min{j in J} Readiness[j];
param MaxTotalExecutionTime := EarliestStart + sum{j in J} Duration[j];
param LatestStart := MaxTotalExecutionTime - min{j in J} Duration[j];


/*  variables  */

var Time{J}, integer, >= EarliestStart, <= LatestStart;
var Order{i in J, j in J: i != j}, binary;



/*  cost function  */
minimize Cost: sum{j in J} Wage[j]*Time[j];



/*  constraints  */

s.t. StartOnlyReady{j in J}:
    Time[j] >= Readiness[j];

s.t. OfEachTwoOneMustStartEarlier{i in J, j in J: i != j}:
    Order[i,j] + Order[j,i] = 1;

s.t. ProcessAreCompleted{i in J, j in J: i != j}:
    Time[i] + Duration[i] <= Time[j] + MaxTotalExecutionTime * Order[i,j];

/*  end of model generation  */
solve;



/*  beginning of displaying results  */

printf "Job \t | Ready \t | Start \t | Finish \t | Took \t | Delay\n";
printf "---------+---------------+---------------+---------------+---------------+----------------\n";
for {j in J} {
	printf "%s \t | %s \t\t | %s \t\t | %s \t\t | %s \t\t | %s\n", j, Readiness[j], Time[j], Time[j] + Duration[j], Duration[j], Time[j] - Readiness[j];
}
printf "Total cost: %s:\n", Cost;
for {j in J} {
	printf "%s - %s (wage: %s)\n", j, Time[j]*Wage[j], Wage[j];
}

/*  end of displaying results  */
end;