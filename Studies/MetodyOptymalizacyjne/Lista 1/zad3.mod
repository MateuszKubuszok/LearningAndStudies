/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */

/*  enumeration types used in model  */
set Resources;      /*  available resources  */
set Processes;      /*  available production processes  */
set InterProducts;  /*  available intermediate products  */
set Products;       /*  available products  */

/*  enums mapping */
param ProductsToInterProducts{Products, InterProducts} within {0..1}, default 0;  /*  products mapped to intermediate products */

/*  costs  */
param ResourcesCost{Resources}, >= 0;  /*  cost of each resource  */
param ProcessCosts{Processes}, >= 0;   /*  cost of each process  */

/*  restrictions related to sulphur  */
param SulphurAmounts{Resources, Processes}, >= 0, <= 1;  /*  sulphur amount in products in percent  */
param MaxSulphurAmount{Products}, >= 0, <= 1;            /*  maximal sulphur amount in final product (home oil) in percent */

/*  restrictions related to production  */
param MinAmounts{Products}, >= 0;                                              /*  minimal required amount of each product  */
param Efficiency{Resources, InterProducts, Processes}, >= 0, <= 1, default 0;  /*  efficiency of each process for each resource and intermediate product  */



/*  variables  */

var resourcesAmount{Resources}, >= 0;             /*  amount of each resource  */

var destilation{Resources, InterProducts}, >= 0;  /*  amount of each intermediate destilation product for each resource  */

var destilateForCracking{Resources}, >= 0;        /*  how much destilate will be used for cracking  */
var destilateForHeavyOil{Resources}, >= 0;        /*  how much destilate will be used for heavy oil production  */

var oilForHomeOil{Resources}, >= 0;               /*  how much oil from destilation will be used for home oil production  */
var oilForHeavyOil{Resources}, >= 0;              /*  how much oil from destilation will be used for heavy oil production  */

var cracking{Resources, InterProducts}, >= 0;     /*  how much intemidiate product we obtain from cracking of each resource  */

var oilFromCracking{Products}, >= 0;              /*  how much products of each type we obtained from cracking  */

var sulphurInHomeOil, >= 0;                       /*  amount of sulphur in obtained home oil  */
var amountOfHomeOil, >= 0;                        /*  total amount of home oil  */



/*  cost function  */
minimize ProductionCost:
    (sum {resource in Resources}
        ResourcesCost[resource] * resourcesAmount[resource])
    +
    ProcessCosts['Destilation'] * 
    (sum {resource in Resources}
        resourcesAmount[resource])
    +
    ProcessCosts['Cracking'] *
    (sum {resource in Resources}
        destilateForCracking[resource]); 



/*  constraints  */

/*  calculate results of destilation  */
s.t. destilationProcess{resource in Resources, interProduct in InterProducts}:
    destilation[resource, interProduct] =
    resourcesAmount[resource] *
    Efficiency[resource, interProduct, 'Destilation'];

/*  divides destilate for cracking and for heavy oil production  */
s.t. destilateDivision{resource in Resources}:
    destilation[resource, 'Destilate'] =
    destilateForCracking[resource] +
    destilateForHeavyOil[resource];

/*  divides oil for home and for heavy oil production */
s.t. oilDivision{resource in Resources}:
    destilation[resource, 'Oil'] =
    oilForHomeOil[resource] +
    oilForHeavyOil[resource];

/*  calculated products for each cracking of destilates of each resource  */
s.t. crackingProcess{resource in Resources, interProduct in InterProducts}:
    cracking[resource, interProduct] =
    destilateForCracking[resource] *
    Efficiency[resource, interProduct, 'Cracking'];

/*  calculates total amount of intermediate products produced by cracking  */
s.t. meltingCrackedOils{product in Products, interProduct in InterProducts: ProductsToInterProducts[product, interProduct] = 1}:
    oilFromCracking[product] =
    sum{resource in Resources}
        cracking[resource, interProduct];

/*  ensures that amount of produced engine oil is no less than required  */
s.t. minimalEngineOilAmount:
    MinAmounts['EngineOil'] <=
    oilFromCracking['EngineOil'] +
    sum{resource in Resources}
        destilation[resource, 'Petrol'];
    
/*  ensures that amount of produced home oil is no less than required  */
s.t. minimalHomeOilAmount:
    MinAmounts['HomeOil'] <=
    oilFromCracking['HomeOil'] +
    sum{resource in Resources}
        oilForHomeOil[resource];

/*  ensures that amount of produced heavy oil is no less than required  */
s.t. minimalHeavyOilAmount:
    MinAmounts['HeavyOil'] <=
    oilFromCracking['HeavyOil'] +
    sum{resource in Resources}
        (destilation[resource, 'Leftovers'] +
        destilateForHeavyOil[resource] +
        oilForHeavyOil[resource]);

/*  calculates total amount of sulphur in home oil  */
s.t. calculateSulphurInHomeOil:
    sulphurInHomeOil =
    sum{resource in Resources}
        (oilForHomeOil[resource] *
        SulphurAmounts[resource, 'Destilation'] +
        cracking[resource, 'Oil'] *
        SulphurAmounts[resource, 'Cracking']);

/*  calculates total amount of home oil  */
s.t. calculateAmountOfHomeOil:
    amountOfHomeOil = 
    oilFromCracking['HomeOil'] +
    sum{resource in Resources}
        oilForHomeOil[resource];

/*  ensures that amount of sulphur in percents is lesser than maximal allowed  */
s.t. limitedAmountOfSulphur:
    MaxSulphurAmount['HomeOil'] *
    amountOfHomeOil >=
    sulphurInHomeOil;

/*  end of model generation  */
solve;


/*  beginning of displaying results  */

printf "Required amounts of resources:\n";
for{resource in Resources} {
	printf " - %s: %s\n", resource, resourcesAmount[resource];
}
printf "Ovarall production cost = %s\n", ProductionCost;


/*  end of displaying results  */
end;
