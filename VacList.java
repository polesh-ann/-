package org.example;

import java.util.ArrayList;
import java.util.List;

public class VacList<T extends Vac> extends VacStorage<T> {
    private List<T> vacs = new ArrayList<>();

    @Override
    public void addVac(T vac) {
        vacs.add(vac);
    }

    @Override
    public void removeVac(T vac) {
        vacs.remove(vac);
    }

    @Override
    public void updateVac(T vac) {
        int index = vacs.indexOf(vac);
        if (index != -1) {
            vacs.set(index, vac);
        }
    }

    @Override
    public List<T> getVacs() {
        return vacs;
    }
}


