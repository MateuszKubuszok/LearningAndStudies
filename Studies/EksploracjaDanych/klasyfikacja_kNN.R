##########################################################################################
# KLASYFIKACJA  na przyk³adzie metody k-NN (k-Nearest Neighbors)
##########################################################################################
## Kurs: Eksploracja danych / data mining
## Materia³y pomocnicze do laboratorium 
## (C) A.Zagdanski & A.Suchwalko


# Zajmiemy siê analiz¹ danych dotycz¹cych irysów. Zachêcamy do samodzielnej pracy z trudniejszymi zbiorami danych
# oraz do stawiania w³asnych problemów, które mog¹ byæ rozwi¹zane z wykorzystaniem metod klasyfikacji.
#
# Przyk³ady ciekawych danych do wykorzystania w przyk³adach:
#  -- {painters}{MASS}
#  -- {PimaIndiansDiabetes}{mlbench}
#  -- {BreastCancer}{mlbench}
#  -- {Glass}{mlbench}
#  -- {spam}{ElemStatLearn}

library(MASS)
data(iris)


############################################################################################
#### Przed budow¹ modeli klasyfikacyjnych nale¿y przyjrzeæ siê danym (analiza opisowa)
#
# W szczególnoœci wykonujemy analizy jedno- i dwuwymiarowe dla danych pogrupowanych: 
#  -- wykresy pude³kowe, 
#  -- histogramy, 
#  -- wykresy rozrzutu, 
#  -- korelacje,
#  -- ....


############################################################################################
#### klasyfikacja obiektów na przyk³adzie metody k-nn  z pakietu class

library(class)

# losowanie podzbiorów
n <- dim(iris)[1]

# losujemy obiekty do zbioru ucz¹cego i testowego
learning.set.index <- sample(1:n,2/3*n)

# tworzymy zbiór ucz¹cy i testowy
learning.set <- iris[learning.set.index,]
test.set     <- iris[-learning.set.index,]

# rzeczywiste gatunki irysów
etykietki.rzecz <- test.set$Species

# teraz robimy prognozê
etykietki.prog <- knn(learning.set[,-5], test.set[,-5], learning.set$Species, k=5)

# tablica kontyngencji
(wynik.tablica <- table(etykietki.prog,etykietki.rzecz))

# b³¹d klasyfikacji
n.test <- dim(test.set)[1]

(n.test - sum(diag(wynik.tablica))) / n.test


# Uda³o siê, ale to nie jest najlepsza metoda. 
# Wady funkcji knn{class}:
#  * trudno jest wybieraæ cechy,  
#  * w R zazwyczaj modele klasyfikacyjne buduje siê inaczej, ³atwiej wtedy widaæ, na czym polega budowa i ocena
#     jakoœci modeli klasyfikacyjnych.



############################################################################################
#### Idea budowy modeli klasyfikacyjnych w R na przyk³adzie metody k-nn z pakietu ipred 

# Uwaga: 
# Implementacja w pakiecie "ipred" znacznie bardziej zbli¿ona jest do standardowej implementacji
# modeli klasyfikacyjnych w systemie R.

library(ipred)

# budujemy model
model.knn.1 <- ipredknn(Species ~ ., data=learning.set, k=5)

# zobaczmy, co jest w œrodku
model.knn.1
summary(model.knn.1)
attributes(model.knn.1)

# sprawdŸmy jakoœæ modelu
# uwaga: czasami funkcje "predict" dzia³aj¹ niestandardowo
etykietki.prog <- predict(model.knn.1,test.set,type="class")

# tablica kontyngencji
(wynik.tablica <- table(etykietki.prog,etykietki.rzecz))

# b³¹d klasyfikacji
(n.test - sum(diag(wynik.tablica))) / n.test

# mo¿na równie¿ skonstruowaæ model oparty na innych zmiennych oraz bazuj¹cy na innej liczbie s¹siadów
# i sprawdziæ jego jakoœæ:
model.knn.2 <- ipredknn(Species ~ Petal.Length + Petal.Width, data=learning.set, k=5)
model.knn.3 <- ipredknn(Species ~ Sepal.Length + Sepal.Width, data=learning.set, k=5)



############################################################################################
#### Inne (zaawansowane) sposoby oceny jakoœci klasyfikacji

# Czêsto stosuje sie metodê cross-validation polegaj¹c¹ na wielokrotnym losowaniu zbioru ucz¹cego i testowego, budowie klasyfikatora
# na zbiorze ucz¹cym, sprawdzenia go na testowym oraz uœrednieniu wyników.

# Oczywiœcie, da siê zrobiæ wszystko "na piechotê" w pêtli i uœredniæ, ale mo¿na zrobiæ to znacznie proœciej...

library(ipred)

# ¿eby skorzystaæ z gotowych funkcji nale¿y przygotowaæ sobie "wrapper" dostosowuj¹cy
# funkcjê "predict" dla naszego modelu do standardu wymaganego przez "errorest"

my.predict  <- function(model, newdata) predict(model, newdata=newdata, type="class")
my.ipredknn <- function(formula1, data1, ile.sasiadow) ipredknn(formula=formula1,data=data1,k=ile.sasiadow)

# porownanie b³êdów klasyfikacji: cv, boot, .632plus
errorest(Species ~., iris, model=my.ipredknn, predict=my.predict, estimator="cv",     est.para=control.errorest(k = 10), ile.sasiadow=5)
errorest(Species ~., iris, model=my.ipredknn, predict=my.predict, estimator="boot",   est.para=control.errorest(nboot = 10), ile.sasiadow=5)
errorest(Species ~., iris, model=my.ipredknn, predict=my.predict, estimator="632plus",est.para=control.errorest(nboot = 10), ile.sasiadow=5)


############################################################################################
#### Obszary decyzyjne (dla wybranych par zmiennych)

library(klaR)

levels(iris$Species) = c("A","B","C") #"setosa"     "versicolor" "virginica"

drawparti( iris$Species, x=iris$Petal.Length, y=iris$Petal.Width, method = "sknn", k=1,  xlab="PL",ylab="PW")
drawparti( iris$Species, x=iris$Petal.Length, y=iris$Petal.Width, method = "sknn", k=15,  xlab="PL",ylab="PW")

partimat(Species ~ ., data = iris, method = "sknn",  plot.matrix = FALSE, imageplot = TRUE)