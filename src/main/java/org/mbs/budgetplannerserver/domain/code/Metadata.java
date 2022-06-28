package org.mbs.budgetplannerserver.domain.code;


public class Metadata {
    private Iterable<FunctionGroup> functionGroups;
    private Iterable<MajorHeadGroup> majorHeadGroups;

    public Metadata(Iterable<FunctionGroup> functionGroups, Iterable<MajorHeadGroup> majorHeadGroups) {
        this.functionGroups = functionGroups;
        this.majorHeadGroups = majorHeadGroups;
    }

    public Iterable<FunctionGroup> getFunctionGroups() {
        return functionGroups;
    }

    public void setFunctionGroups(Iterable<FunctionGroup> functionGroups) {
        this.functionGroups = functionGroups;
    }

    public Iterable<MajorHeadGroup> getMajorHeadGroups() {
        return majorHeadGroups;
    }

    public void setMajorHeadGroups(Iterable<MajorHeadGroup> majorHeadGroups) {
        this.majorHeadGroups = majorHeadGroups;
    }
}
