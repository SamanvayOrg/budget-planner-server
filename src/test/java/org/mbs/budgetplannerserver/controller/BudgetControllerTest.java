package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.Test;
import org.mbs.budgetplannerserver.domain.Budget;
import org.mbs.budgetplannerserver.repository.BudgetRepository;
import org.mbs.budgetplannerserver.service.UserService;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BudgetControllerTest {

	/*@Test
	public void shouldCallBudgetRepositoryToRetrieveBudgets() {
		BudgetRepository budgetsRepository = mock(BudgetRepository.class);
		UserService userService = mock(UserService.class);
		Budget returnedBudgets = new Budget();
		when(budgetsRepository.findAll()).thenReturn(Arrays.asList(returnedBudgets));
		Iterable<Budget> budgets = new BudgetController(budgetsRepository, userService).getBudgets();
		assertThat(budgets, hasItem(returnedBudgets));
	}*/

//	@Test
//	public void shouldRetrieveBudgetByYearThroughService() {
//		BudgetService budgetService = mock(BudgetService.class);
////		when(budgetService.get())
//	}
}