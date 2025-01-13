package org.example;

import java.util.Date;

public class Builders {

    public interface Builder<T> {
        Builder setId(String id);

        Builder setModel(String model);

        Builder setPrice(double price);

        Builder setMaxPower(double maxPower);

        Builder setReleaseDate(Date releaseDate);

        T build();
    }

    public static class VacBuilder implements Builder<Vac> {
        private String id;
        private String model;
        private double price;
        private double maxPower;
        private Date releaseDate;


        @Override
        public VacBuilder setId(String id) {
            this.id = id;
            return this;
        }

        @Override
        public VacBuilder setModel(String model) {
            this.model = model;
            return this;
        }

        @Override
        public VacBuilder setPrice(double price) {
            this.price = price;
            return this;
        }

        @Override
        public VacBuilder setMaxPower(double maxPower) {
            this.maxPower = maxPower;
            return this;
        }

        @Override
        public VacBuilder setReleaseDate(Date releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        @Override
        public Vac build() {
            return new Vac(id, model, price, maxPower, releaseDate) {
            };
        }
    }

    public static class RobotBuilder implements Builder<Robot> {
        private String id;
        private String model;
        private double price;
        private double maxPower;
        private Date releaseDate;


        @Override
        public RobotBuilder setId(String id) {
            this.id = id;
            return this;
        }

        @Override
        public RobotBuilder setModel(String model) {
            this.model = model;
            return this;
        }

        @Override
        public RobotBuilder setPrice(double price) {
            this.price = price;
            return this;
        }

        @Override
        public RobotBuilder setMaxPower(double maxPower) {
            this.maxPower = maxPower;
            return this;
        }

        @Override
        public RobotBuilder setReleaseDate(Date releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        @Override
        public Robot build() {
            return new Robot(id, model, price, maxPower, releaseDate) ;
        }
    }
}


