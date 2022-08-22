package org.mbs.budgetplannerserver.contract;

import org.mbs.budgetplannerserver.domain.BudgetStatus;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetStatusAuditContract {

        private UserContract userContract;

        private String currentBudgetStatus;

        private String prevBudgetStatus;

        private List<String> allowedNextBudgetStatuses;

        private Date createdAt;

        public UserContract getUserContract() {
                return userContract;
        }

        public void setUserContract(UserContract userContract) {
                this.userContract = userContract;
        }

        public String getCurrentBudgetStatus() {
                return currentBudgetStatus;
        }

        public void setCurrentBudgetStatus(String currentBudgetStatus) {
                this.currentBudgetStatus = currentBudgetStatus;
        }

        public String getPrevBudgetStatus() {
                return prevBudgetStatus;
        }

        public void setPrevBudgetStatus(String prevBudgetStatus) {
                this.prevBudgetStatus = prevBudgetStatus;
        }

        public List<String> getAllowedNextBudgetStatuses() {
                return allowedNextBudgetStatuses;
        }

        public void setAllowedNextBudgetStatuses(List<BudgetStatus> allowedNextBudgetStatuses) {
                this.allowedNextBudgetStatuses = allowedNextBudgetStatuses.stream().map(BudgetStatus::toString).collect(Collectors.toList());
        }

        public Date getCreatedAt() {
                return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
                this.createdAt = createdAt;
        }
}
