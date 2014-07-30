/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */
set V;
set E within V cross V;

param Start in V;
param End in V;
param Wage{E}, >= 0;




/*  variables  */

var path{E}, integer, >= 0, <= 1;



/*  cost function  */
minimize PathWage:
    sum{(v1,v2) in E}
        Wage[v1,v2] * path[v1,v2];




/*  constraints  */

s.t. PathHasOneStartingPoint:
    sum{(v1,v2) in E: v1 = Start}
        path[v1,v2]
    = 1;

s.t. PathHasOneEndingPoint:
    sum{(v1,v2) in E: v2 = End}
        path[v1,v2]
    = 1;

s.t. IfPredecessorBelongsToPathItHasToHaveFollowerInPath{(v1,v2) in E: v2 != End}:
    path[v1,v2] <= 
    sum{(v3,v4) in E: v3 = v2}
        path[v3,v4];

s.t. PredecessorCanHaveAtMostOneFollowerInPath{(v1,v2) in E: v2 != End}:
    sum{(v3,v4) in E: v3 = v2}
        path[v3,v4]
    <= 1;

s.t. IfFollowerBelongsToPathItHasToHavePredecessorInPath{(v1,v2) in E: v1 != Start}:
    path[v1,v2] <= 
    sum{(v3,v4) in E: v4 = v1}
        path[v3,v4];

s.t. FollowerCanHaveAtMostOnePredecessorInPath{(v1,v2) in E: v1 != Start}:
    sum{(v3,v4) in E: v4 = v1}
        path[v3,v4]
    <= 1;

/*  end of model generation  */
solve;



/*  beginning of displaying results  */

printf "Path:\n";
for{(v1,v2) in E: path[v1,v2] = 1} {
	printf "(%s,%s), wage: %s\n", v1, v2, Wage[v1,v2];
}
printf "Total wage: %s\n", PathWage;

/*  end of displaying results  */
end;
