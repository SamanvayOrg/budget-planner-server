package org.mbs.budgetplannerserver.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BudgetTest {

    private BudgetLine withName(String name) {
        BudgetLine budgetLine = new BudgetLine();
        budgetLine.setName(name);
        return budgetLine;
    }
}