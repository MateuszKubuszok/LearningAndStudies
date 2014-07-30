################################################################################
#
# Mateusz Kubuszok, 179956
# Eksploracja danych, lista 2
#
################################################################################

library(HDclassif) # Wczytanie danych na temat win
data(wine)         #

colnames(wine) <- gsub("^V1$",  "alcohol",           colnames(wine)) # Zmiana nazw zmiennych na bardziej informacyjne
colnames(wine) <- gsub("^V2$",  "malic_acid",        colnames(wine)) #
colnames(wine) <- gsub("^V3$",  "ash",               colnames(wine)) #
colnames(wine) <- gsub("^V4$",  "alcalinity",        colnames(wine)) #
colnames(wine) <- gsub("^V5$",  "magnesium",         colnames(wine)) #
colnames(wine) <- gsub("^V6$",  "phenols",           colnames(wine)) #
colnames(wine) <- gsub("^V7$",  "flavanoids",        colnames(wine)) #
colnames(wine) <- gsub("^V8$",  "nonflavanoids",     colnames(wine)) #
colnames(wine) <- gsub("^V9$",  "proanthocyanidins", colnames(wine)) #
colnames(wine) <- gsub("^V10$", "color",             colnames(wine)) #
colnames(wine) <- gsub("^V11$", "hue",               colnames(wine)) #
colnames(wine) <- gsub("^V12$", "od280_od315",       colnames(wine)) #
colnames(wine) <- gsub("^V13$", "proline",           colnames(wine)) #

wine$numclass  <- wine$class                                       # Kopia klas do wykresu korelacji 
wine$class     <- as.factor(wine$class)                            # Korekta typu


################################################################################
#
# Wybór cech dyskryminujących
# 
################################################################################

# Wykresy poszczegolnych cech wg. klas wina

par(mfrow=c(4,4))
plot(          alcohol~class, data=wine)
plot(       malic_acid~class, data=wine)
plot(              ash~class, data=wine)
plot(       alcalinity~class, data=wine)
plot(        magnesium~class, data=wine)
plot(          phenols~class, data=wine)
plot(       flavanoids~class, data=wine)
plot(    nonflavanoids~class, data=wine)
plot(proanthocyanidins~class, data=wine)
plot(            color~class, data=wine)
plot(              hue~class, data=wine)
plot(      od280_od315~class, data=wine)
plot(          proline~class, data=wine)


# Zależności między zmiennymi

library(lattice)
correlation <- cor(wine[,!(names(wine) %in% c("class"))])
levelplot(correlation)                                    # macierz zależności
levelplot(ifelse(abs(correlation) < 0.5, 0, 1))           # zmienne których kowariancja jest odległa od 0 o co najmniej 0.5


################################################################################
#
# Zbiór uczący oraz testowy
# 
################################################################################

# Utworzenie zbioru uczącego oraz testowego oraz pewne dane początkowe

classes            <- unique(wine$class)
classes.number     <- length(classes)[1]
n                  <- dim(wine)[1]
learning.set.index <- sample(1:n,n*2/3)
learning.set       <- wine[learning.set.index,]
learning.set.n     <- dim(learning.set)[1]
test.set           <- wine[-learning.set.index,]
test.set.n         <- dim(test.set)[1]
labels             <- learning.set$class
colors             <- ifelse(labels == "1", "green", ifelse(labels == "2", "orange", "red"))
traits             <- c("proline", "od280_od315", "hue", "flavanoids", "phenols", "alcalinity")


################################################################################
#
# Klasyfikacja
# 
################################################################################

# Drzewo klasyfikacyjne

library(tree)
tree.model.traits <- c("class", traits)
tree.model.data   <- learning.set[,tree.model.traits]
tree.model.tree   <- tree(class ~ ., data=tree.model.data)
summary(tree.model.tree)

par(mfrow=c(1,1))
plot(tree.model.tree, type="uniform")      # podgląd drzewa klasyfikayjnego
title("Drzewo klasyfikacyjne")             #
text(tree.model.tree, cex=1.2, col="blue") #

# Prognozy

tree.model.predictions <- predict(tree.model.tree, test.set, type="class")        # predykcja dla zb. testowego
tree.model.table       <- table(tree.model.predictions, test.set$class)           # tablica kontyngencji
tree.model.error       <- (test.set.n - sum(diag(tree.model.table))) / test.set.n # błąd klasyfikacji


