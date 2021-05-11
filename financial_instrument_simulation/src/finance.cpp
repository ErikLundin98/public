
#include <finance.h>
#include <cmath>
#include <string>
#include <sstream>
#include <iostream>
#include <memory>
#include <fstream>

static const unsigned int days_per_month[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
const unsigned int* DateTime::DAYS_PER_MONTH = days_per_month;

DateTime::DateTime() {
    this->y = 2021;
    this->m = 05;
    this->d = 10;
    this->time = 0;
};

DateTime::DateTime(unsigned int y, unsigned int m, unsigned int d) {
    this->y = y;
    this->m = m;
    this->d = d;
    this->time = 0;
};

//Expects a string of format YYYY-MM-DD
DateTime::DateTime(std::string const dstr) {
    this->y = std::stoi(dstr.substr(0, 4));
    this->m = std::stoi(dstr.substr(5, 2));
    this->d = std::stoi(dstr.substr(8, 2));
    this->time = 0;
};

//Very simple subtraction between two dates. Only works properly if d2 is smaller than d1
double DateTime::operator-(const DateTime & d2) const {
    int days_between{0};
    for(int i{0} ; i != m - d2.m ; i+=sign(m - d2.m)) {
        days_between += sign(m - d2.m)*DAYS_PER_MONTH[d2.m-1+i];
    }
    days_between += d - d2.d;
    double res = days_between*1.0/365+y-d2.y+time-d2.time;
    return res;
};

//Very simple += operator for date. Takes a double representing time step in years
void DateTime::operator+=(const double & dt) {
    if(dt < 0) return;
    double time = ((dt*365.0) - floor(dt*365.0))/365.0;
    int days = floor(dt*365) + d;
    while(days > DAYS_PER_MONTH[m-1]) {
        days -= DAYS_PER_MONTH[m-1];
        m++;
        if(m > 12) {
            m = 1;
            y++;
        }
    }
    d = days;
}


Stock::Stock(double S_0, double mu, double sigma, double rf) {
    this->S_0 = S_0;
    this->mu = mu;
    this->sigma = sigma;
    this->rf = rf;
    this->S = this->S_0;
};

void Stock::step(double dt, double W_t) {
    S = S*std::exp((mu-sigma*sigma/2.0)*dt + sigma*W_t);
};

double Derivative::price() {
    return value;
}

double Option::d1(DateTime t) {
    return (std::log(underlying->S/K) + (underlying->rf + underlying->sigma*underlying->sigma/2)*(T-t))/(underlying->sigma*std::sqrt(T-t));
};

double Option::d2(double d1, DateTime t) {
    return d1 - underlying->sigma*std::sqrt(T-t);
}


Option::Option(Stock* underlying, const double & K, const DateTime & T) {
    this->underlying = underlying;
    this->K = K;
    this->T = T;
};

void CallOption::step(DateTime t) {
    double d1 = this->d1(t);
    double d2 = this->d2(d1, t);
    value = underlying->S*normCDF(d1) - K*normCDF(d2)*std::exp(-underlying->rf*(T-t));
    //std::cout << "Call: t" << t << " T-t " << T-t << std::endl;
}

void PutOption::step(DateTime t) {
    double d1 = this->d1(t);
    double d2 = this->d2(d1, t);
    value = K*normCDF(-d2)*std::exp(-underlying->rf*(T-t)) - underlying->S*normCDF(-d1);
}

Simulator::Simulator(DateTime start, double dt) {
    this->current_time = start;
    this->stocks = std::vector<Stock*>();
    this->derivatives = std::vector<Derivative*>();
    this->distribution = std::normal_distribution<double>(0, std::sqrt(dt));
    this->generator = std::default_random_engine();
    this->dt = dt;
};

void Simulator::addStock(Stock* ptr) {
    stocks.push_back(ptr);
};
/*Stock** Simulator::addStock(double S_0, double mu, double sigma, double rf) {
    Stock* ptr = new Stock{S_0, mu, sigma, rf};
    stocks.push_back(ptr);
    return &ptr;
}*/
void Simulator::addDerivative(Derivative* ptr) {
    derivatives.push_back(ptr);
};

std::string Simulator::step() {
    current_time += dt;
    double W_t;
    std::ostringstream os;
    os << current_time << '\t';
    for(auto it{stocks.begin()} ; it != stocks.end() ; ++it) {
        W_t = distribution(generator);
        (*it)->step(this->dt, W_t);
        os << (*it)->S << '\t';
    }
    for(auto it{derivatives.begin()} ; it != derivatives.end() ; ++it) {
        (*it)->step(current_time);
        os << (*it)->price() << '\t';
    }
    return os.str();
};

std::string Simulator::initialPrices() {
    std::ostringstream os;
    os << current_time << '\t';
    for(auto it{stocks.begin()} ; it != stocks.end() ; ++it) {
        os << (*it)->S << '\t';
    }
    for(auto it{derivatives.begin()} ; it != derivatives.end() ; ++it) {
        (*it)->step(current_time);
        os << (*it)->price() << '\t';
    }
    return os.str();
}

std::ofstream* Simulator::openFile(char const filename[]) {
    file.open(filename);
    return &file;
}

void Simulator::closeFile() {
    file.close();
}


