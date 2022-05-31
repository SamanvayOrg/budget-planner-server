package org.mbs.budgetplannerserver.domain.code;

import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "major_head")
public class MajorHead extends BaseModel {
    private String code;
    private String name;

    @ManyToOne(targetEntity = MajorHeadGroup.class)
    @JoinColumn(name = "major_head_group_id")
    private MajorHeadGroup majorHeadGroup;

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

    public MajorHeadGroup getMajorHeadGroup() {
        return majorHeadGroup;
    }

    public void setMajorHeadGroup(MajorHeadGroup majorHeadGroup) {
        this.majorHeadGroup = majorHeadGroup;
    }
}
