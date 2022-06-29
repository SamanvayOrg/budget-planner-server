package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class MinorHeadContract {
    private String name;
    private String code;
    private List<DetailedHeadContract> detailedHeads = new ArrayList<>();

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

    public List<DetailedHeadContract> getDetailedHeads() {
        return detailedHeads;
    }

    public void setDetailedHeads(List<DetailedHeadContract> detailedHeadContracts) {
        this.detailedHeads = detailedHeads;
    }

    public void addMinorHead(DetailedHeadContract detailedHeadContract) {
        this.getDetailedHeads().add(detailedHeadContract);
    }
}
