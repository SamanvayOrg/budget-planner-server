package org.mbs.budgetplannerserver.domain;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "budget_status_audit")
@SQLDelete(sql = "UPDATE budget_status_audit SET is_voided = true WHERE id=?")
@Where(clause = "is_voided=false")
public class BudgetStatusAudit extends BaseModel{
    @ManyToOne(targetEntity = Budget.class)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    private BudgetStatus currentBudgetStatus;

    private BudgetStatus prevBudgetStatus;

    @Column(name="created_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public BudgetStatusAudit(Budget budget, User user, BudgetStatus currentBudgetStatus, BudgetStatus prevBudgetStatus) {
        this.budget = budget;
        this.user = user;
        this.currentBudgetStatus = currentBudgetStatus;
        this.prevBudgetStatus = prevBudgetStatus;
    }

    public BudgetStatusAudit() {

    }

    public Budget getBudget() {
        return budget;
    }

    public User getUser() {
        return user;
    }

    public BudgetStatus getCurrentBudgetStatus() {
        return currentBudgetStatus;
    }

    public BudgetStatus getPrevBudgetStatus() {
        return prevBudgetStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
