package org.mbs.budgetplannerserver.domain.code;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "major_head")
@BatchSize(size = 50)
@SQLDelete(sql = "UPDATE major_head SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class MajorHead extends BaseModel {
    private String code;
    private String name;
    private BigDecimal displayOrder;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "majorHead")
    private List<MinorHead> minorHeads;

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

    public List<MinorHead> getMinorHeads() {
        return minorHeads;
    }

    public void setMinorHeads(List<MinorHead> minorHeads) {
        this.minorHeads = minorHeads;
    }

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
    }

}
