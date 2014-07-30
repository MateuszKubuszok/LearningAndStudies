##########################################################################################
# ANALIZA SKUPIEN
##########################################################################################
## Kurs:  Eksploracja danych  / data mining
## Materia³y pomocnicze do laboratorium 
## A.Suchwalko & A.Zagdanski


# Zawartoœæ skryptu
# - metoda K-means
# - macierz niepodobieñstwa
# - algorytm PAM
# - metody hierarchiczne: AGNES i DIANA

##########################################################################################################
##### Metoda K-means #####################################################################################
##########################################################################################################

# K-means to jedna z najbardziej popularnych metod stosowanych w analizie skupieñ
# Nale¿y ona do metod grupuj¹cych (ang. partitioning methods)
# Metoda ta ma jednak pewne ograniczenia i wady, np.:
# a) Dzia³a tylko dla zmiennych liczbowych  (miara niepodobieñstwa = odleg³oœæ euklidesowa)
# b) Najczêœciej wykrywa prawid³owo jedynie skupiska o kszta³tach wypuk³ych


#=== Przyk³ad: K-means dla danych o  irysach =============================================================

library(stats)

data(iris)
irysy.cechy <- iris[,1:4] # Usuwamy etykietki klas
irysy.etykietki.rzeczywiste <- iris[,5]

# Podzia³ na K skupisk
k <- 3
kmeans.k3 <- kmeans(irysy.cechy,centers=k,iter.max=10)

# Etykietki grup
(irysy.etykietki.kmeans <- kmeans.k3$cluster)

# Wizualizacja wyników analizy skupieñ (wykres rozrzutu dla zmiennych Petal.Length i Sepal.Width)
plot(irysy.cechy$Petal.Length,irysy.cechy$Sepal.Width,col=irysy.etykietki.kmeans,
pch=as.numeric(irysy.etykietki.rzeczywiste))
title("Klastrowanie z wykorzystaniem k-means \n kolor -  etykietki z k-means, symbol - etykietki rzeczywiste")

# zaznacz centra klasteryzacji
points( kmeans.k3$centers[,c("Petal.Length","Sepal.Width")],pch=16,cex=1.5,col=1:3)


#---- Przyk³ad ('Cassini problem') ----------------------------------------------------------------------
# A teraz przyk³ad danych, z którymi k-means nie radzi sobie najlepiej ...
# Cassini problem: Skupisko o kszta³cie sferycznym wewn¹trz 2 skupisk o kszta³tach bananowych

library(mlbench)
dane.cassini <- mlbench.cassini(n=2000)
plot(dane.cassini)
title('Cassini problem')

dane  <- dane.cassini$x
klasy <- dane.cassini$clases


kmeans.cassini3 <- kmeans(dane,centers=3,iter.max=10,nstart=1)
plot(dane,col=kmeans.cassini3$cluster)
title('Cassini problem: wynik metody k-means')

# Æwiczenia
# 1) Powtórz kilkukrotnie powy¿sz¹ analizê, obserwuj¹c, jak zmienia siê rezultat (=otrzymana partycja)
# 2) PrzeprowadŸ podobne eksperymenty jak w punkcie 1), tym razem dla innej ni¿ k=3
# liczby klastrów  (np. k=4,5,6)


# Analiza dla 10-ciu losowych inicjalizacji metody k-means
kmeans.cassini3.10x <- kmeans(dane,centers=3,iter.max=10,nstart=10)
plot(dane,col=kmeans.cassini3.10x$cluster)
title('Cassini problem: wynik metody k-means (10 inicjalizacji)')
# Czy teraz uda³o sie znaleŸæ prawdziw¹ strukturê danych?


######################################################################################################
####### Algorytm  PAM (Partitioning Around Medoids) ##################################################
######################################################################################################

# Metoda PAM jest bardziej ogólna ni¿ K-means
# Dzia³a dla zmiennych dowolnych typów (tzn. dla dowolnej macierzy niepodobieñstwa)
# Centrami skupisk s¹ nie œrednie a "medoidy" (obiekty-reprezentanci)
#  wybrane spoœród wszystkich obiektów znajduj¹cych siê w naszym zbiorze danych

# Wczytanie niezbêdnych bibliotek
library(MASS)
library(cluster)
data(Cars93)


#=== Przyklad: Cars93 - wszystkie cechy ===============================================================
# Wyznaczenie macierzy podobieñstwa/niepodobieñstwa pomiêdzy obiektami
# jest kluczowe dla analizy skupieñ i czêsto wa¿niejsze od samego wyboru metody (algorytmu) podzia³u na grupy!
# Aby zrobiæ to poprawnie powinniœmy zorientowaæ siê, jakie typu s¹ nasze zmienne (cechy), tzn.
# liczbowe, nominalne, binarne, itd.


# Na pocz¹tek: usuwamy zmienne "Model" i "Make"
# kazdy samochód ma ró¿n¹ wartoœæ, wiec s¹ nieprzydatne do oceny  podobieñstwa
  rownames(Cars93) <- Cars93$Make
  wybraneCechy <- setdiff(colnames(Cars93),c("Model","Make"))
 Cars93.wybrane <- Cars93[,wybraneCechy]
 samochody.MacNiepodob <- daisy(Cars93.wybrane)

# Konwersja do macierzy
 samochody.MacNiepodob.matrix <- as.matrix(samochody.MacNiepodob)


Cars93.pam3 <- pam(x=samochody.MacNiepodob,diss=TRUE,k=3)
X11()
plot(Cars93.pam3)


