package org.example;

import java.util.Date;

public class Vac {
    private String vacId;
    private String model;
    private double price;
    private double maxPower;
    private Date releaseDate;

    public Vac(){};

    public Vac(String vacId, String model, double price, double maxPower, Date releaseDate) {
        this.vacId = vacId;
        this.model = model;
        this.price = price;
        this.maxPower = maxPower;
        this.releaseDate = releaseDate;
    }

    public String getVacId() { return vacId; }
    public void setVacId(String vacId) { this.vacId = vacId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getMaxPower() { return maxPower; }
    public void setMaxPower(double maxPower) { this.maxPower = maxPower; }
    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    @Override
    public String toString() {
        return "ID: " + vacId + ", Model: " + model + ", Price: " + price + ", Max Power: " + maxPower +
                " W, Release Date: " + releaseDate;
    }
}
