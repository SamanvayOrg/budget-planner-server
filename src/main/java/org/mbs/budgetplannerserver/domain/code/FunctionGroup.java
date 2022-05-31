package org.mbs.budgetplannerserver.domain.code;


import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "function_group")
public class FunctionGroup extends BaseModel {
    private String code;
    private String name;

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
}
