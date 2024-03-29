package org.mbs.budgetplannerserver.domain.code;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "function")
@BatchSize(size = 50)
@SQLDelete(sql = "UPDATE function SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class Function extends BaseModel {

    @ManyToOne(targetEntity = FunctionGroup.class)
    @JoinColumn(name = "function_group_id")
    private FunctionGroup functionGroup;

    private String code;
    private String name;
    private String fullCode;

    public FunctionGroup getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(FunctionGroup functionGroup) {
        this.functionGroup = functionGroup;
    }

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

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }
}
