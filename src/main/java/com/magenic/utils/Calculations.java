package com.magenic.utils;

public class Calculations {

    public double doCalc(double price, double percent, double base) {

        return price * percent / 100.0 * base / 100.0;

    }

}
