ifeq ($(OS),Windows_NT)
    RM = del /f simulator.* .\output\*.o
else
    RM = rm -f simulator ./output/*.o
endif

# Using windows and g++
simulator: output/main.o output/finance.o
	mkdir output
	g++ output/main.o output/finance.o -o simulator

output/main.o: src/main.cpp
	g++ -c src/main.cpp -o output/main.o -I./src

output/finance.o: src/finance.cpp
	g++ -c src/finance.cpp -o output/finance.o -I./src
clean:
	$(RM)
