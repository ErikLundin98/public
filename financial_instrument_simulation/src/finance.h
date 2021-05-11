//#pragma once

#include <vector>
#include <string>
#include <ostream>
#include <cmath>
#include <random>
#include <memory>
#include <fstream>

inline double normCDF(double val) {
    return 0.5 * erfc(-val * std::sqrt(0.5));
}
template <typename T> 
inline int sign(T val) {
    return (T(0) < val) - (val < T(0));
}

struct DateTime {
    static const unsigned int* DAYS_PER_MONTH;
    int y;
    int m;
    int d;
    double time;
    DateTime();
    DateTime(unsigned int y, unsigned int m, unsigned int d);
    DateTime(std::string const dstr);
    double operator-(const DateTime & d1) const;
    void operator+=(const double & dt);
};

inline std::ostream& operator<<(std::ostream& os, const DateTime& d) {
    
    os << d.y << '-';
    if(d.m < 10) os << '0';
    os << d.m << '-';
    if(d.d < 10) os << '0';
    os << d.d;
    return os;
};

class Stock {

    public:
        double S;
        double S_0;
        double sigma;
        double mu;
        double rf;
        Stock(double S_0, double sigma, double mu, double rf);
        void step(double dt, double W_t);
};

class Derivative {
    protected:
        double value;
        Stock* underlying;
    public:
        virtual void step(DateTime t) = 0;
        double price();
};

// Virtual class, option
class Option : public Derivative {
    protected:
        double K; // Strike 
        DateTime T; // Expiry date
        double d1(DateTime t);
        double d2(double d1, DateTime t);
        
    public: 
        inline Option() = default;
        Option(Stock* underlying, const double & K, const DateTime & T);
        inline virtual ~Option() {}
        using Derivative::price;
        virtual void step(DateTime t) = 0;
        

    
};
// European Call option
class CallOption : public Option {
    public:
        using Option::Option;
        void step(DateTime t);
        using Option::price;
};
// European Put option
class PutOption : public Option {
    public:
        using Option::Option;
        void step(DateTime t);
        using Option::price;
};

struct Simulator {
    
    std::vector<Stock*> stocks;
    std::vector<Derivative*> derivatives;
    DateTime current_time;
    double dt;

    std::normal_distribution<double> distribution;
    std::default_random_engine generator;

    std::ofstream file;


    Simulator(DateTime start, double dt);
    void addStock(Stock* ptr);
    //Stock** addStock(double S_0, double mu, double sigma, double rf);
    void addDerivative(Derivative* ptr);
    //void addDerivative()
    void newSeed();
    std::string step();
    std::string initialPrices();
    std::ofstream* openFile(char const filename[]);
    void closeFile();
};