echo
echo Running makefile
echo
make all
echo
echo 1st error example - second declaration
echo
./bin/compiler < ./bin/error1.imp
echo
echo
echo 2nd error example - const as lvalue
echo
./bin/compiler < ./bin/error2.imp
echo
echo
echo 3rd error example - reading into const
echo
./bin/compiler < ./bin/error3.imp
echo
echo
echo 4th error example - undeclared identifier
echo
./bin/compiler < ./bin/error4.imp
echo
echo
echo 1st example - GCD
echo
./bin/compiler < ./bin/example1.imp > ./bin/result
./bin/interpreter ./bin/result
echo
echo 2nd example - factorization
./bin/compiler < ./bin/example2.imp > ./bin/result
./bin/interpreter ./bin/result
echo
echo 3rd example - a^b % c
./bin/compiler < ./bin/example3.imp > ./bin/result
./bin/interpreter ./bin/result
echo
echo Cleaning up
echo
rm -f ./bin/result
make clean
