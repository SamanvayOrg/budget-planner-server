package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class MajorHeadGroupContract {

    private String name;
    private String code;
    private List<MajorHeadContract> majorHeads = new ArrayList<>();

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

    public List<MajorHeadContract> getMajorHeads() {
        return majorHeads;
    }

    public void setMajorHeads(List<MajorHeadContract> majorHeads) {
        this.majorHeads = majorHeads;
    }

    public void addMajorGroup(MajorHeadContract majorHeadContract) {
        this.getMajorHeads().add(majorHeadContract);
    }
}
