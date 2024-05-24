package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.BudgetReportsContract;
import org.mbs.budgetplannerserver.mapper.BudgetReportsContractMapper;
import org.mbs.budgetplannerserver.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ReportsController {

    private BudgetService budgetService;

    @Autowired
    public ReportsController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @RequestMapping(value = "/api/report/allBudgetsForCurrentYear", method = GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('superAdmin')")
    public List<BudgetReportsContract> findAllForCurrentYear() {
        return new BudgetReportsContractMapper().map(budgetService.getAllForFinancialYear());
    }
}
