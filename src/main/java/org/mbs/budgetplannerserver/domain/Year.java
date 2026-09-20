package org.mbs.budgetplannerserver.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class Year {
    public Integer year;

    public Year(Integer year) {
        this.year = year;
    }

    public Year() {
        this.year = currentYear();
    }

    public static Integer currentYear() {
        return currentYearFor(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    // Returns the financial year whose budget is currently being PREPARED — which is the
    // financial year *after* the one the given date falls in. That "+1" is deliberate, not
    // an off-by-one: a ULB prepares next year's budget during the current year, so at any
    // moment the budget being worked on is for the upcoming FY.
    //
    // Corroborated by the rest of the domain: for a Budget with financialYear = Y,
    // BudgetLineContractMapper maps PREV_YEAR (= Y-1) into the contract's currentYear*
    // fields and BudgetLine#canBeDeleted calls that same line currentYearBudgetLine — i.e.
    // Y-1 is the year in progress and Y is the year being planned. The UI columns agree:
    // a Y=2026 budget shows "2025-2026 Actuals for 8 months" next to "2026-2027 Budgeted
    // amount", and 8 months of actuals only exist partway through the prior year.
    //
    // Do not "correct" this to return the in-progress year. Doing so blocks creating next
    // year's budget during Jan-Mar, which is exactly the statutory budget-preparation window.
    static Integer currentYearFor(LocalDateTime date) {
        int yearOfDate = date.getYear();
        int monthValue = date.getMonthValue();
        return monthValue <= 3 ? yearOfDate : yearOfDate + 1;
    }

    public Integer getYear() {
        return Optional.of(year).orElse(currentYear());
    }
}
