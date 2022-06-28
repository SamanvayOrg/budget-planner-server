package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class MetadataContract {
    private List<FunctionGroupContract> functionGroups = new ArrayList<>();
    private List<MajorHeadGroupContract> majorHeadGroups = new ArrayList<>();

    public List<FunctionGroupContract> getFunctionGroups() {
        return functionGroups;
    }

    public void setFunctionGroups(List<FunctionGroupContract> functionGroups) {
        this.functionGroups = functionGroups;
    }

    public List<MajorHeadGroupContract> getMajorHeadGroups() {
        return majorHeadGroups;
    }

    public void setMajorHeadGroups(List<MajorHeadGroupContract> majorHeadGroups) {
        this.majorHeadGroups = majorHeadGroups;
    }

    public void addFunctionGroup(FunctionGroupContract functionGroupContract) {
        this.getFunctionGroups().add(functionGroupContract);
    }

    public void addMajorHeadGroup(MajorHeadGroupContract majorHeadGroupContract) {
        this.getMajorHeadGroups().add(majorHeadGroupContract);
    }
}
