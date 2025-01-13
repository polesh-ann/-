package org.example;

import java.util.List;

public abstract class VacStorage<T extends Vac> {
    public abstract void addVac(T vac);
    public abstract void removeVac(T vac);
    public abstract void updateVac(T vac);
    public abstract List<T> getVacs();
}