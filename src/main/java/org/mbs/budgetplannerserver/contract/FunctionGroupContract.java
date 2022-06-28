package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.List;

public class FunctionGroupContract {
    private String code;
    private String name;
    private List<FunctionContract> functions = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FunctionContract> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionContract> functions) {
        this.functions = functions;
    }

    public void addFunction(FunctionContract functionContract) {
        this.getFunctions().add(functionContract);
    }
}
