package org.mbs.budgetplannerserver.domain.code;


import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "function_group")
public class FunctionGroup extends BaseModel {
    private String code;
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "functionGroup")
    private List<Function> functions;

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

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }
}
