package org.mbs.budgetplannerserver.domain;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public enum BudgetStatus {
    Draft("Draft"), SubmittedToGB("Submitted to GB"), ApprovedByGB("Approved by GBM"), ApprovedByDistrict("Approved by District");

    String name;

    BudgetStatus(String name) {
        this.name = name;
    }

    public List<BudgetStatus> allowedTransitionValues() {
        switch(this) {
            case Draft:
                return asList(SubmittedToGB);
            case SubmittedToGB:
                return asList(ApprovedByGB, Draft);
            case ApprovedByGB:
                return asList(Draft, ApprovedByGB, ApprovedByDistrict);
            case ApprovedByDistrict:
                return asList(Draft);
            default:
                return asList(Draft, SubmittedToGB, ApprovedByGB, ApprovedByDistrict);
        }
    }

    public Boolean isTransitionAllowed(BudgetStatus nextBS) {
        return allowedTransitionValues().contains(nextBS);
    }

    @Override
    public String toString() {
        return name;
    }

    public static BudgetStatus from(String name) {
        return Arrays.stream(BudgetStatus.values()).filter(bs -> bs.name.equalsIgnoreCase(name)).findFirst().orElseThrow(EntityNotFoundException::new);
    }
}
