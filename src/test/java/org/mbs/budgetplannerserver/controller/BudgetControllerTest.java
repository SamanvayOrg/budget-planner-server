package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.domain.Budgets;
import org.mbs.budgetplannerserver.repository.BudgetsRepository;
import org.mbs.budgetplannerserver.service.BudgetService;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetControllerTest {

	@Test
	public void shouldCallBudgetRepositoryToRetrieveBudgets() {
		BudgetsRepository budgetsRepository = mock(BudgetsRepository.class);
		Budgets returnedBudgets = new Budgets();
		when(budgetsRepository.findAll()).thenReturn(Arrays.asList(returnedBudgets));
		Iterable<Budgets> budgets = new BudgetController(budgetsRepository).getBudgets();
		assertThat(budgets, hasItem(returnedBudgets));
	}

//	@Test
//	public void shouldRetrieveBudgetByYearThroughService() {
//		BudgetService budgetService = mock(BudgetService.class);
////		when(budgetService.get())
//	}
}