#  Uwaga
#  Obiekt  Cars93.pam3 zawiera informacje o poszczególnych klastrach, w tym m.in.:
#  rozmiar(size), maksymalna i œrednia wartoœæ niepodobieñstwa (max_diss,av_diss),
#  œrednica (diameter), separacja  (separation),
#  informacja o L- i L*-klastrach,
#  wartoœci 'silhouette' dla poszczególnych obiektów
#  (Szczegó³owy opis  wskaŸników --> patrz wyk³ad lub help)

(summary(Cars93.pam3))

# Przyk³adowa wizualizacja wyników w 2D  (kolor = etykietka klastra)
X11()
etykietki <- Cars93.pam3$clustering
plot(Cars93$Horsepower,Cars93$Price,col=etykietki,xlab="Moc",ylab="Cena")
title("Dane Cars93 -- wizualizacja wynikow analizy skupien")

# Mo¿emy sprawdziæ, które obiekty s¹ centrami skupisk czyli s¹ reprezentatywne dla danego skupiska
CentraSkupisk.nazwy <- Cars93.pam3$medoids

# Æwiczenie (Interpretacja wyników analizy skupieñ)
# Przeanalizuj jakie samochody znalaz³y siê w poszczególnych skupieniach
# Mo¿na np. przeanalizowaæ w poszczególnych skupieniach: wartoœci œrednie dla  cech numerycznych lub
# licznoœci (funkcja "table") dla cech jakoœciowych


#=== Przyk³ad: Cars93 - Wybieramy tylko zmienne liczbowe (typ==numeric) ============================================
Cars93.CechyLiczbowe <- Cars93[,names(unlist(lapply(Cars93,FUN=function(x) if(is.numeric(x)) "numeric" else NULL)))]
Cars93.numeric.pam3 <- pam(Cars93.CechyLiczbowe,k=3,metric="euclidean")

(summary(Cars93.numeric.pam3))

# Wizualizacja wyników
plot(Cars93.numeric.pam3)
# W tym przypadku, oprócz wykresów silhouette pojawia siê równie¿ wizualizacja wyników analizy skupieñ
# z wykorzystaniem PCA


############################################################################################################
############## Metody hierarchiczne: AGNES,DIANA ###########################################################
############################################################################################################

#============== AGNES (Aglomerative Nesting) =============================================

# Trzy metody ³¹czenia klastrow (ang. linkage method)
# method = 'average'  (œrednia odleg³oœæ)
# method = 'complete' (najdalszy s¹siad)
# method = 'single'   (najbli¿szy s¹siad)

Cars93.agnes.avg <- agnes(x=samochody.MacNiepodob,diss=TRUE,method="average")
Cars93.agnes.single <- agnes(x=samochody.MacNiepodob,diss=TRUE,method="single")
Cars93.agnes.complete <- agnes(x=samochody.MacNiepodob,diss=TRUE,method="complete")


#--- Wizualizacja (dendrogram, clustering tree) ------------------------------------------

X11()
par(cex=0.6)
plot(Cars93.agnes.avg,which.plots=2,main="AGNES: average linkage")

X11()
par(cex=0.6)
plot(Cars93.agnes.single,which.plots=2,main="AGNES: single linkage")

X11()
par(cex=0.6)
plot(Cars93.agnes.complete,which.plots=2, main="AGNES: complete linkage")


# Æwiczenia
# 1) Porównaj skonstruowane dendrogramy
# 2) Która metoda ³¹czenia klastrow wydaje siê najlepsza?

#--- Odcinanie drzewa klastrow (dedrogramu) dla zadanej liczby klastrów  -----------------------------
Cars93.agnes.avg.k2 <- cutree(Cars93.agnes.avg,k=2) # etykietki klastrow

#--- WskaŸniki silhouette dla metod hierarchicznych --------------------------------------------------

sil.agnes <- silhouette(x=Cars93.agnes.avg.k2,dist=samochody.MacNiepodob)
# œredni¹ wartoœæ silhouette dla ka¿dego klastra mo¿emy otrzymaæ nastêpuj¹co
(sil.srednie.w.klastrach <- summary(sil.agnes)$clus.avg.widths)

# Uwaga
# Podobnie jak dla PAM i K-means mo¿emy wybraæ opytymaln¹ liczbê klastrów, która maksymalizuje sredni¹
# wartoœæ silhouette

#=========== DIANA (Divisive clustering) ===========================================
Cars93.diana <- diana(x=samochody.MacNiepodob,diss=TRUE)
X11()
par(cex=0.6)
plot(Cars93.diana,which.plot=2, main="DIANA")

# Odcinamy drzewo tak aby uzyskac dokladnie K=3 klastry
(Cars93.diana.k3 <- cutree(Cars93.diana,k=3)) # etykietki klastrow


#========== Przyklad1: Dane o zwierzêtach ==========================================
# Wczytanie danych
require(cluster)
data(animals)

# Zamiana na zmienne logiczne i obiekt typu data.frame
 {animals==2} -> animals1
 animals.df <- data.frame(animals1)

# Wyznaczenie macierzy (nie)podobieñstwa (wszystkie zmienne s¹ zmiennymi binarnymi - symetrycznymi ! )
daisy(animals.df,type=list(symm=1:6)) -> dis.matrix

animals.agnes <- agnes(dis.matrix,method="average",diss=T)

# wykres: clustering tree + banner
 plot(animals.agnes)

# Æwiczenia
# 1) Porównaj wyniki analizy skupieñ dla
#    a) metody AGNES  wykorzystuj¹cej inne ni¿ 'average' metody ³¹czenia klastrów
#       (tzn. method = 'complete' i method = 'single')
#    b) hierarchicznej metody rozdzielaj¹cej (DIANA)
#    c) metody grupuj¹cej PAM
# 2) Zaproponuj metodê  wyboru optymalnej liczby klastrów   dla tego zbioru danych
#
