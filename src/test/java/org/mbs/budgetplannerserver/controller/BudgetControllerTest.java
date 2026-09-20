package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.contract.BudgetContract;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.domain.BudgetStatus;
import org.mbs.budgetplannerserver.domain.BudgetStatusAudit;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.domain.Year;
import org.mbs.budgetplannerserver.service.BudgetLineService;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.mbs.budgetplannerserver.service.BudgetStatusAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BudgetControllerTest {

    private final BudgetService budgetService = mock(BudgetService.class);
    private final BudgetLineService budgetLineService = mock(BudgetLineService.class);
    private final BudgetStatusAuditService budgetStatusAuditService = mock(BudgetStatusAuditService.class);
    private final BudgetController controller = new BudgetController(budgetService, budgetLineService, budgetStatusAuditService);

    // What *which* year is current means is pinned by YearTest against literal dates; this
    // only asserts that create() accepts exactly that year and hands back the created
    // budget rather than the void it used to return.
    @Test
    public void shouldCreateBudgetForTheCurrentFinancialYearAndReturnIt() {
        int currentYear = Year.currentYear();
        Budget created = minimalBudget(currentYear);
        when(budgetService.getOrCreate(currentYear, 0, true)).thenReturn(created);

        BudgetContract contract = controller.create(currentYear);

        verify(budgetService).getOrCreate(currentYear, 0, true);
        assertThat(contract.getBudgetYear(), is(created.getFinancialYearString()));
    }

    // Both the year *after* and the year *before* must be refused. Only testing one side
    // leaves the assertion true under either definition of "current financial year", which
    // is precisely how an inverted Year#currentYearFor slipped through once already.
    @Test
    public void shouldRejectBudgetCreationForTheYearAfterTheCurrentFinancialYear() {
        assertRejects(Year.currentYear() + 1);
    }

    @Test
    public void shouldRejectBudgetCreationForTheYearBeforeTheCurrentFinancialYear() {
        assertRejects(Year.currentYear() - 1);
    }

    private void assertRejects(int year) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.create(year));

        assertThat(exception.getStatus(), is(HttpStatus.BAD_REQUEST));
        // Reject before doing any DB work — creating a budget seeds every sample line
        // and shouldn't run at all for a request we're about to refuse.
        verify(budgetService, never()).getOrCreate(year, 0, true);
    }

    // Regression test: /budget/actuals took the year straight from the request body and
    // fed it to getOrCreate, so posting budgetYear "2099-00" brought a budget for financial
    // year 2097 into existence — bypassing the restriction create() enforces.
    @Test
    public void shouldNotLetActualsConjureABudgetForAYearTheMunicipalityDoesNotHave() {
        BudgetContract contract = new BudgetContract();
        contract.setBudgetYear("2099-00");
        when(budgetService.getBudgetForFinancialYear(2099)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> controller.updateActuals(contract));

        verify(budgetService, never()).getOrCreate(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    public void shouldRejectAMalformedBudgetYearRatherThanFailingWithAServerError() {
        BudgetContract contract = new BudgetContract();
        contract.setBudgetYear("not-a-year");

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> controller.updateEstimates(contract));

        assertThat(exception.getStatus(), is(HttpStatus.BAD_REQUEST));
    }

    private Budget minimalBudget(int financialYear) {
        Municipality municipality = new Municipality();
        User user = new User();
        user.setMunicipality(municipality);

        Budget budget = new Budget();
        budget.setFinancialYear(financialYear);
        budget.setOpeningBalance(BigDecimal.ZERO);
        budget.setClosingBalance(BigDecimal.ZERO);
        budget.setPopulation(0L);
        budget.setBudgetLines(new HashSet<>());
        budget.setBudgetStatusAudits(List.of(new BudgetStatusAudit(budget, user, BudgetStatus.Draft, null)));
        return budget;
    }
}
