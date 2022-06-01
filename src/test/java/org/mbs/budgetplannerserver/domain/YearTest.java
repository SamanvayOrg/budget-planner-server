package org.mbs.budgetplannerserver.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class YearTest {

    @Test
    public void shouldCalculateCurrentYearAccurately() {
        LocalDateTime january2021 = LocalDateTime.of(2021, 1, 1, 0, 0);
        assertThat(new Year().currentYearFor(january2021), is(2020));
    }

}