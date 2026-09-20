package org.mbs.budgetplannerserver.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class YearTest {

    // Year.currentYear() answers "which financial year is being PREPARED right now", not
    // "which financial year are we in". A ULB prepares next year's budget during the
    // current one, so the answer is always the FY after the one the date falls in.
    // See the comment on Year#currentYearFor for the corroborating evidence.

    @Test
    public void aprilToDecemberPreparesTheFinancialYearStartingTheFollowingCalendarYear() {
        // Apr 2026 - Mar 2027 is FY 2026-27; throughout it, the budget being prepared is 2027-28.
        assertThat(Year.currentYearFor(LocalDateTime.of(2026, 4, 1, 0, 0)), is(2027));
        assertThat(Year.currentYearFor(LocalDateTime.of(2026, 8, 18, 0, 0)), is(2027));
        assertThat(Year.currentYearFor(LocalDateTime.of(2026, 12, 31, 0, 0)), is(2027));
    }

    @Test
    public void januaryToMarchStillPreparesTheSameFinancialYearAsTheDecemberBeforeIt() {
        // Jan-Mar 2027 is the tail of FY 2026-27 — the budget-preparation window — and must
        // still resolve to 2027-28, otherwise next year's budget cannot be created when it
        // is actually drawn up.
        assertThat(Year.currentYearFor(LocalDateTime.of(2027, 1, 1, 0, 0)), is(2027));
        assertThat(Year.currentYearFor(LocalDateTime.of(2027, 3, 31, 0, 0)), is(2027));
    }

    @Test
    public void theAnswerAdvancesOnlyWhenANewFinancialYearBeginsInApril() {
        assertThat(Year.currentYearFor(LocalDateTime.of(2027, 3, 31, 23, 59)), is(2027));
        assertThat(Year.currentYearFor(LocalDateTime.of(2027, 4, 1, 0, 0)), is(2028));
    }
}
