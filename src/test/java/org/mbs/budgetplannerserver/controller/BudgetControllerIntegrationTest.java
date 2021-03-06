package org.mbs.budgetplannerserver.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BudgetControllerIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private BudgetController budgetController;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

//	@Test
//	public void givenWac_whenServletContext_thenItProvidesGreetController() throws Exception {
//		this.mockMvc.perform(get("/api/budgets"))
//				.andDo(print())
//				.andExpect(status().isOk())
//				.andReturn();
//	}
//
//	@Test
//	public void shouldRetrieveBudgetByYear() throws Exception {
//		this.mockMvc.perform(get("/api/budget?year=2022"))
//				.andDo(print())
//				.andExpect(status().isOk())
//				.andReturn();
//	}
}
