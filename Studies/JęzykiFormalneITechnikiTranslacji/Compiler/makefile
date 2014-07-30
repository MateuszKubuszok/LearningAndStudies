all : ./bin/interpreter ./bin/compiler

./bin/interpreter : ./src/interpreter-cln.cc
	g++ -o ./bin/interpreter ./src/interpreter-cln.cc -lcln

./bin/compiler : ./tmp/compiler.lexer.c ./tmp/compiler.parser.c ./tmp/compiler.parser.h ./src/compiler.main.c ./src/compiler.node_builder.c ./src/compiler.node_builder.h ./src/compiler.generator.c ./src/compiler.generator.h ./libbigd/bigd.h ./libbigd/bigdigits.h ./libbigd/bigdtypes.h ./libbigd/bigd.c ./libbigd/bigdigits.c 
	gcc -I./src -I./tmp -I./libbigd ./libbigd/bigd.c ./libbigd/bigdigits.c  ./src/compiler.node_builder.c ./src/compiler.generator.c ./tmp/compiler.parser.c ./tmp/compiler.lexer.c ./src/compiler.main.c -lfl -o ./bin/compiler

./tmp/compiler.lexer.c : ./src/compiler.lexer.l
	flex -o ./tmp/compiler.lexer.c ./src/compiler.lexer.l

./tmp/compiler.parser.c ./tmp/compiler.parser.h : ./src/compiler.parser.y
	bison --defines=./tmp/compiler.parser.h -o ./tmp/compiler.parser.c ./src/compiler.parser.y

clean :
	rm -f ./tmp/compiler.lexer.c ./tmp/compiler.parser.c ./tmp/compiler.parser.h ./bin/compiler ./bin/interpreter
