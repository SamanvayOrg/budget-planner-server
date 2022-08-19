package org.mbs.budgetplannerserver.domain;

import java.util.Arrays;
import java.util.List;

public enum BudgetStatus {
    Draft("Draft"), ApprovedByGBM("Approved by GBM"), ApprovedByDistrict("Approved by District");

    String name;

    BudgetStatus(String name) {
        this.name = name;
    }

    public List<BudgetStatus> allowedTransitionValues() {
        if (this == BudgetStatus.ApprovedByGBM) {
            return Arrays.asList(Draft, ApprovedByDistrict);
        }
        return Arrays.asList(ApprovedByGBM);
    }

    public Boolean isTransitionAllowed(BudgetStatus nextBS) {
        return allowedTransitionValues().contains(nextBS);
    }
}
