package org.mbs.budgetplannerserver.contract;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MajorHeadContract {
    private String name;
    private String code;
    private BigDecimal majorHeadDisplayOrder;
    private List<MinorHeadContract> minorHeads= new ArrayList<>();

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

    public List<MinorHeadContract> getMinorHeads() {
        return minorHeads;
    }

    public void setMinorHeads(List<MinorHeadContract> minorHeads) {
        this.minorHeads = minorHeads;
    }

    public void addMinorHeadContract(MinorHeadContract minorHeadContract) {
        this.getMinorHeads().add(minorHeadContract);
    }

    public BigDecimal getMajorHeadDisplayOrder() {
        return majorHeadDisplayOrder;
    }

    public void setMajorHeadDisplayOrder(BigDecimal majorHeadDisplayOrder) {
        this.majorHeadDisplayOrder = majorHeadDisplayOrder;
    }
}
