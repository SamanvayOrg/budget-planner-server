package org.mbs.budgetplannerserver.contract;

import org.mbs.budgetplannerserver.domain.BudgetStatus;

import java.util.Date;
import java.util.List;

public class BudgetStatusAuditContract {

        private UserContract userContract;

        private BudgetStatus currentBudgetStatus;

        private BudgetStatus prevBudgetStatus;

        private List<BudgetStatus> allowedNextBudgetStatuses;

        private Date createdAt;

        public UserContract getUserContract() {
                return userContract;
        }

        public void setUserContract(UserContract userContract) {
                this.userContract = userContract;
        }

        public BudgetStatus getCurrentBudgetStatus() {
                return currentBudgetStatus;
        }

        public void setCurrentBudgetStatus(BudgetStatus currentBudgetStatus) {
                this.currentBudgetStatus = currentBudgetStatus;
        }

        public BudgetStatus getPrevBudgetStatus() {
                return prevBudgetStatus;
        }

        public void setPrevBudgetStatus(BudgetStatus prevBudgetStatus) {
                this.prevBudgetStatus = prevBudgetStatus;
        }

        public List<BudgetStatus> getAllowedNextBudgetStatuses() {
                return allowedNextBudgetStatuses;
        }

        public void setAllowedNextBudgetStatuses(List<BudgetStatus> allowedNextBudgetStatuses) {
                this.allowedNextBudgetStatuses = allowedNextBudgetStatuses;
        }

        public Date getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
                this.createdAt = createdAt;
        }
}
