package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.domain.Municipality;

import java.util.ArrayList;
import java.util.List;

public class MunicipalityContractMapper {
    public Iterable<MunicipalityContract> map(Iterable<Municipality> municipalities) {
        List<MunicipalityContract> municipalityContract = new ArrayList<>();
        municipalities.forEach(municipality -> {
            municipalityContract.add(fromMunicipality(municipality));
        });
        return municipalityContract;
    }

    public MunicipalityContract fromMunicipality(Municipality municipality) {
        MunicipalityContract municipalityContract = new MunicipalityContract();
        municipalityContract.setId(municipality.getId());
        municipalityContract.setName(municipality.getName());
        municipalityContract.setState(municipality.getState().getName());
        municipalityContract.setCityClass(municipality.getCityClass());
        municipalityContract.setPopulation(municipality.getPopulation());
        return municipalityContract;
    }
}
