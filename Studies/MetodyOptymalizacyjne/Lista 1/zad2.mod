/*
    Mateusz Kubuszok (179956)
*/

/*  beginning of model generation  */

set Companies;                                  /*  available companies  */
set Airports;                                   /*  handled airports  */
param CompanyMax{Companies}, >= 0;              /*  maximal amount of supplied oil per company  */
param AirportNeeds{Airports}, >= 0;             /*  required amount of oil per airport  */
param DeliveryCost{Airports, Companies}, >= 0;  /*  delivery cost per airport and company  */



/*  vairables  */
var delivery{Airports, Companies} >= 0;



/*  cost function  */
minimize TotalDeliveryCost:
    sum{airport in Airports, company in Companies}
        DeliveryCost[airport, company] * delivery[airport, company];



/*  constraints  */

/*  ensures that each airport has enough oil  */
s.t. ensureAirportsHaveEnoughOil{airport in Airports}:
    AirportNeeds[airport] <= sum{company in Companies} delivery[airport, company];
/*  ensures that each company can supply required amount of oil  */
s.t. ensureCompaniesCanSupplyOil{company in Companies}:
    CompanyMax[company] >= sum{airport in Airports} delivery[airport, company];



/*  end of model generation  */
solve;



/*  beginning of displaying results  */

printf "Overall cost = %s\n", TotalDeliveryCost;
for{airport in Airports} {
	for{company in Companies} {
		printf "Airport %s takes from company %s: %s gallons of oil\n", airport, company, delivery[airport, company];
	}
}

/*  end of displaying results  */
end;
