package org.example;

import java.util.Date;

public class Robot extends Vac {

    public Robot(){}

    public Robot(String vacId, String model, double price, double maxPower, Date releaseDate) {
        super(vacId, model, price, maxPower, releaseDate);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

