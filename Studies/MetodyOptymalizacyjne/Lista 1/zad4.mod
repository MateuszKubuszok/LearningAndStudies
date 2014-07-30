/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */

set Choice := {0..1};

/*  enumeration types used in model  */
set Subjects;     /*  available subjects  */
set Groups;       /*  available groups  */
set SportGroups;  /*  sport groups  */

/*  non-overlaping time intervals used for time allocation  */
set Days;                                                 /*  possible days  */
set Hours;                                                /*  non-overlaping time intervals  */
param HourDuration{Hours}, >= 0, default 1;               /*  duration of each interval  */
param DaysToHours{Hours, Days} within Choice, default 0;  /*  days to hours mapping  */

/*  restrictions related to academic activities  */
param PlanAllocation{Hours, Groups, Subjects} within Choice, default 0; /*  hours allocated for each group  */
param Ranks{Groups, Subjects}, integer, >= 0, <= 10;                    /*  groups' ranks */
param MaxHoursDaily, >= 0;                                              /*  maximal hours to attend daily  */

/*  restrictions related to sport activities  */
param SportGroupsAllocation{Hours, SportGroups} within Choice, default 0; /*  hours allocated for each sport group  */
param MinimalSportGroupActivities, >= 0;                                  /*  minimal amount of sport activities to attend weekly  */

/*  restrictions related to require free time in some intervals  */
param PotenciallyFreeHours{Hours} within Choice, default 0;  /*  hours that should be considered to be free  */
param FreeHoursDuration{Days}, >= 0;                         /*  lengths of considered intervals for each day  */
param RequiredFreeHoursDaily, >= 0;                          /*  miminal amount of free hours during considered intervals  */



/*  variables  */
var signed{Groups, Subjects}, integer, >= 0, <= 1;  /*  groups student signed up to  */
var signedSport{SportGroups}, integer, >= 0, <= 1;  /*  sport groups student signed up to  */



/*  cost function  */
maximize PlanRanks:
    sum{subject in Subjects, group in Groups}
        Ranks[group, subject] * signed[group, subject];



/*  constraints  */

/*  ensures that student is signed up to one group per subject  */
s.t. oneGroupPerSubject{subject in Subjects}:
    sum{group in Groups}
        signed[group, subject] = 1;

/*  ensures that no activity collides with another */
s.t. nonOverlappingHoursAllocation{hour in Hours}:
    1 >=
    (sum{subject in Subjects, group in Groups: PlanAllocation[hour, group, subject] = 1}
        signed[group, subject])
    +
    (sum{sportGroup in SportGroups: SportGroupsAllocation[hour, sportGroup] = 1}
        signedSport[sportGroup]);

/*  ensures that academic subject takes no more that some defined amount of time  */
s.t. noMoreThanMaxHoursDaily{day in Days}:
    MaxHoursDaily >= 
    sum{hour in Hours: DaysToHours[hour, day] = 1}
        sum{subject in Subjects, group in Groups: PlanAllocation[hour, group, subject] = 1}
            signed[group, subject] * HourDuration[hour];

/*  ensures that in defined interval there is defined amount of free time  */
s.t. requiredFreeHoursDailyInDefinedIntervals{day in Days}:
    RequiredFreeHoursDaily <=
    FreeHoursDuration[day] -
    (sum{hour in Hours: DaysToHours[hour, day] = 1 and PotenciallyFreeHours[hour] = 1} (
        (sum{subject in Subjects, group in Groups: PlanAllocation[hour, group, subject] = 1}
            HourDuration[hour] * signed[group, subject])
        +
        (sum{sportGroup in SportGroups: SportGroupsAllocation[hour, sportGroup] = 1}
            HourDuration[hour] * signedSport[sportGroup])
    ));

/*  ensures that there is required amount of sport activities  */
s.t. requiredAmountOfSportActivities:
    MinimalSportGroupActivities <=
    sum{sportGroup in SportGroups}
        signedSport[sportGroup];

/*  end of model generation  */
solve;



/*  beginning of displaying results  */

printf "Signed to groups:\n";
for{subject in Subjects} {
    for{group in Groups: signed[group, subject] = 1} {
        printf " - %s: %s\n", subject, group;
    }
}
printf "Signed for sport groups:\n";
for{sportGroup in SportGroups: signedSport[sportGroup] = 1} {
    printf " - %s\n", sportGroup;
}
printf "Plan rank: %s\n", PlanRanks;

/*  end of displaying results  */
end;
