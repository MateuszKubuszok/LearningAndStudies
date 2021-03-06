##########################################################################################
# METODY REDUKCJI WYMIARU (PCA i MDS)
##########################################################################################
## Kurs: Eksploracja danych  / data mining
## Materia�y pomocnicze do laboratorium 
## (C) A.Zagdanski & A.Suchwalko

library(MASS)
library(cluster)

data(Cars93)

# we�my probk� danych, �eby lepiej by�o mo�na dane zwizualizowa�
# zach�camy do wizualizacji ca�ego zbioru danych, a tak�e do dok�adniejszego przygl�dania si� samochodom poszczeg�lnych typ�w
# (np. tylko ameryka�skim albo modelom poszczeg�lnych producent�w)
n <- dim(Cars93)[1]

probka.index <- sample(1:n,30) # losujemy 30 samochod�w

Cars93 <- Cars93[probka.index,]

# wyznaczmy nazwy samochodow (pomo�e nam to w przysz�ej identyfikacji samochod�w)
nazwy <- paste(Cars93$Manufacturer,Cars93$Model,sep=" ")


##########################################################################################
#### Principal Component Analysis   (PCA)

# najpierw zr�bmy prosty przyk�ad wykorzystujac zmienne "waga" i "moc" (jest ciekawy, bo s� obserwacje nietypowe)
plot(Cars93$Weight,Cars93$Horsepower)
text(Cars93$Weight,Cars93$Horsepower,nazwy,cex=.6)

# teraz zobaczmy te dane w nowym uk�adzie wsp�rz�dnych uzyskanym za pomoc� PCA
cechy.pca <- c("Weight","Horsepower")

dane.pca <- Cars93[,cechy.pca]

prcomp(dane.pca,retx=T,center=T,scale.=T) -> dane.po.pca

# teraz wizualizacja
windows() # nowe urz�dzenie graficzne
plot(dane.po.pca$x[,1],dane.po.pca$x[,2])
text(dane.po.pca$x[,1],dane.po.pca$x[,2],nazwy,cex=.6)

# teraz to samo, ale bardziej szczeg�owo i dla wi�kszej liczby cech

# wybieramy dosy� du�y zbi�r cech numerycznych (mo�na wi�cej)
cechy.pca <- names(Cars93)[c(4,5,6,7,8,12,13,14,15)]

dane.pca <- Cars93[,cechy.pca]

prcomp(dane.pca,retx=T,center=T,scale.=T) -> dane.po.pca

print("Skladowe glowne:")
print(dane.po.pca$rotation)

summary(dane.po.pca)

# analizujemy udzia� wariancji wyja�nionej przez kolejne sk�adowe g��wne
wariancja <- ( dane.po.pca$sdev ^2)/sum(dane.po.pca$sdev^2) 
wariancja.narast <- cumsum(wariancja)
barplot(wariancja)
barplot(wariancja.narast)

# teraz wizualizacja
plot(dane.po.pca$x[,1],dane.po.pca$x[,2])
identify(dane.po.pca$x[,1],dane.po.pca$x[,2],plot=T,labels=nazwy,col="red") -> indeksy.punktow

print("Nazwy wskazanych samochod�w: ")
print(nazwy[indeksy.punktow])


##########################################################################################
#### skalowanie wielowymiarowe   (MultiDimensional Scaling, MDS)

# zobaczymy, jakie mamy cechy
names(Cars93)
# kt�re z nich mo�na wykorzysta�?

# wybierzemy niedu�y, ale zr�nicowany zbi�r cech
cechy.mds <- c("Price","Weight","Cylinders","AirBags","Width","Turn.circle")

dane.mds <- Cars93[,cechy.mds]

# wyznaczamy odleg�o�ci
odl <- daisy(dane.mds,type=list(ordratio=c("Cylinders","AirBags")),stand=T)

# zobaczmy, jakie s� odleg�o�ci
odl.macierz <- as.matrix(odl)

# metryczne skalowanie wielowymiarowe
mds.wynik <- cmdscale(odl,k=2)

# teraz wizualizacja
plot(mds.wynik[,1],mds.wynik[,2])
identify(mds.wynik[,1],mds.wynik[,2],plot=T,labels=nazwy,col="red")


# jak zrobi� wizualizacj� z podzia�em na grupy?
# narysujmy r�nymi kolorami samochody krajowe i zagraniczne
par(bg="lightyellow1")
plot(mds.wynik[,1],mds.wynik[,2],col=c("red","blue")[as.numeric(Cars93$Origin)],pch=20,cex=2)
text(mds.wynik[,1],mds.wynik[,2],nazwy,col="brown",cex=.8)


# jak to zrobi� w 3d rgl?
mds.wynik.3 <- cmdscale(odl,k=3)

library(rgl)
rgl.open()
rgl.bg(color="black")
rgl.spheres(mds.wynik.3[,1],mds.wynik.3[,2],mds.wynik.3[,3],r=.01,col="blue",alpha=.35)
rgl.texts  (mds.wynik.3[,1],mds.wynik.3[,2],mds.wynik.3[,3],nazwy,adj=.5,color="red");

