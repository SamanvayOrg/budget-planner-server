package org.mbs.budgetplannerserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BudgetPlannerServerApplicationTest {

	@Autowired
	private BudgetPlannerServerApplication budgetPlannerServerApplication;

	@Test
	public void shouldStart() {
		budgetPlannerServerApplication.main(new String[]{});
	}

}