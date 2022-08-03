package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.CityClassContract;
import org.mbs.budgetplannerserver.domain.CityClass;

import java.util.ArrayList;
import java.util.List;

public class CityClassContractMapper {
    public Iterable<CityClassContract> map(Iterable<CityClass> cityClass) {
        List<CityClassContract> cityClassContract = new ArrayList<>();
        cityClass.forEach(e -> {
            cityClassContract.add(fromCityClass(e));
        });
        return cityClassContract;
    }

    public CityClassContract fromCityClass(CityClass cityClass) {
        CityClassContract cityClassContract = new CityClassContract();
        cityClassContract.setName(cityClass.getName());
        return cityClassContract;
    }
}
