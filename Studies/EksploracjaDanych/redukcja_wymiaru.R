##########################################################################################
# METODY REDUKCJI WYMIARU (PCA i MDS)
##########################################################################################
## Kurs: Eksploracja danych  / data mining
## Materia³y pomocnicze do laboratorium 
## (C) A.Zagdanski & A.Suchwalko

library(MASS)
library(cluster)

data(Cars93)

# weŸmy probkê danych, ¿eby lepiej by³o mo¿na dane zwizualizowaæ
# zachêcamy do wizualizacji ca³ego zbioru danych, a tak¿e do dok³adniejszego przygl¹dania siê samochodom poszczególnych typów
# (np. tylko amerykañskim albo modelom poszczególnych producentów)
n <- dim(Cars93)[1]

probka.index <- sample(1:n,30) # losujemy 30 samochodów

Cars93 <- Cars93[probka.index,]

# wyznaczmy nazwy samochodow (pomo¿e nam to w przysz³ej identyfikacji samochodów)
nazwy <- paste(Cars93$Manufacturer,Cars93$Model,sep=" ")


##########################################################################################
#### Principal Component Analysis   (PCA)

# najpierw zróbmy prosty przyk³ad wykorzystujac zmienne "waga" i "moc" (jest ciekawy, bo s¹ obserwacje nietypowe)
plot(Cars93$Weight,Cars93$Horsepower)
text(Cars93$Weight,Cars93$Horsepower,nazwy,cex=.6)

# teraz zobaczmy te dane w nowym uk³adzie wspó³rzêdnych uzyskanym za pomoc¹ PCA
cechy.pca <- c("Weight","Horsepower")

dane.pca <- Cars93[,cechy.pca]

prcomp(dane.pca,retx=T,center=T,scale.=T) -> dane.po.pca

# teraz wizualizacja
windows() # nowe urz¹dzenie graficzne
plot(dane.po.pca$x[,1],dane.po.pca$x[,2])
text(dane.po.pca$x[,1],dane.po.pca$x[,2],nazwy,cex=.6)

# teraz to samo, ale bardziej szczegó³owo i dla wiêkszej liczby cech

# wybieramy dosyæ du¿y zbiór cech numerycznych (mo¿na wiêcej)
cechy.pca <- names(Cars93)[c(4,5,6,7,8,12,13,14,15)]

dane.pca <- Cars93[,cechy.pca]

prcomp(dane.pca,retx=T,center=T,scale.=T) -> dane.po.pca

print("Skladowe glowne:")
print(dane.po.pca$rotation)

summary(dane.po.pca)

# analizujemy udzia³ wariancji wyjaœnionej przez kolejne sk³adowe g³ówne
wariancja <- ( dane.po.pca$sdev ^2)/sum(dane.po.pca$sdev^2) 
wariancja.narast <- cumsum(wariancja)
barplot(wariancja)
barplot(wariancja.narast)

# teraz wizualizacja
plot(dane.po.pca$x[,1],dane.po.pca$x[,2])
identify(dane.po.pca$x[,1],dane.po.pca$x[,2],plot=T,labels=nazwy,col="red") -> indeksy.punktow

print("Nazwy wskazanych samochodów: ")
print(nazwy[indeksy.punktow])


##########################################################################################
#### skalowanie wielowymiarowe   (MultiDimensional Scaling, MDS)

# zobaczymy, jakie mamy cechy
names(Cars93)
# które z nich mo¿na wykorzystaæ?

# wybierzemy niedu¿y, ale zró¿nicowany zbiór cech
cechy.mds <- c("Price","Weight","Cylinders","AirBags","Width","Turn.circle")

dane.mds <- Cars93[,cechy.mds]

# wyznaczamy odleg³oœci
odl <- daisy(dane.mds,type=list(ordratio=c("Cylinders","AirBags")),stand=T)

# zobaczmy, jakie s¹ odleg³oœci
odl.macierz <- as.matrix(odl)

# metryczne skalowanie wielowymiarowe
mds.wynik <- cmdscale(odl,k=2)

# teraz wizualizacja
plot(mds.wynik[,1],mds.wynik[,2])
identify(mds.wynik[,1],mds.wynik[,2],plot=T,labels=nazwy,col="red")


# jak zrobiæ wizualizacjê z podzia³em na grupy?
# narysujmy ró¿nymi kolorami samochody krajowe i zagraniczne
par(bg="lightyellow1")
plot(mds.wynik[,1],mds.wynik[,2],col=c("red","blue")[as.numeric(Cars93$Origin)],pch=20,cex=2)
text(mds.wynik[,1],mds.wynik[,2],nazwy,col="brown",cex=.8)


# jak to zrobiæ w 3d rgl?
mds.wynik.3 <- cmdscale(odl,k=3)

library(rgl)
rgl.open()
rgl.bg(color="black")
rgl.spheres(mds.wynik.3[,1],mds.wynik.3[,2],mds.wynik.3[,3],r=.01,col="blue",alpha=.35)
rgl.texts  (mds.wynik.3[,1],mds.wynik.3[,2],mds.wynik.3[,3],nazwy,adj=.5,color="red");

