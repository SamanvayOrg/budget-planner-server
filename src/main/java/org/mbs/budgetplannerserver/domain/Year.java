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

    static Integer currentYearFor(LocalDateTime date) {
        int yearOfDate = date.getYear();
        int monthValue = date.getMonthValue();
        return monthValue <= 3 ? yearOfDate : yearOfDate + 1;
    }

    public Integer getYear() {
        return Optional.of(year).orElse(currentYear());
    }
}
