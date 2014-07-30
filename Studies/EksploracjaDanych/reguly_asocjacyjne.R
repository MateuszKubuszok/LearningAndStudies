#####################################################################################################
# Analiza reguł asocjacyjnych
#####################################################################################################
## Kurs: Eksploracja danych  / Data Mining
## Materiały pomocnicze do laboratorium 
## A.Suchwalko & A.Zagdanski

library(arules)

# wczytywanie danych
data("AdultUCI")
# dokładniejsze informacje na temat danych:
# http://archive.ics.uci.edu/ml/machine-learning-databases/adult/adult.names

#####################################################################################################
# usuwanie zbędnych zmiennych
AdultUCI[["fnlwgt"]] <- NULL
AdultUCI[["education-num"]] <- NULL

#####################################################################################################
# przedziałowanie zmiennych cišgłych
AdultUCI[["age"]] <- ordered(cut(AdultUCI[["age"]], c(15,25, 45, 65, 100)),
                             labels = c("Young", "Middle-aged", "Senior", "Old"))

AdultUCI[["hours-per-week"]] <- ordered(cut(AdultUCI[["hours-per-week"]],c(0, 25, 40, 60, 168)),
                                        labels = c("Part-time", "Full-time","Over-time", "Workaholic"))

AdultUCI[["capital-gain"]] <- ordered(cut(AdultUCI[["capital-gain"]],c(-Inf, 0, median(AdultUCI[["capital-gain"]][AdultUCI[["capital-gain"]] > 0]), Inf)),labels = c("None", "Low", "High"))

AdultUCI[["capital-loss"]] <- ordered(cut(AdultUCI[["capital-loss"]],c(-Inf, 0, median(AdultUCI[["capital-loss"]][AdultUCI[["capital-loss"]] > 0]), Inf)),labels = c("none", "low", "high"))

#####################################################################################################
# zamiana na dane transakcyjne
Adult <- as(AdultUCI, "transactions")

summary(Adult)

#####################################################################################################
# wykres częstoci

itemFrequencyPlot(Adult[, itemFrequency(Adult) > 0.1], cex.names = 0.8)

# porównanie częstoci: osoby o dużym dochodzie vs. rednie częstoci w populacji
Adult.largeIncome <- Adult[Adult %in% "income=large"]

itemFrequencyPlot(Adult.largeIncome[,itemFrequency(Adult.largeIncome) > 0.1])

itemFrequencyPlot(Adult.largeIncome[,itemFrequency(Adult.largeIncome) > 0.1], population = Adult[,itemFrequency(Adult.largeIncome) > 0.1])


#####################################################################################################
# znajdowanie reguł asocjacyjnych
rules <- apriori(Adult, parameter = list(support = 0.01,confidence = 0.6))

summary(rules)

# domylne ograniczenie na długoć reguły: minlen=1
# to powoduje, że otrzymujemy reguły z pustym poprzednikiem
inspect(rules[1:4])

# aby pozbyć się takich reguł nakładamy ograniczenie: minlen=2
rules <- apriori(Adult, parameter = list(support=0.01,confidence=0.6, minlen=2))

# wybieranie reguł z odpowiedniš zmiennš w następniku i odpowiednim liftem
rulesIncomeSmall <- subset(rules, subset = rhs %in% "income=small" & lift > 1.2)
rulesIncomeLarge <- subset(rules, subset = rhs %in% "income=large" & lift > 1.2)

#  wywietlanie kilku reguł
inspect(rulesIncomeLarge[1:5])
inspect(rulesIncomeSmall[1:5])

# reguły zawierajšce w poprzedniku  "age=Young" i "workclass=Private", a w następniku "income=small" 
rulesIncomeSmallYoungPrivateWork <- subset(rules, subset = lhs %ain% c("age=Young","workclass=Private") & rhs %in% "income=small" & lift > 1.3)

inspect(rulesIncomeSmallYoungPrivateWork)

# zastosowanie czeciowego dopasowania (partial matching) do wyboru reguł: 
# wybieramy reguły zawierajšce w następniku zmienne zwišzane z "marital-status"
rulesMaritalStatus <- subset(rules, subset = rhs %pin% "marital-status=")

inspect(rulesMaritalStatus[1:5])

# wywietlanie po posortowaniu
inspect(sort(rulesIncomeSmall, by = "confidence")[1:3])
inspect(sort(rulesIncomeSmall, by = "support")[1:3])
inspect(sort(rulesIncomeSmall, by = "lift")[1:3])

inspect(sort(rulesIncomeLarge, by = "confidence")[1:3])
inspect(sort(rulesIncomeLarge, by = "support")[1:3])
inspect(sort(rulesIncomeLarge, by = "lift")[1:3])

##############################################################################
# Wizualizacja reguł 
library(arulesViz)

# wykresy rozrzutu
plot(rules)
plot(rulesIncomeSmall)
plot(rulesIncomeLarge)

# wykres 'balonowy'
plot(rules, method="grouped")

# graf (tylko dla kilku reguł)
plot(rulesIncomeLarge[1:5], method="graph")
plot(rulesIncomeSmall[1:5], method="graph")