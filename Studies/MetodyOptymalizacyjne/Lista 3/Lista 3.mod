/*********************************************
 * OPL 12.4 Model
 * Author: Mateusz Kubuszok
 * Creation Date: May 25, 2013 at 8:34:13 PM
 *********************************************/


/* Parameters and helper variables */

int p = ...; assert p > 0;	/* number of resources' types */
int n = ...; assert n > 0;	/* number of jobs types */ 

range Resources = 1..p;	/* resources set */
range Jobs = 1..n;		/* jobs set */
tuple JobJob {
	int i;
	int j;
}						/* tuple used fo definning dependencies */

float Limits[Resources] = ...;			/* maximal amount of each resource */
float Requires[Jobs][Resources] = ...;	/* required amoutns of resources for each job */
int Duration[Jobs] = ...;				/* duration of each job */
{JobJob} Dependency = ...;				/* jobs dependencies */

int MaxExecutionTime = sum(j in Jobs) Duration[j];	/* upper bound of execution time */
range Times = 0..MaxExecutionTime;					/* set of considered moments in time */


/* Decision variables */

dvar boolean Active[Jobs][Times];				/* whether jobs starts at given moment */
dvar float+ ResourcesInTime[Resources][Times];	/* amounts of free resources at given moment */
dvar int+ FinishTime;							/* execution finish time */


/* Cost finction */

minimize FinishTime;


/* Constraints */

subject to {
	EachJobHasOneStart:
	forall (j in Jobs)
		sum (t in Times) Active[j][t] == 1;
		
	JobsRespectDependency:
	forall (<i,j> in Dependency)
		sum (t in Times) t*Active[j][t] + Duration[j] <= sum (t in Times) t*Active[i][t];
	FinishTimeAfterEachTask:
	forall (j in Jobs)
		sum (t in Times) t*Active[j][t] + Duration[j] <= FinishTime;
	
	ResourcesAreAlwaysWithinBounds:
	forall (r in Resources, t in Times)
		ResourcesInTime[r][t] <= Limits[r];
		
	ResourceAllocationAtStart:
	forall (r in Resources)
		ResourcesInTime[r][0] == Limits[r] - sum (j in Jobs) Active[j][0] * Requires[j][r];
	ResourceAllocationAfterStart:
	forall (r in Resources, t in 1..MaxExecutionTime)
		ResourcesInTime[r][t]
			== ResourcesInTime[r][t-1]
			+ sum (j in Jobs: t - Duration[j] >= 0) Active[j][t-Duration[j]] * Requires[j][r]
			- sum (j in Jobs) Active[j][t] * Requires[j][r];
}


/* Results preview */

execute DIAGRAM_GANTT {
	var j;
	var t;
	var r;
	var x;

	// displaying times
	for (j in Jobs)
		for (t in Times)
			if (Active[j][t] == 1)
				writeln("Job ", j, "\t\t start: ", t, "\t\t end: ", t + Duration[j]);
	writeln("Total time: ", FinishTime);
	
	// displaying Gantt's diagramme
	write("\nj");
	for (t in Times) {
		if (t <= FinishTime) {
			x = 0;
			for (j in Jobs)
				if (Active[j][t] == 1 || (t-Duration[j] >= 0 && Active[j][t-Duration[j]] == 1))
					x = 1;
			if (x == 1) 
				write("\t", t);
		}
	}	
	for (j in Jobs) {
		write("\n", j);
		for (t in Times)
			if (t <= FinishTime) {
				x = 0;
				for (var j2 in Jobs)
					if (Active[j2][t] == 1 || (t-Duration[j2] >= 0 && Active[j2][t-Duration[j2]] == 1))
						x = 1;
				if (x == 1)	
					write("\t");
				if (t-Duration[j] >= 0 && Active[j][t-Duration[j]] == 1)
					write("]");
				else if (Active[j][t] == 1)
					write("[");
			}
	}
	writeln("\n");
	
	// resoures in time
	for (r in Resources) {
		writeln("Resource: ", r);
		writeln("\tInitially: ", Limits[r]);
		for (t in Times) {
			x = 0;
			for (j in Jobs)
				if (Active[j][t] == 1 || (t-Duration[j] >= 0 && Active[j][t-Duration[j]] == 1))
					x = 1;
			if (x == 1) {
				writeln("\tAt moment ", t, ":");
				for (j in Jobs) {
					if (t-Duration[j] >= 0 && Active[j][t-Duration[j]] == 1)
						writeln("\t\tjob ", j, " freed ", Requires[j][r]);
					if (Active[j][t] == 1)
						writeln("\t\tjob ", j, " allocated ", Requires[j][r]);
				}
				writeln("\t\tresulting in free ", ResourcesInTime[r][t]);
			}
		}
		writeln("\tFinally: ", ResourcesInTime[r][FinishTime], "\n");
	}
}