package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.MunicipalityContract;
import org.mbs.budgetplannerserver.domain.Municipality;

public class MunicipalityContractMapper {
    public MunicipalityContract map(Municipality municipality) {
        MunicipalityContract municipalityContract = new MunicipalityContract();
        municipalityContract.setId(municipality.getId());
        municipalityContract.setName(municipality.getName());
        municipalityContract.setState(municipality.getState().getName());
        municipalityContract.setCityClass(municipality.getCityClass());
        municipalityContract.setPopulation(municipality.getPopulation());
        return municipalityContract;
    }
}