# k najbliższych sąsiadów

library(class)
knn.model.predictions <- knn(learning.set, test.set, learning.set$class, k=5)   # predykcja dla zb. testowego
knn.model.table       <- table(knn.model.predictions, test.set$class)           # tablica kontyngencji
knn.model.error       <- (test.set.n - sum(diag(knn.model.table))) / test.set.n # błąd klasyfikacji


################################################################################
#
# Redukcja wymiaru
# 
################################################################################

# Obliczanie PCA

pca.traits   <- traits
pca.data     <- learning.set[,pca.traits]
pca.result   <- prcomp(pca.data, retx=TRUE, center=FALSE, scale=FALSE)
pca.rotation <- varimax(pca.result$rotation)$rotmat
summary(pca.result)

#model.pca.rawdata        <- (as.matrix(pca.data) %*% pca.rotation)             # sama ortogonalizacja
#model.pca.rawdata        <- (as.matrix(pca.data) %*% pca.rotation)[,c(1,2)]    # redukcja wymiarów do 2
model.pca.rawdata        <- (as.matrix(pca.data) %*% pca.rotation)[,c(1,2,3)]  # redukcja wymiarów do 3
model.pca.data           <- data.frame(model.pca.rawdata, labels)
colnames(model.pca.data) <- gsub("^labels$", "class", colnames(model.pca.data))
#test.set.pca.raw         <- (as.matrix(test.set[,pca.traits]) %*% pca.rotation)             # sama ortogonalizacja
#test.set.pca.raw         <- (as.matrix(test.set[,pca.traits]) %*% pca.rotation)[,c(1,2)]    # redukcja wymiarów do 2
test.set.pca.raw         <- (as.matrix(test.set[,pca.traits]) %*% pca.rotation)[,c(1,2,3)]  # redukcja wymiarów do 3
test.set.pca             <- data.frame(test.set.pca.raw, test.set$class)
test.set.pca.n           <- dim(test.set.pca)[1]
colnames(test.set.pca)   <- gsub("^test.set.class$", "class", colnames(test.set.pca))


# Podgląd zależności dla PCA okrojonego do 2 wymiarów

par(mfrow=c(1,1))
plot(model.pca.rawdata[,1], model.pca.rawdata[,2])
title("Wykres zależności dla PCA")
text(model.pca.rawdata[,1], model.pca.rawdata[,2], labels, cex=1, pos=4, col=colors)


# Podgląd zależności dla PCA okrojonego do 3 wymiarów

library(rgl)
rgl.open()
rgl.bg(color="white")
rgl.spheres(model.pca.rawdata[,1], model.pca.rawdata[,2], model.pca.rawdata[,3], r=.01, col=colors, alpha=.35)
rgl.texts  (model.pca.rawdata[,1], model.pca.rawdata[,2], model.pca.rawdata[,3], labels, adj=.5, color=colors);


# Drzewo klasyfikacyjne dla PCA

model.pca.tree <- tree(class ~ ., data=model.pca.data)
summary(model.pca.tree)

plot(model.pca.tree, type="uniform")      # podgląd drzewa klasyfikacyjnego
title("Drzewo klasyfikacyjne dla PCA")    #
text(model.pca.tree, cex=1.2, col="blue") #

# Predykcje

tree.model.pca.predictions <- predict(model.pca.tree, test.set.pca, type="class")                 # predykcja dla zb. testowego
tree.model.pca.table       <- table(tree.model.pca.predictions, test.set.pca$class)               # tablica kontyngencji
tree.model.pca.error       <- (test.set.pca.n - sum(diag(tree.model.pca.table))) / test.set.pca.n # błąd klasyfikacji


# k najbliższych sąsiadów

knn.model.pca.predictions <- knn(model.pca.data, test.set.pca, model.pca.data$class, k=5)       # predykcja dla zb. testowego
knn.model.pca.table       <- table(knn.model.pca.predictions, test.set.pca$class)               # tablica kontyngencji
knn.model.pca.error       <- (test.set.pca.n - sum(diag(knn.model.pca.table))) / test.set.pca.n # błąd klasyfikacji
