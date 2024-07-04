package org.mbs.budgetplannerserver.domain.code;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "detailed_head")
@SQLDelete(sql = "UPDATE detailed_head SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class DetailedHead extends BaseModel {
    private String code;
    private String name;

    @ManyToOne(targetEntity = MinorHead.class)
    @JoinColumn(name = "minor_head_id")
    private MinorHead minorHead;

    private String fullCode;

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

    public MinorHead getMinorHead() {
        return minorHead;
    }

    public void setMinorHead(MinorHead minorHead) {
        this.minorHead = minorHead;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public MajorHead getMajorHead() {
        return getMinorHead().getMajorHead();
    }

    public MajorHeadGroup getMajorHeadGroup() {
        return getMinorHead().getMajorHeadGroup();
    }
}
