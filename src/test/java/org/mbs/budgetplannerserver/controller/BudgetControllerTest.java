package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.repository.BudgetsRepository;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetControllerTest {

	@Test
	public void shouldCallBudgetRepositoryToRetrieveBudgets() {
		BudgetsRepository budgetsRepository = mock(BudgetsRepository.class);
		Budget returnedBudgets = new Budget();
		when(budgetsRepository.findAll()).thenReturn(Arrays.asList(returnedBudgets));
		Iterable<Budget> budgets = new BudgetController(budgetsRepository).getBudgets();
		assertThat(budgets, hasItem(returnedBudgets));
	}

//	@Test
//	public void shouldRetrieveBudgetByYearThroughService() {
//		BudgetService budgetService = mock(BudgetService.class);
////		when(budgetService.get())
//	}
}