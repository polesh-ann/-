package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VacMap<T extends Vac> extends VacStorage<T> {
    private Map<String, T> vacMap = new HashMap<>();

    @Override
    public void addVac(T vac) {
        vacMap.put(vac.getVacId(), vac);
    }

    @Override
    public void removeVac(T vac) {
        vacMap.remove(vac.getVacId());
    }

    @Override
    public void updateVac(T vac) {
        vacMap.put(vac.getVacId(), vac);
    }

    @Override
    public List<T> getVacs() {
        return vacMap.values().stream().collect(Collectors.toList());
    }

    public T getVacById(String vacId) {
        return vacMap.get(vacId);
    }
}


