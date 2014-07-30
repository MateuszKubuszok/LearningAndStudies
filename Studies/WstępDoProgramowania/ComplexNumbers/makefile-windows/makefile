all: ../bin/complex_example-stat ../bin/complex_example-dyn ../bin/equations-stat ../bin/equations-dyn



# ------------------Program przykladowy biblioteki liczb zespolonych------------------


# Statycznie linkowany program przykladowy liczb zespolonych

../bin/complex_example-stat: ../obj/main-complex_example.o ../lib/complex.a
	gcc -Wall -ansi -pedantic -static ../obj/main-complex_example.o ../lib/complex.a -o ../bin/complex_example-stat -lm


# Dynamicznie linkowany program przykladowy liczb zespolonych

../bin/complex_example-dyn: ../obj/main-complex_example.o ../lib/complex.dll
	gcc -Wall -ansi -pedantic ../obj/main-complex_example.o -o ../bin/complex_example-dyn -L../lib -lcomplex -lm



# -----------------------------Rozwiazania ukladow rownan-----------------------------


# Statycznie linkowane rozwiazania ukladkow rownan liniowych

../bin/equations-stat: ../obj/main-equations.o ../lib/complex.a ../lib/matrixc.a
	gcc -Wall -ansi -pedantic -static ../obj/main-equations.o ../lib/matrixc.a ../lib/complex.a -o ../bin/equations-stat -lm


# Dynamicznie linowane rozwiazania ukladow rownan liniowych

../bin/equations-dyn: ../obj/main-equations.o ../lib/complex.dll ../lib/matrixc.dll
	gcc -Wall -ansi -pedantic ../obj/main-equations.o -o ../bin/equations-dyn -L../lib -lmatrixc -lcomplex -lm



# ------------------------Kompilacje funkcji main () programow------------------------


../obj/main-complex_example.o: ../src/main-complex_example.c
	gcc -Wall -ansi -pedantic -I../include -c ../src/main-complex_example.c -o ../obj/main-complex_example.o

../obj/main-equations.o: ../src/main-equations.c
	gcc -Wall -ansi -pedantic -I../include -c ../src/main-equations.c -o ../obj/main-equations.o



# -----------------------------------Pliki biliotek-----------------------------------


# Statyczna biblioteka liczb zespolonych

../lib/complex.a: ../obj/complex-stat.o
	ar r ../lib/complex.a ../obj/complex-stat.o

../obj/complex-stat.o: ../src/complex.c
	gcc -Wall -ansi -pedantic -I../include -c ../src/complex.c -o ../obj/complex-stat.o


# Statyczna biblioteka macierzy zespolonych

../lib/matrixc.a: ../obj/matrixc-stat.o
	ar r ../lib/matrixc.a ../obj/matrixc-stat.o

../obj/matrixc-stat.o: ../src/matrixc.c
	gcc -Wall -ansi -pedantic -I../include -c ../src/matrixc.c -o ../obj/matrixc-stat.o


# Dynamiczna biblioteka liczb zespolonych

../lib/complex.dll: ../obj/complex-dyn.o
	gcc -shared -Wl,-soname,../lib/complex.dll ../obj/complex-dyn.o -o ../lib/complex.dll

../obj/complex-dyn.o: ../src/complex.c
	gcc -fPIC -Wall -ansi -pedantic -I../include -c ../src/complex.c -o ../obj/complex-dyn.o


# Dynamiczna biblioteka macierzy zespolonych

../lib/matrixc.dll: ../obj/matrixc-dyn.o ../lib/complex.dll
	gcc -shared -Wl,-soname,../lib/matrixc.dll ../obj/matrixc-dyn.o -o ../lib/matrixc.dll -L../lib -lcomplex

../obj/matrixc-dyn.o: ../src/matrixc.c
	gcc -fPIC -Wall -ansi -pedantic -I../include -c ../src/matrixc.c -o ../obj/matrixc-dyn.o

