package org.mbs.budgetplannerserver.domain.code;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "major_head_group")
@SQLDelete(sql = "UPDATE major_head_group SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class MajorHeadGroup extends BaseModel {
    private String code;
    private String name;
    private BigDecimal displayOrder;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "majorHeadGroup")
    private List<MajorHead> majorHeads;

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

    public BigDecimal getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(BigDecimal displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<MajorHead> getMajorHeads() {
        return majorHeads;
    }

    public void setMajorHeads(List<MajorHead> majorHeads) {
        this.majorHeads = majorHeads;
    }
}
