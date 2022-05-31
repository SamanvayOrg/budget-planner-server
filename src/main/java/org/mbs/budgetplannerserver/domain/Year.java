package org.mbs.budgetplannerserver.domain;

import java.util.Optional;

public class Year {
    public Integer year;

    public Year(Integer year) {
        this.year = year;
    }

    public Integer currentYear() {
        return 2022;
    }

    public Integer getYear() {
        return Optional.of(year).orElse(currentYear());
    }
}
