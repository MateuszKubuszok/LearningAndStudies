/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */
set Types; /* Allowed types */

param MaxWidth, > 0;            /* Original width of a procuded planks */
param TypeWidth{Types}, > 0;    /* Width of each type of cut planks */
param TypeRequired{Types}, > 0; /* Required amount of each type's planks */

param TypeMaxAmount{t in Types} := floor(MaxWidth/TypeWidth[t]); /* Calculates number of maximal possible amounts of each type's planks,
                                                                    made by cutting the widest plank */

set Amounts := setof {
    (t1,t2,t3) in {0..TypeMaxAmount['1']} cross {0..TypeMaxAmount['2']} cross {0..TypeMaxAmount['3']}:
    t1*TypeWidth['1'] + t2*TypeWidth['2'] + t3*TypeWidth['3'] <= MaxWidth
    and
    t1 + t2 + t3 > 0
} (t1,t2,t3); /* Creates set of all possible cuts */

param RestForDivision{(t1,t2,t3) in Amounts} := MaxWidth - t1*TypeWidth['1'] - t2*TypeWidth['2'] - t3*TypeWidth['3']; /*  Rests left from given cut */




/*  variables  */

var DivisionAmount{Amounts}, integer, >= 0; /* Amount of plans for each division */




/*  cost function  */
minimize RestAmount:
    sum{(t1,t2,t3) in Amounts}
        RestForDivision[t1,t2,t3] * DivisionAmount[t1,t2,t3];



/*  constraints  */

/* Ensures planks of each type are produced in required amounts */

s.t. ProduceRequiredAmountOfType1:
    sum{(t1,t2,t3) in Amounts}
        t1 * DivisionAmount[t1,t2,t3]
    >= TypeRequired['1'];

s.t. ProduceRequiredAmountOfType2:
    sum{(t1,t2,t3) in Amounts}
        t2 * DivisionAmount[t1,t2,t3]
    >= TypeRequired['2'];

s.t. ProduceRequiredAmountOfType3:
    sum{(t1,t2,t3) in Amounts}
        t3 * DivisionAmount[t1,t2,t3]
    >= TypeRequired['3'];

/*  end of model generation  */
solve;



/*  beginning of displaying results  */

printf "Required total amounts of planks to cut: %s\n", 
    sum{(t1,t2,t3) in Amounts}
        DivisionAmount[t1,t2,t3];

printf "Produced planks %s'': %s\n",
    TypeWidth['1'],
    sum{(t1,t2,t3) in Amounts}
        t1 * DivisionAmount[t1,t2,t3];

printf "Produced planks %s'': %s\n",
    TypeWidth['2'],
    sum{(t1,t2,t3) in Amounts}
        t2 * DivisionAmount[t1,t2,t3];

printf "Produced planks %s'': %s\n",
    TypeWidth['3'],
    sum{(t1,t2,t3) in Amounts}
        t3 * DivisionAmount[t1,t2,t3];

for{(t1,t2,t3) in Amounts: DivisionAmount[t1,t2,t3] > 0} {
    printf "Division: %s*%s'' + %s*%s'' + %s*%s'', used planks %s\n",
        t1, TypeWidth['1'],
        t2, TypeWidth['2'],
        t3, TypeWidth['3'],
        DivisionAmount[t1,t2,t3];
}

printf "Amount of unused rests: %s\n", RestAmount;

/*  end of displaying results  */
end;