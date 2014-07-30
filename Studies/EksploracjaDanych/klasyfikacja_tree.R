##########################################################################################
# DRZEWA KLASYFIKACYJNE
##########################################################################################
## Kurs: Eksploracja danych  / data mining
## Materia³y pomocnicze do laboratorium 
## (C) A.Zagdanski & A.Suchwalko

library(MASS)
data(iris)

# losowanie podzbiorów
n <- dim(iris)[1]

# losujemy obiekty do zbioru ucz¹cego i testowego
learning.set.index <- sample(1:n,2/3*n)

# tworzymy zbiór ucz¹cy i testowy
learning.set <- iris[learning.set.index,]
test.set     <- iris[-learning.set.index,]
n.test <- dim(test.set)[1]

# rzeczywiste gatunki irysów
etykietki.rzecz <- test.set$Species

############################################################################################
#### drzewa klasyfikacyjne

# Uwaga: Drzewa klasyfikacyjne maj¹ wiele parametrów, które mo¿na ustawiaæ. 
# Co wiêcej, w systemie R mamy wiele implementacji algorytmów konstruuj¹cych drzewa.

library(tree)

# budowa modelu - jak wszystkich innych!
model.tree.1 <- tree(Species ~ .,data=learning.set) 

# obejrzyjmy model
model.tree.1
summary(model.tree.1)

# teraz obrazek
plot(model.tree.1,type="uniform")
title("Drzewo klasyfikacyjne")
text(model.tree.1,cex=1.2,col="blue")

# informacje o b³êdnie zaklasyfikowanych obiektach (³¹cznie oraz osobno dla poszczególnych wêz³ów) 
misclass.tree(model.tree.1)
misclass.tree(model.tree.1, detail=T)

etykietki.prog <- predict(model.tree.1,test.set,type="class")

# tablica kontyngencji
(wynik.tablica <- table(etykietki.prog,etykietki.rzecz))

# b³¹d klasyfikacji
(n.test - sum(diag(wynik.tablica))) / n.test

# mincut  - minimalna, dopuszczalna liczba obiektów w jednym z wêz³ów potomnych
# minsize - minimalna liczba obiektów w wêŸle, dla której jeszcze wykonujemy podzia³
# mindev  - minmalna wartoœæ rozrzutu (ang. "deviance") w wêŸle, dla ktorego jeszcze wykonujemy podzia³ 
# split   - kryterium podzia³u, do wyboru mamy: "deviance" (odchylenie) lub "gini" (indeks Giniego)


##############################################
# obszary decyzyjne
# Dla drzew ³atwo zobaczyæ obszary decyzyjne. W tym celu musimy jednak mieæ drzewo zbudowane w oparciu o jedn¹
# lub dwie zmienne.

model.tree.2 <- tree(Species ~ Petal.Length + Sepal.Length,data=iris) 

par(pty = "s")
plot(iris$Petal.Length, iris$Sepal.Length, type="n", xlab="petal length", ylab="sepal length")
text(iris$Petal.Length, iris$Sepal.Length, c("s", "c", "v")[iris$Species])
partition.tree(model.tree.2,col="orange",cex=2,add=T)

##############################################     
# automatyczne przycinanie drzew
# Do tego celu s³u¿y funkcja "prune.tree". Zachêcamy do poznania jej argumentów, a teraz przyjrzymy siê najprostszemu
# przyk³adowi.

# budujemy skomplikowane drzewo
model.tree.3 <- tree(Species ~ Sepal.Length + Sepal.Width,data=iris)

# obcinamy
model.tree.4 <- prune.tree(model.tree.3,method="misclass",best=3)

par(mfrow=c(2,1))
plot(model.tree.3)
text(model.tree.3)
title("Oryginalne drzewo")
plot(model.tree.4)
text(model.tree.4)
title("Przyciete drzewo")

par(mfrow=c(1,1))

##############################################
# interaktywne przycinanie drzew

# przytniemy drzewo "model.tree.3" z poprzedniego przyk³adu
model.tree.3

# rysujemy i przycinamy
plot(model.tree.3,pch=1,cex=1.2)
text(model.tree.3,cex=1.2)
model.tree.3.przyciete <- snip.tree(model.tree.3)

# Uwaga: Klikamy dwukrotnie aby zaznaczyæ wêz³y, które chcemy odci¹æ. Koñczymy pracê w trybie interaktywnym
# klikaj¹c prawy klawisz myszki i wybieraj¹c "stop".

# zobaczmy, co wysz³o
model.tree.3.przyciete

# nowe okno graficzne
windows()
dev.set(which=dev.cur())

plot(model.tree.3.przyciete,pch=1,cex=1.2)
text(model.tree.3.przyciete,cex=1.2)


##############################################
# inne sposoby wizualizacji drzew klasyfikacyjnych

# skorzystamy z biblioteki rpart
library(rpart)

model.rpart <- rpart(Species~., data=iris, method="class")

# bardzo prosta prezentacja
plot(model.rpart, uniform=T, branch=0, margin=0.1)
text(model.rpart, digits=3, use.n=T, pretty=0)

# bardziej zaawansowana
par(xpd=T)
plot(model.rpart, uniform=T, branch=0, margin=0.08, compress=T, nspace=.8)
text(model.rpart, splits=T, all=T, pretty=0, use.n=T, fancy=T, fwidth=0.4, fheight=0.7, cex=0.8)

