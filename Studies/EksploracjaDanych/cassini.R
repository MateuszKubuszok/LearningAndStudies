library(mlbench)
dane.cassini <- mlbench.cassini(n=2000)
plot(dane.cassini)
title('Cassini problem')

dane        <- dane.cassini$x
klasy       <- dane.cassini$clases
dane.niepod <- daisy(dane)

dane.agnes.avg      <- agnes(x=dane.niepod,diss=TRUE,method="average")
dane.agnes.single   <- agnes(x=dane.niepod,diss=TRUE,method="single")
dane.agnes.complete <- agnes(x=dane.niepod,diss=TRUE,method="complete")


library(MASS)
library(cluster)

X11()
par(cex=0.6)
plot(dane.agnes.avg,main="AGNES: average linkage")

X11()
par(cex=0.6)
plot(dane.agnes.single,main="AGNES: single linkage")

X11()
par(cex=0.6)
plot(dane.agnes.complete,main="AGNES: complete linkage")


dane.agnes.avg3 <- cutree(dane.agnes.avg, 3)
dane.agnes.single3 <- cutree(dane.agnes.single, 3)
dane.agnes.complete3 <- cutree(dane.agnes.complete, 3)


X11()
par(cex=0.6)
plot(dane,col=dane.agnes.avg3,main="AGNES: average linkage")

X11()
par(cex=0.6)
plot(dane,col=dane.agnes.single3,main="AGNES: single linkage")

X11()
par(cex=0.6)
plot(dane,col=dane.agnes.complete3,main="AGNES: complete linkage")


sil.agnes.avg3      <- silhouette(x=dane.agnes.avg3,     dist=dane.niepod)
sil.agnes.single3   <- silhouette(x=dane.agnes.single3,  dist=dane.niepod)
sil.agnes.complete3 <- silhouette(x=dane.agnes.complete3,dist=dane.niepod)

summary(sil.agnes.avg3)
plot(sil.agnes.avg3)
summary(sil.agnes.single3)
plot(sil.agnes.single3)
summary(sil.agnes.complete3)
plot(sil.agnes.complete3)

