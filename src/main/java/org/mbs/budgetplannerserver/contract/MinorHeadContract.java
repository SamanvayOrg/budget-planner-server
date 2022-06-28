package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class MinorHeadContract {
    private String name;
    private String code;
    private List<DetailedHeadContract> detailedHeadContracts = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DetailedHeadContract> getDetailedHeadContracts() {
        return detailedHeadContracts;
    }

    public void setDetailedHeadContracts(List<DetailedHeadContract> detailedHeadContracts) {
        this.detailedHeadContracts = detailedHeadContracts;
    }

    public void addMinorHead(DetailedHeadContract detailedHeadContract) {
        this.getDetailedHeadContracts().add(detailedHeadContract);
    }
}
