#ifndef GNUPLOT_H
#define GNUPLOT_H

#include <iostream>
#include <string>

class Gnuplot {
    public:
        Gnuplot();
        ~Gnuplot();
        void operator() (const std::string & command);
    protected:
        std::FILE *gnuplotpipe;
};

inline Gnuplot::Gnuplot() {

    #ifdef _WIN32
        gnuplotpipe = _popen("gnuplot -persist", "w");
    #else
        gnuplotpipe = popen("gnuplot", "w");
    #endif

    if(!gnuplotpipe) {
        std::cerr << ("Gnuplot not found.");
    }
}

inline Gnuplot::~Gnuplot() {
    fprintf(gnuplotpipe, "exit\n");
    #ifdef _WIN32
        _pclose(gnuplotpipe);
    #else
        pclose(gnuplotpipe);
    #endif
}
inline void Gnuplot::operator() (const std::string & command) {
    fprintf(gnuplotpipe, "%s\n", command.c_str());
    fflush(gnuplotpipe);
}

#endif