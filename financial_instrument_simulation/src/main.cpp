#include <finance.h>
#include <gnuplot.h>
#include <iostream>
#include <fstream>
#include <string>

#ifdef _WIN32
        #include <windows.h>
    #else
        #include <unistd.h>
    #endif

int main() {
    
    Simulator simulator{DateTime{"2021-05-11"}, 1.0/365.0};
    Stock s{500.0, 0.1, 0.4, 0.001};
    CallOption c{&s, 500.0, DateTime{"2025-05-12"}};
    PutOption p{&s, 500.0, DateTime{"2025-05-12"}};
    simulator.addStock(&s);
    simulator.addDerivative(&c);
    simulator.addDerivative(&p);
    
    Gnuplot gnu;
    std::string newData;
    
    std::ofstream file;
    file.open("data.dat");

    file << "#S, Call, Put\n";
    file << simulator.initialPrices() << '\n';
    file.close();

    int range = 1000;
    gnu("set term qt noraise");
    gnu("set xdata time");
    gnu("set timefmt \"%Y-%m-%d\"");
    gnu("set xtics rotate");
    gnu("set yrange[0:*]");
    gnu("plot \"data.dat\" u 1:2 w lines title \"underlying\",\\");
    gnu("\"data.dat\" u 1:3 w lines title \"call\",\\");
    gnu("\"data.dat\" u 1:4 w lines title \"put\""); 

    
    for(int i = 0 ; i < range ; ++i) {
        file.open("data.dat", std::ios_base::app);
        newData = simulator.step();
        file << newData << '\n';
        
        #ifdef _WIN32
            Sleep(25);
        #else
            usleep(500000)
        #endif
        file.close();
        gnu("replot");
    }
    gnu("exit");

    
}