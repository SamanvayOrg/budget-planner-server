package org.mbs.budgetplannerserver.domain.code;

import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "minor_head")
public class MinorHead extends BaseModel {
    private String code;
    private String name;

    @ManyToOne(targetEntity = MajorHead.class)
    @JoinColumn(name = "major_head_id")
    private MajorHead majorHead;

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

    public MajorHead getMajorHead() {
        return majorHead;
    }

    public void setMajorHead(MajorHead majorHead) {
        this.majorHead = majorHead;
    }
}
