package org.mbs.budgetplannerserver.domain.code;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.mbs.budgetplannerserver.domain.BaseModel;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "minor_head")
@BatchSize(size = 50)
@SQLDelete(sql = "UPDATE minor_head SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class MinorHead extends BaseModel {
    private String code;
    private String name;
    private String category;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "minorHead")
    private List<DetailedHead> detailedHeads;

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

    public List<DetailedHead> getDetailedHeads() {
        return detailedHeads;
    }

    public void setDetailedHeads(List<DetailedHead> detailedHeads) {
        this.detailedHeads = detailedHeads;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public MajorHeadGroup getMajorHeadGroup() {
        return getMajorHead().getMajorHeadGroup();
    }
}
